package com.documind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryRequest {
    private String query;
    private String documentId;
    private Integer maxResults = 5;
}