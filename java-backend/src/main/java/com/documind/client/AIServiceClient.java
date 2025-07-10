package com.documind.client;

import com.documind.dto.QueryRequest;
import com.documind.dto.QueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AIServiceClient {
    
    private final WebClient webClient;
    
    public AIServiceClient(@Value("${ai.service.url}") String aiServiceUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(aiServiceUrl)
            .build();
    }
    
    public Mono<Void> indexDocument(String documentId) {
        Map<String, String> request = new HashMap<>();
        request.put("document_id", documentId);
        
        return webClient.post()
            .uri("/api/query/index")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Void.class)
            .doOnSuccess(v -> log.info("Document indexed successfully: {}", documentId))
            .doOnError(e -> log.error("Error indexing document: {}", e.getMessage()))
            .onErrorResume(e -> Mono.empty());
    }
    
    public Mono<QueryResponse> askQuestion(QueryRequest queryRequest) {
        return webClient.post()
            .uri("/api/query/ask")
            .bodyValue(queryRequest)
            .retrieve()
            .bodyToMono(QueryResponse.class)
            .doOnError(e -> log.error("Error querying AI service: {}", e.getMessage()));
    }
}