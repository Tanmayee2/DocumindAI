package com.documind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryResponse {
    private String answer;
    private List<SourceDto> sources;
    private String model;
    private Map<String, Object> hallucinationCheck;
    private Map<String, Integer> usage;
}