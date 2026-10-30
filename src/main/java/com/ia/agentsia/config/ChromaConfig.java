package com.ia.agentsia.config;

import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ChromaConfig {

    @Bean
    @Primary
    public VectorStore chromaVectorStore(EmbeddingModel embeddingModel, ChromaApi chromaApi) {
        return ChromaVectorStore.builder(chromaApi, embeddingModel)
                .collectionName("SpringAiCollection") // Tu colección oficial para los PDFs
                .initializeSchema(true)
                .build();
    }

    @Bean(name = "pruebasVectorStore")
    public VectorStore pruebasVectorStore(EmbeddingModel embeddingModel, ChromaApi chromaApi) {
        return ChromaVectorStore.builder(chromaApi, embeddingModel)
                .collectionName("PruebasLocalCollection") // Collection separada de los PDFS
                .initializeSchema(true)
                .build();
    }

}
