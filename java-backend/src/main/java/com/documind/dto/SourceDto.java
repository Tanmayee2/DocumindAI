package com.documind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourceDto {
    private String chunkId;
    private String content;
    private String documentId;
    private Double relevanceScore;
}