package com.documind.service;

import com.documind.client.AIServiceClient;
import com.documind.dto.DocumentResponse;
import com.documind.dto.DocumentUploadResponse;
import com.documind.model.Chunk;
import com.documind.model.DocumentEntity;  // ← Updated import
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
            DocumentEntity document = new DocumentEntity();  // ← Updated
            document.setFileName(file.getOriginalFilename());
            document.setFileType(file.getContentType());
            document.setFileSize(file.getSize());
            document.setStatus("PROCESSING");
            document.setUploadedAt(LocalDateTime.now());
            document.setUserId("default");
            
            document = documentRepository.save(document);
            
            // Extract text
            log.info("Extracting text from document: {}", document.getId());
            String extractedText = parserService.extractText(file);
            
            // Chunk the text
            log.info("Chunking document: {}", document.getId());
            List<Chunk> chunks = chunkingService.chunkText(document.getId(), extractedText);
            
            // Save chunks
            chunks = chunkRepository.saveAll(chunks);
            
            // Update document with chunk info
            document.setChunkCount(chunks.size());
            document.setChunkIds(chunks.stream().map(Chunk::getId).collect(Collectors.toList()));
            document.setStatus("COMPLETED");
            document.setProcessedAt(LocalDateTime.now());
            documentRepository.save(document);
            
            // Index document in AI service
            aiServiceClient.indexDocument(document.getId()).subscribe();
            
            log.info("Document processed successfully: {}", document.getId());
            
            return new DocumentUploadResponse(
                document.getId(),
                document.getFileName(),
                document.getStatus(),
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
    
    private DocumentResponse toDocumentResponse(DocumentEntity document) {  // ← Updated parameter
        return new DocumentResponse(
            document.getId(),
            document.getFileName(),
            document.getFileType(),
            document.getFileSize(),
            document.getStatus(),
            document.getUploadedAt(),
            document.getProcessedAt(),
            document.getChunkCount()
        );
    }
}