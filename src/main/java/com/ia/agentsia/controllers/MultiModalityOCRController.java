package com.ia.agentsia.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ia.agentsia.dtos.ResponseDTO;

import lombok.RequiredArgsConstructor;

// OCR: Reconocimiento Óptico de caracteres
// Multimodality: Combinación de múltiples agentes
@RestController
@RequestMapping("/multis")
@RequiredArgsConstructor
public class MultiModalityOCRController {

        private final OpenAiChatModel openAiChatModel;
        private final OpenAiImageModel openAiImageModel;

        @GetMapping()
        public ResponseEntity<ResponseDTO<String>> multimodality() {

                try {
                        ClassPathResource classPathResource = new ClassPathResource("/images/image1.png");

                        UserMessage userMessage = UserMessage.builder()
                                        .text("¿Explícame que ves en esta imagen? e invéntame una historia alrededor de ello")
                                        .media(new Media(MimeTypeUtils.IMAGE_JPEG, classPathResource))
                                        .build();

                        ChatResponse chatResponse = openAiChatModel.call(new Prompt(List.of(userMessage)));
                        String result = chatResponse.getResult().getOutput().getText();

                        return ResponseEntity.ok(new ResponseDTO<>(200, "success", result));
                } catch (Exception e) {
                        return ResponseEntity.status(500)
                                        .body(new ResponseDTO<>(500, "Fallo técnico al leer el recurso local", null));
                }

        }

        @GetMapping("/image")
        public ResponseEntity<ResponseDTO<String>> multimodalityURL() {

                try {

                        UserMessage userMessage = UserMessage.builder()
                                        .text("¿Explícame que ves en esta imagen? y si pertenece a una película")
                                        .media(new Media(MimeTypeUtils.IMAGE_JPEG, new URI(
                                                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRS3T1h_QdpupQfP2M5Sk5rfAjlRNJAP7CPVjSAhVusqK3s5-eXqGB6Z48K&s=10")
                                                        .toURL().toURI()))
                                        .build();

                        ChatResponse chatResponse = openAiChatModel.call(new Prompt(List.of(userMessage)));
                        String result = chatResponse.getResult().getOutput().getText();

                        return ResponseEntity.ok(new ResponseDTO<>(200, "success", result));
                } catch (Exception e) {
                        return ResponseEntity.status(500)
                                        .body(new ResponseDTO<>(500,
                                                        "Error de infraestructura al descargar la imagen de internet.",
                                                        null));
                }

        }

        @GetMapping("/image2")
        public ResponseEntity<ResponseDTO<String>> multimodalityURL2() {

                try {

                        UserMessage userMessage = UserMessage.builder()
                                        .text("¿Explícame que ves en cada imagen? dime si es de una película e inventa una historia de ambos")
                                        .media(new Media(
                                                        MimeTypeUtils.IMAGE_JPEG, new URI(
                                                                        "https://m.media-amazon.com/images/M/MV5BMzk2YzRjYmItNTA5Mi00ODczLTljYzUtOWE1ZDFkODhlZWJlXkEyXkFqcGc@._V1_.jpg")
                                                                        .toURL().toURI()),
                                                        new Media(
                                                                        MimeTypeUtils.IMAGE_JPEG, new URI(
                                                                                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTqLdyPRfxa9W67m7yZa28PP3Ojgy9vKQJ0453bK1XHCpzpn8Z3NAMJm8o&s=10")
                                                                                        .toURL().toURI()))
                                        .build();

                        ChatResponse chatResponse = openAiChatModel.call(new Prompt(List.of(userMessage)));
                        String result = chatResponse.getResult().getOutput().getText();

                        return ResponseEntity.ok(new ResponseDTO<>(200, "success", result));
                } catch (Exception e) {
                        return ResponseEntity.status(500)
                                        .body(new ResponseDTO<>(500, "Fallo al procesar el lote de imágenes", null));
                }

        }

        @PostMapping("/upload")
        public ResponseEntity<ResponseDTO<String>> multiModalityUpload(@RequestParam("image") MultipartFile imageFile) {

                try {
                        // Paso 1: Análisis de visión inteligente (OCR)
                        UserMessage userMessage = UserMessage.builder()
                                        .text("Explicame que ves en esta imagen? e invéntame una historia corta para un libro")
                                        .media(List.of(new Media(MimeTypeUtils.IMAGE_JPEG,
                                                        new ByteArrayResource(imageFile.getBytes()))))
                                        .build();

                        ChatResponse chatResponse = openAiChatModel.call(new Prompt(List.of(userMessage)));
                        String description = chatResponse.getResult().getOutput().getText();

                        // Apartado de seguridad: interceptar las respuestas de cortesía de rechazo de
                        // OpenAI
                        String descrLower = description.toLowerCase();
                        if (descrLower.contains("no puede ayudar") ||
                                        descrLower.contains("lo siento") ||
                                        descrLower.contains("no puede procesar") ||
                                        descrLower.contains("inapropiado") ||
                                        descrLower.contains("no es posible cumplir")) {

                                System.err.println(
                                                "Alerta: Filtro preventivo activado. GPT-4o rechazó describir el archivo por políticas.");
                                return ResponseEntity.badRequest().body(new ResponseDTO<>(400,
                                                "La imagen subida infringe las políticas de moderación de contenido y fue bloqueada en la entrada",
                                                null));
                        }

                        // Paso 2: Si pasa el filtro, procedemos a diseñar la portada de un libro usando
                        // gpt-image-2
                        var imageResponse = openAiImageModel.call(
                                        new ImagePrompt(
                                                        "Generame una portada artística basada estrictamente en esta historia: "
                                                                        + description,
                                                        OpenAiImageOptions.builder()
                                                                        .model("gpt-image-2")
                                                                        .quality("low")
                                                                        .N(1)
                                                                        .height(1024)
                                                                        .width(1024)
                                                                        .build()));

                        // Paso 3: Extraer el base64 para que podamos inyectarlo en el visualizer del
                        // postman
                        String base64Image = "data:image/png;base64,"
                                        + imageResponse.getResult().getOutput().getB64Json();

                        return ResponseEntity.ok(new ResponseDTO<>(200, "success", base64Image));

                } catch (org.springframework.ai.retry.NonTransientAiException e) {
                        String errorMessage = e.getMessage() != null ? e.getMessage() : "";
                        if (errorMessage.contains("moderation_blocked") || errorMessage.contains("policy")) {
                                return ResponseEntity.badRequest().body(new ResponseDTO<>(400,
                                                "La historia generada por el modelo infringió las políticas de salida en gpt-image-2",
                                                null));

                        }
                        return ResponseEntity.status(500)
                                        .body(new ResponseDTO<>(500,
                                                        "Fallo de infraestructura de los servicios de OpenAI.", null));
                } catch (Exception e) {
                        return ResponseEntity.status(500)
                                        .body(new ResponseDTO<>(500,
                                                        "Error interno del servidor al procesar la carga binaria",
                                                        null));
                }
        }

}
