package com.documind.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chunks")
public class ChunkEntity {
    @Id
    private String id;
    private String documentId;
    private String content;
    private Integer chunkIndex;
    private Integer startPosition;
    private Integer endPosition;
}