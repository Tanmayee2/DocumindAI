package com.documind.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// import javax.persistence.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "documents")
public class DocumentEntity {
    @Id
    private String id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String status; // PROCESSING, COMPLETED, FAILED
    private LocalDateTime uploadedAt;
    private LocalDateTime processedAt;
    private Integer chunkCount;
    private List<String> chunkIds;
    private String userId;
}