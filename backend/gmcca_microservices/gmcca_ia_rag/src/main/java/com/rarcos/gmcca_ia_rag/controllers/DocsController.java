package com.rarcos.gmcca_ia_rag.controllers;

import com.rarcos.gmcca_ia_rag.events.DocProcessEvent;
import com.rarcos.gmcca_ia_rag.model.dtos.FileData;
import com.rarcos.gmcca_ia_rag.model.enums.DocProcessStatus;
import com.rarcos.gmcca_ia_rag.services.StorageService;
import com.rarcos.gmcca_ia_rag.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/docs")
public class DocsController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final JdbcClient jdbcClient;
    private final StorageService storageService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("classpath:prompts/manual_product.ai.st")
    private Resource stPromptTemplate;

    public DocsController(ChatClient.Builder chatBuilder, VectorStore vectorStore, JdbcClient jdbcClient, StorageService storageService, KafkaTemplate<String, String> kafkaTemplate) {
        this.chatClient = chatBuilder
                .defaultAdvisors(new PromptChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
        this.vectorStore = vectorStore;
        this.jdbcClient = jdbcClient;
        this.storageService = storageService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestPart("file") MultipartFile file) {
        String resultMsgUpload = "";

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        //Send message to notification service
        this.kafkaTemplate.send("doc-process-topic", JsonUtils.toJson(
                new DocProcessEvent(file.getOriginalFilename(), DocProcessStatus.PROCESSING)
        ));

        if(!storageService.existFile(file.getOriginalFilename())){
            //Upload document into mongoDB
            log.info("Uploading docs into mongoDB");
            try {
                resultMsgUpload = storageService.uploadFile(file);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed while uploaded the document: " + file.getOriginalFilename());
            }
        }else{
            log.info("This file exist in mongoDB was uploaded previously");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This file was uploaded previously");
        }


        var count = jdbcClient.sql("SELECT count(*) FROM public.vector_store where metadata->>'file_name' = ?;")
                .param(file.getOriginalFilename())
                .query(Integer.class)
                .single();

        if(count == 0){
            //Load and process document into vectorStore pgVector DB
            log.info("Loading docs into vector store");
            var config = PdfDocumentReaderConfig.builder()
                    .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder()
                            .withNumberOfBottomTextLinesToDelete(0)
                            .withNumberOfTopTextLinesToDelete(0)
                            .build()
                    )
                    .withPagesPerDocument(1)
                    .build();

            var pdfReader = new PagePdfDocumentReader(file.getResource(), config);
            var result = pdfReader.get().stream()
                    .peek(doc -> log.info("Loading doc: {}", doc.getText()))
                    .toList();

            vectorStore.accept(result);

            log.info("Loaded {} docs into vector store", result.size());

            //Send message to notification service
            this.kafkaTemplate.send("doc-process-topic", JsonUtils.toJson(
                    new DocProcessEvent(file.getOriginalFilename(), DocProcessStatus.PROCESSED)
            ));

            return ResponseEntity.status(HttpStatus.OK).body("File uploaded and processed successfully: " + file.getOriginalFilename() + "\n" + resultMsgUpload);
        }else{
            log.info("This file exist in pgVector was uploaded previously");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This file was uploaded previously");
        }
    }

    @GetMapping("/download")
    private ResponseEntity<byte[]> downloadFile(@RequestParam String fileName){
        FileData fileData = storageService.downloadFile(fileName);
        if(fileData != null)
            return ResponseEntity.status(HttpStatus.OK)
                    //.contentType(MediaType.parseMediaType(fileData.getType()))
                    .contentType(MediaType.parseMediaType("application/pdf"))
                    .body(fileData.getContent());
        else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @GetMapping("/chat")
    private ResponseEntity<String> generateResponse(@RequestParam String query, @RequestParam String filename) {
        var docs = this.findSimilarDocuments(query, filename);
        if (docs.size() > 0) {
            PromptTemplate promptTemplate = new PromptTemplate(stPromptTemplate);
            var promptParameters = new HashMap<String, Object>();
            promptParameters.put("input", query);
            promptParameters.put("documents", String.join("\n", docs.stream().map(Document::getText).toList()));

            var prompt = promptTemplate.create(promptParameters);
            var response = this.chatClient.prompt(prompt).call().chatResponse();
            return ResponseEntity.status(HttpStatus.OK).body(response.getResult().getOutput().getText() + formattedMetadata(docs.stream().map(Document::getMetadata).toList()));
        }else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    private List<Document> findSimilarDocuments(String query, String filename) {
        return vectorStore.similaritySearch(
                        SearchRequest.builder()
                            .query(query)
                            .topK(3)
                            .similarityThreshold(0.5)
                            .filterExpression(new FilterExpressionBuilder().eq("file_name", filename).build())
                            .build());
    }

    private String formattedMetadata(List<Map<String, Object>> metadata){
        StringBuilder metadataFormated = new StringBuilder("Extraído del documento " + metadata.get(0).get("file_name") + " de las páginas ");
        for (Map<String, Object> m : metadata){
            metadataFormated.append(m.get("page_number").toString()).append(", ");
        }
        metadataFormated.delete(metadataFormated.length()-2, metadataFormated.length());
        return "\n\n-----\n" + metadataFormated;
    }

}
