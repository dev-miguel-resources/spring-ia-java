package com.ia.agentsia.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/etls")
@RequiredArgsConstructor
public class ETLController {

    private static final Logger log = LoggerFactory.getLogger(ETLController.class);

    private final ChromaVectorStore vectorStore;

    private final OpenAiChatModel openAiChatModel;

    @PostConstruct
    public void initializeAndStorePDF() {

        Thread.startVirtualThread(() -> {
            try {
                log.info("Iniciando la carga única del PDF al arrancar el servidor...");

                PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(
                        "classpath:/pdf/sample2.pdf",
                        PdfDocumentReaderConfig.builder()
                                .withPageTopMargin(0)
                                .withPageExtractedTextFormatter(
                                        ExtractedTextFormatter.builder()
                                                .withNumberOfTopTextLinesToDelete(0).build())
                                .withPagesPerDocument(1).build());
                vectorStore.add(pdfReader.read());
            } catch (Exception e) {
                log.error("Alerta: No se pudo indexar el PDF en Chroma (Verifica tu saldo o API Key de OpenAI)");
                e.getMessage();
            }
        });
    }

    @GetMapping("/pdf")
    public ResponseEntity<String> etlPDF(@RequestParam String message) {

        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .topK(2)
                        .build());

        if (results.isEmpty()) {
            return ResponseEntity.ok("No se encontró información relevante en el documento");
        }

        String information = results.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        PromptTemplate promptTemplate = new PromptTemplate(
                "Responde esta pregunta: {message} con la siguiente información: {information}");

        Prompt prompt = promptTemplate.create(Map.of(
                "message", message,
                "information", information));

        ChatResponse chatResponse = openAiChatModel.call(prompt);

        String iaResult = chatResponse.getResult().getOutput().getText();

        return ResponseEntity.ok(iaResult);

    }

    @PostMapping("upload")
    public ResponseEntity<String> etlFromFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("message") String message) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo está vacío");
        }

        File tempFile = null;

        try {
            tempFile = File.createTempFile("upload-", ".pdf");
            file.transferTo(tempFile);

            List<String> textChunks = extractPdfChunks(tempFile, 1500);

            String fileId = "current_pdf_session";

            List<Document> documentToEmbed = textChunks.stream()
                    .map(chunk -> {
                        return new Document(chunk, Map.of("file_id", fileId));
                    })
                    .collect(Collectors.toList());

            try {
                List<Document> antiguos = vectorStore.similaritySearch(
                        SearchRequest.builder().query("*").topK(100).build());
                if (!antiguos.isEmpty()) {
                    List<String> idsParaBorrar = antiguos.stream().map(Document::getId).collect(Collectors.toList());
                    vectorStore.delete(idsParaBorrar);
                }
            } catch (Exception e) {
                log.warn("No se requería limpieza previa o la colección estaba vacía");
            }

            vectorStore.add(documentToEmbed);

            List<Document> results = vectorStore
                    .similaritySearch(SearchRequest.builder().query(message).topK(1).build());

            if (results.isEmpty()) {
                return ResponseEntity.ok("No se encontró información relevante en el PDF.");
            }

            String information = results.get(0).getText();

            PromptTemplate promptTemplate = new PromptTemplate(
                    "Responde esta pregunta: {message} con la siguiente informacion: {information}");

            Prompt prompt = promptTemplate.create(Map.of(
                    "message", message,
                    "information", information));

            ChatResponse response = openAiChatModel.call(prompt);

            String iaResult = response.getResult().getOutput().getText();

            return ResponseEntity.ok(iaResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al procesar el archivo: " + e.getMessage());
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }

    }

    private List<String> extractPdfChunks(File file, int chunckSize) throws IOException {

        List<String> chunks = new ArrayList<>();

        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(file)) {

            PDFTextStripper stripper = new PDFTextStripper();

            String text = stripper.getText(document);

            text = text.replaceAll("\\s+", " ").trim();

            int start = 0;

            while (start < text.length()) {
                int end = start + chunckSize;

                if (end >= text.length()) {
                    chunks.add(text.substring(start));

                    break;
                }

                int lastSpace = text.lastIndexOf(' ', end);

                if (lastSpace > start) {
                    end = lastSpace;
                }

                chunks.add(text.substring(start, end).trim());

                start = end + 1;
            }

        }

        return chunks;
    }

}
