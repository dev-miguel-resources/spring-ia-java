package com.ia.agentsia.config;

import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChromaConfig {

    @Bean
    public VectorStore chromaVectorStore(EmbeddingModel embeddingModel, ChromaApi chromaApi) {

        return ChromaVectorStore.builder(chromaApi, embeddingModel)
                .collectionName("SpringAiCollection")
                .initializeSchema(true)
                .build();
    }

}
