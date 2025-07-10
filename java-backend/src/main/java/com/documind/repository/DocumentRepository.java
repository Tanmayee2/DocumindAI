package com.documind.repository;

import com.documind.model.Doc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends MongoRepository<Doc, String> {
    List<Doc> findByStatus(String status);
    List<Doc> findByUserId(String userId);
}