package com.documind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private String id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String status;
    private LocalDateTime uploadedAt;
    private LocalDateTime processedAt;
    private Integer chunkCount;
}