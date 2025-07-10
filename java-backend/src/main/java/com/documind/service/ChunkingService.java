package com.documind.service;

import com.documind.model.Chunk;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChunkingService {
    
    private static final int CHUNK_SIZE = 500; // words
    private static final int OVERLAP_SIZE = 50; // words
    
    public List<Chunk> chunkText(String documentId, String text) {
        String[] words = text.split("\\s+");
        List<Chunk> chunks = new ArrayList<>();
        
        int index = 0;
        int position = 0;
        
        while (position < words.length) {
            int endPosition = Math.min(position + CHUNK_SIZE, words.length);
            
            StringBuilder chunkContent = new StringBuilder();
            for (int i = position; i < endPosition; i++) {
                chunkContent.append(words[i]).append(" ");
            }
            
            Chunk chunk = new Chunk();
            chunk.setDocumentId(documentId);
            chunk.setContent(chunkContent.toString().trim());
            chunk.setChunkIndex(index);
            chunk.setStartPosition(position);
            chunk.setEndPosition(endPosition);
            
            chunks.add(chunk);
            
            position = endPosition - OVERLAP_SIZE;
            if (position < 0) position = endPosition;
            index++;
        }
        
        return chunks;
    }
}