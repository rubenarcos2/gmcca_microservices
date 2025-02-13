package com.rarcos.gmcca_ia_rag.services;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.rarcos.gmcca_ia_rag.model.dtos.FileData;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class StorageService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations operations;

    public String uploadFile(MultipartFile file) throws IOException{
        log.info("Start storage of file");

        DBObject metaData = new BasicDBObject();
        metaData.put("type", "pdf");
        metaData.put("filename", file.getOriginalFilename());
        ObjectId id = gridFsTemplate.store(
                file.getInputStream(), file.getName(), file.getContentType(), metaData);

        log.info("End storage of file");

        return "File storage successfully: " + file.getOriginalFilename() + " with ID: " + id.toString();
    }

    public FileData downloadFile(String fileName){
        log.info("Start download file {}", fileName);

        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("metadata.filename").is(fileName)));
        try {
            return new FileData(file.getId().toString(), file.getMetadata().getString("filename"), file.getMetadata().getString("_contentType"), operations.getResource(file).getContentAsByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existFile(String fileName){
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("metadata.filename").is(fileName)));
        return file != null;
    }
}
