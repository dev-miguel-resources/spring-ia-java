package com.ia.agentsia.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ia.agentsia.dtos.ResponseDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final OpenAiImageModel openAiImageModel;

    @GetMapping("/generate")
    public ResponseEntity<ResponseDTO<String>> generateImage(@RequestParam String param) {

        ImageResponse imageResponse = openAiImageModel.call(new ImagePrompt(param,
                OpenAiImageOptions.builder()
                        .model("gpt-image-1") // 1. Usamos el modelo sucesor oficial de OpenAI
                        .quality("low") // 2. Calidad estándar (recuerda evitar 'low')
                        .N(1)
                        .height(1024)
                        .width(1024)
                        .build()));

        // Obtenemos la cadena Base64 generada por el nuevo modelo
        String base64Image = imageResponse.getResult().getOutput().getB64Json();

        // Retornamos de forma exitosa
        // return ResponseEntity.ok(new ResponseDTO<>(200, "success", base64Image));

        return ResponseEntity.ok(new ResponseDTO<>(200, "success", "data:image/png;base64," + base64Image));
    }

    @GetMapping("/generate2")
    public ResponseEntity<ResponseDTO<List<String>>> generateImage2(
            @RequestParam String param,
            @RequestParam(defaultValue = "1") int n) {

        try {
            ImageResponse imageResponse = openAiImageModel.call(new ImagePrompt(param,
                    OpenAiImageOptions.builder()
                            .model("gpt-image-1") // 1. Usamos el modelo sucesor oficial de OpenAI
                            .quality("low") // 2. Calidad estándar (recuerda evitar 'low')
                            .N(n)
                            .height(1024)
                            .width(1024)
                            .build()));

            // Extraer todas los resultados de la lista de imágenes en base 64
            List<String> imagesBase64 = imageResponse.getResults().stream()
                    .map(result -> "data:image/png;base64," + result.getOutput().getB64Json())
                    .collect(Collectors.toList());

            // Retornamos de forma exitosa
            return ResponseEntity.ok(new ResponseDTO<>(200, "success", imagesBase64));

        } catch (org.springframework.ai.retry.NonTransientAiException e) {

            String errorMessage = e.getMessage() != null ? e.getMessage() : "";

            // exepciones por moderación
            if (errorMessage.contains("moderation_blocked") || errorMessage.contains("policy")) {
                System.err.println("Bloqueo de seguridad OpenAI: " + errorMessage);

                return ResponseEntity.badRequest().body(new ResponseDTO<>(400,
                        "El prompt enviado infringe las políticas de contenido de la IA.",
                        List.of()));

            }
            // Si es otro error de la API (clave inválida, falta de saldo, etc...)
            System.err.println("Falla de infraestructura OpenAI: " + errorMessage);
            return ResponseEntity.status(500).body(new ResponseDTO<>(500,
                    "El servicio no se encuentra disponible debido a un problema de autenticación o infraestructura.",
                    List.of()));
        } catch (Exception e) {
            System.err.println("Falla técnico general: " + e.getMessage());
            return ResponseEntity.status(500).body(new ResponseDTO<>(500,
                    "El servicio de generación no está disponible en estos momentos debido a un problema técnico interno.",
                    List.of()));
        }

    }

    @GetMapping("/generate3")
    public ResponseEntity<ResponseDTO<List<String>>> generateImage3(
            @RequestParam String param,
            @RequestParam(defaultValue = "1") int n) {

        // 1. Seguro de saldo: Controlar el flujo de la cantidad de datos enviados
        if (n > 3) {
            n = 3;
        }

        try {
            ImageResponse imageResponse = openAiImageModel.call(new ImagePrompt(param,
                    OpenAiImageOptions.builder()
                            .model("gpt-image-2") // 1. Usamos el modelo sucesor oficial de OpenAI
                            .quality("low") // 2. Calidad estándar (recuerda evitar 'low')
                            .N(n)
                            .height(1024)
                            .width(1024)
                            .build()));

            // Extraer todas los resultados de la lista de imágenes en base 64
            List<String> imagesBase64 = imageResponse.getResults().stream()
                    .map(result -> "data:image/png;base64," + result.getOutput().getB64Json())
                    .collect(Collectors.toList());

            // Retornamos de forma exitosa
            return ResponseEntity.ok(new ResponseDTO<>(200, "success", imagesBase64));

        } catch (org.springframework.ai.retry.NonTransientAiException e) {

            String errorMessage = e.getMessage() != null ? e.getMessage() : "";

            // exepciones por moderación
            if (errorMessage.contains("moderation_blocked") || errorMessage.contains("policy")) {
                System.err.println("Bloqueo de seguridad OpenAI: " + errorMessage);

                return ResponseEntity.badRequest().body(new ResponseDTO<>(400,
                        "El prompt enviado infringe las políticas de contenido de la IA.",
                        List.of()));

            }
            // Si es otro error de la API (clave inválida, falta de saldo, etc...)
            System.err.println("Falla de infraestructura OpenAI (API Key/Saldo): " + errorMessage);
            return ResponseEntity.status(500).body(new ResponseDTO<>(500,
                    "El servicio no se encuentra disponible debido a un problema de autenticación o infraestructura.",
                    List.of()));
        } catch (Exception e) {
            System.err.println("Falla técnico general: " + e.getMessage());
            return ResponseEntity.status(500).body(new ResponseDTO<>(500,
                    "El servicio de generación no está disponible en estos momentos debido a un problema técnico interno.",
                    List.of()));
        }

    }

}
