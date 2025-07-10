package com.documind.service;

import com.documind.client.AIServiceClient;
import com.documind.dto.DocumentResponse;
import com.documind.dto.DocumentUploadResponse;
import com.documind.model.ChunkEntity;
import com.documind.model.DocumentEntity;
import com.documind.repository.ChunkRepository;
import com.documind.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {
    
    private final DocumentRepository documentRepository;
    private final ChunkRepository chunkRepository;
    private final DocumentParserService parserService;
    private final ChunkingService chunkingService;
    private final AIServiceClient aiServiceClient;
    
    public DocumentUploadResponse uploadDocument(MultipartFile file) {
        try {
            // Create document record
            DocumentEntity documentEntity = new DocumentEntity();
            documentEntity.setFileName(file.getOriginalFilename());
            documentEntity.setFileType(file.getContentType());
            documentEntity.setFileSize(file.getSize());
            documentEntity.setStatus("PROCESSING");
            documentEntity.setUploadedAt(LocalDateTime.now());
            documentEntity.setUserId("default");
            
            documentEntity = documentRepository.save(documentEntity);
            
            // Extract text
            log.info("Extracting text from document: {}", documentEntity.getId());
            String extractedText = parserService.extractText(file);
            
            // Chunk the text
            log.info("Chunking document: {}", documentEntity.getId());
            List<ChunkEntity> chunks = chunkingService.chunkText(documentEntity.getId(), extractedText);
            
            // Save chunks
            chunks = chunkRepository.saveAll(chunks);
            
            // Update document with chunk info
            documentEntity.setChunkCount(chunks.size());
            documentEntity.setChunkIds(chunks.stream().map(ChunkEntity::getId).collect(Collectors.toList()));
            documentEntity.setStatus("COMPLETED");
            documentEntity.setProcessedAt(LocalDateTime.now());
            documentRepository.save(documentEntity);
            
            // Index document in AI service (async)
            aiServiceClient.indexDocument(documentEntity.getId()).subscribe();
            
            log.info("Document processed successfully: {}", documentEntity.getId());
            
            return new DocumentUploadResponse(
                documentEntity.getId(),
                documentEntity.getFileName(),
                documentEntity.getStatus(),
                "Document uploaded and processed successfully"
            );
            
        } catch (Exception e) {
            log.error("Error processing document", e);
            return new DocumentUploadResponse(
                null,
                file.getOriginalFilename(),
                "FAILED",
                "Error: " + e.getMessage()
            );
        }
    }
    
    public List<DocumentResponse> getAllDocuments() {
        return documentRepository.findAll().stream()
            .map(this::toDocumentResponse)
            .collect(Collectors.toList());
    }
    
    public DocumentResponse getDocumentById(String id) {
        return documentRepository.findById(id)
            .map(this::toDocumentResponse)
            .orElse(null);
    }
    
    private DocumentResponse toDocumentResponse(DocumentEntity entity) {
        return new DocumentResponse(
            entity.getId(),
            entity.getFileName(),
            entity.getFileType(),
            entity.getFileSize(),
            entity.getStatus(),
            entity.getUploadedAt(),
            entity.getProcessedAt(),
            entity.getChunkCount()
        );
    }
}