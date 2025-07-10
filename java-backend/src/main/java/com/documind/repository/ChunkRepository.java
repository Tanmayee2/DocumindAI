package com.documind.repository;

import com.documind.model.ChunkEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChunkRepository extends MongoRepository<ChunkEntity, String> {
    List<ChunkEntity> findByDocumentId(String documentId);
}