package com.ia.agentsia.controllers;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ia.agentsia.dtos.ResponseDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/transcripts")
@RequiredArgsConstructor
public class TranscriptionController {

    private final OpenAiAudioTranscriptionModel transcriptionModel;

    @GetMapping("/es")
    public ResponseEntity<String> transcriptES() {

        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .language("es")
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .temperature(0.8f) // mientras el valor sea más cercano a 1 le das mayor capacidad de pensar
                .build();

        // Cargar un archivo de audio como recurso fijo
        ClassPathResource audioFile = new ClassPathResource("/audios/es_audio1.flac");

        // Crear el prompt
        AudioTranscriptionPrompt transcriptionPrompt = new AudioTranscriptionPrompt(audioFile, options);

        // Ejecutar la transcripción
        AudioTranscriptionResponse response = transcriptionModel.call(transcriptionPrompt);

        return ResponseEntity.ok(response.getResult().getOutput());
    }

    @GetMapping("/en")
    public ResponseEntity<String> transcriptEN() {

        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .language("en")
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .temperature(0.8f) // mientras el valor sea más cercano a 1 le das mayor capacidad de pensar
                .build();

        // Cargar un archivo de audio como recurso fijo
        ClassPathResource audioFile = new ClassPathResource("/audios/en_audio2.flac");

        // Crear el prompt
        AudioTranscriptionPrompt transcriptionPrompt = new AudioTranscriptionPrompt(audioFile, options);

        // Ejecutar la transcripción
        AudioTranscriptionResponse response = transcriptionModel.call(transcriptionPrompt);

        return ResponseEntity.ok(response.getResult().getOutput());
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO<String>> handleAudioUpload(@RequestParam("audio") MultipartFile audioFile) {

        try {
            // Definir la ruta donde se almacenarán los archivos subidos
            String uploadDirPath = "src/main/resources/audios/uploads/";

            // Crear el directorio si no existe
            Path uploadPath = Paths.get(uploadDirPath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generar un nombre único para cada archivo (timestamp)
            String filename = "audio_" + System.currentTimeMillis() + ".wav";
            Path filePath = uploadPath.resolve(filename);

            // Guardar el archivo recibido en el servidor
            Files.copy(audioFile.getInputStream(), filePath);

            OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                    .language("es")
                    .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                    .temperature(0.2f) // mientras el valor sea más cercano a 1 le das mayor capacidad de pensar
                    .build();

            Resource audioFileUploaded = new FileSystemResource(uploadDirPath + filename);

            // Crear el prompt con el archivo subido
            AudioTranscriptionPrompt transcriptionPrompt = new AudioTranscriptionPrompt(audioFileUploaded, options);

            AudioTranscriptionResponse response = transcriptionModel.call(transcriptionPrompt);

            return ResponseEntity.ok(new ResponseDTO<>(200, "success", response.getResult().getOutput()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO<List<Map<String, String>>>> handleMultipleAudioUpload(
            @RequestParam("audios") List<MultipartFile> audioFiles) {

        try {
            // Definir la ruta donde se almacenarán los archivos subidos
            String uploadDirPath = "src/main/resources/audios/uploads/";

            // Crear el directorio si no existe
            Path uploadPath = Paths.get(uploadDirPath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Definir una lista para los resultados individuales
            List<Map<String, String>> results = new ArrayList<>();

            for (MultipartFile audioFile : audioFiles) {
                String filename = "audio_" + System.currentTimeMillis() + "_" + audioFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(filename);

                // Guardar el archivo recibido en el servidor
                Files.copy(audioFile.getInputStream(), filePath);

                OpenAiAudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
                        .language("es")
                        .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                        .temperature(0.2f) // mientras el valor sea más cercano a 1 le das mayor capacidad de pensar
                        .build();

                Resource audioFileUploaded = new FileSystemResource(uploadDirPath + filename);

                // Crear el prompt con el archivo subido
                AudioTranscriptionPrompt transcriptionRequest = new AudioTranscriptionPrompt(audioFileUploaded,
                        transcriptionOptions);

                AudioTranscriptionResponse response = transcriptionModel.call(transcriptionRequest);

                // Guardar el resultado individual en un mapa
                Map<String, String> audioResult = new HashMap<>();
                audioResult.put("filename", audioFile.getOriginalFilename());
                audioResult.put("transcription", response.getResult().getOutput());

                results.add(audioResult);

            }

            return ResponseEntity.ok(new ResponseDTO<>(200, "success", results));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
