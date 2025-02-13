package com.rarcos.gmcca_ia_rag.repositories;

import com.rarcos.gmcca_ia_rag.model.dtos.FileData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StorageRepository extends MongoRepository<FileData, String> {
    Optional<FileData> findByName(String fileName);
}
