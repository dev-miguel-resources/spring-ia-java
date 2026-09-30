package com.ia.agentsia.controllers;

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
                        .model("gpt-image-1") // modelo equivalente a dall-3
                        .quality("low")
                        .N(1)
                        .height(1024)
                        .width(1024)
                        .build()));

        String url = imageResponse.getResult().getOutput().getB64Json();

        // Imprime en tu consola para confirmar que los datos binarios están llegando
        // System.out.println("BASE64 GENERADO: " + base64Image.substring(0, 50) +
        // "...");

        // Retornamos la cadena Base64 dentro del record de forma exitosa
        return ResponseEntity.ok(new ResponseDTO<>(200, "success", url));
    }

}
