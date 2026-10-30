package com.ia.agentsia.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/embeddings")
public class EmbeddingController {

    private final OpenAiEmbeddingModel openAiEmbeddingModel;

    private final VectorStore pruebasVectorStore;

    public EmbeddingController(
            OpenAiEmbeddingModel openAiEmbeddingModel,
            @Qualifier("pruebasVectorStore") VectorStore pruebasVectorStore) {
        this.openAiEmbeddingModel = openAiEmbeddingModel;
        this.pruebasVectorStore = pruebasVectorStore;

    }

    @GetMapping("/generate")
    public Map<String, EmbeddingResponse> generate(@RequestParam String message) {

        EmbeddingResponse response = openAiEmbeddingModel.embedForResponse(List.of(message));

        return Map.of("embedding", response);

    }

    @GetMapping("/vectorstore")
    public List<Document> useVectorStore(@RequestParam String message) {

        List<Document> documents = List.of(
                new Document("Spring AI es lo máximo", Map.of("meta-id-1", "meta-value1")),
                new Document("Python es más popular en IA"),
                new Document("El futuro de la inteligencia artificial", Map.of("meta-id-2", "meta-value2")));

        pruebasVectorStore.add(documents);

        List<Document> results = pruebasVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .topK(1)
                        .build());

        return results;
    }

}
