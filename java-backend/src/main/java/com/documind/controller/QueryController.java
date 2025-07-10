package com.documind.controller;

import com.documind.client.AIServiceClient;
import com.documind.dto.QueryRequest;
import com.documind.dto.QueryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/query")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QueryController {
    
    private final AIServiceClient aiServiceClient;
    
    @PostMapping("/ask")
    public Mono<ResponseEntity<QueryResponse>> askQuestion(@RequestBody QueryRequest request) {
        return aiServiceClient.askQuestion(request)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}