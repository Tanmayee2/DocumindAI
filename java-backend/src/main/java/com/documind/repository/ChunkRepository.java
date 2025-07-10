package com.documind.repository;

import com.documind.model.Chunk;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChunkRepository extends MongoRepository<Chunk, String> {
    List<Chunk> findByDocumentId(String documentId);
}