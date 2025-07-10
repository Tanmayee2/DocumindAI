package com.documind.repository;

import com.documind.model.DocumentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends MongoRepository<DocumentEntity, String> {
    List<DocumentEntity> findByStatus(String status);
    List<DocumentEntity> findByUserId(String userId);
}