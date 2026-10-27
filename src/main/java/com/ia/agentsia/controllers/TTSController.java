package com.ia.agentsia.controllers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiAudioApi;
//import org.springframework.ai.openai.audio.speech.SpeechPrompt;
//import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

// TTS: Text-To-Speech (De texto a voz)
@RestController
@RequestMapping("/tts")
@RequiredArgsConstructor
public class TTSController {

    // tts-1, tts-1-hd, gpt-4o-mini-tts y gpt-4o-tts
    private final OpenAiAudioSpeechModel openAiAudioSpeechModel;
    private final OpenAiChatModel openAiChatModel;

    @GetMapping
    public byte[] tts(@RequestParam String message) throws Exception {

        OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .voice(OpenAiAudioApi.SpeechRequest.Voice.ONYX)
                // .speed(1.0)
                .speed(1.0)
                .model(OpenAiAudioApi.TtsModel.TTS_1_HD.value)
                .build();

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(message, options);
        // SpeechPrompt prompt = new SpeechPrompt(message, options);

        TextToSpeechResponse response = openAiAudioSpeechModel.call(prompt);
        // SpeechResponse response = openAiAudioSpeechModel.call(prompt);

        byte[] responseBytes = response.getResult().getOutput();

        Path directory = Paths.get("src/main/resources/audios/tts/");

        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        Path filePath = directory.resolve("tts_1.mp3");
        Files.write(filePath, responseBytes);

        return responseBytes;
    }

    @GetMapping("/en")
    public byte[] tts2(@RequestParam String message) throws Exception {

        String instructionTraduction = "Traduce el siguiente texto al idioma inglés de forma natural, " +
                "devuelve ÚNICAMENTE la traducción, sin textos adicionales ni comillas: " + message;

        String textInEnglish = openAiChatModel.call(instructionTraduction);

        OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .voice(OpenAiAudioApi.SpeechRequest.Voice.ONYX)
                // .speed(1.0)
                .speed(1.0)
                .model(OpenAiAudioApi.TtsModel.TTS_1_HD.value)
                .build();

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(textInEnglish, options);
        // SpeechPrompt prompt = new SpeechPrompt(textInEnglish, options);

        TextToSpeechResponse response = openAiAudioSpeechModel.call(prompt);
        // SpeechResponse response = openAiAudioSpeechModel.call(prompt);

        byte[] responseBytes = response.getResult().getOutput();

        Path directory = Paths.get("src/main/resources/audios/tts/");

        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        Path filePath = directory.resolve("tts_1.mp3");
        Files.write(filePath, responseBytes);

        return responseBytes;
    }

}
