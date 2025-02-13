package com.rarcos.gmcca_notifications.listeners;

import com.rarcos.gmcca_notifications.events.DocProcessEvent;
import com.rarcos.gmcca_notifications.model.dtos.Product;
import com.rarcos.gmcca_notifications.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class DocProcessEventListener {

    private final WebClient.Builder webClientBuilder;
    private final SimpMessagingTemplate messagingTemplate;

    public DocProcessEventListener(WebClient.Builder webClientBuilder, SimpMessagingTemplate messagingTemplate) {
        this.webClientBuilder = webClientBuilder;
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics = "doc-process-topic")
    public void handleDocProcessNotifications(String message) {
        var docProcessEvent = JsonUtils.fromJson(message, DocProcessEvent.class);

        switch (docProcessEvent.docProcessStatus()) {
            case PROCESSING ->
                    messagingTemplate.convertAndSend("/topic/messages", "El producto con código " + docProcessEvent.fileName().substring(0, docProcessEvent.fileName().length() - 4) + " está procesando su manual");
            case PROCESSED ->
                    messagingTemplate.convertAndSend("/topic/messages", "El producto con código " + docProcessEvent.fileName().substring(0, docProcessEvent.fileName().length() - 4) + " tiene disponible su manual");
        }

        log.info("Status of process product document FILENAME: {}, STATUS: {}", docProcessEvent.fileName(), docProcessEvent.docProcessStatus());

        Product productByCode = this.webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port("8083")
                        .path("/api/product/code")
                        .queryParam("code", docProcessEvent.fileName().substring(0, docProcessEvent.fileName().length() - 4))
                        .build())
                .retrieve()
                .bodyToMono(Product.class)
                .block();

        if (productByCode != null) {
            String resultProductStatus = this.webClientBuilder.build()
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("localhost")
                            .port("8083")
                            .path("/api/product/status")
                            .queryParam("code", docProcessEvent.fileName().substring(0, docProcessEvent.fileName().length() - 4))
                            .queryParam("status", docProcessEvent.docProcessStatus())
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            //messagingTemplate.convertAndSend("/topic/messages", "El producto con código " + docProcessEvent.fileName().substring(0, docProcessEvent.fileName().length() - 4) + " está disponible");
            log.info("Response to API product, change status: {}", docProcessEvent.docProcessStatus());
        } else {
            messagingTemplate.convertAndSend("/topic/messages", "No existe un producto con el código " + docProcessEvent.fileName().substring(0, docProcessEvent.fileName().length() - 4));
            log.info("Not exist a product with this code: {}", docProcessEvent.fileName().substring(0, docProcessEvent.fileName().length() - 4));
        }
    }
}
