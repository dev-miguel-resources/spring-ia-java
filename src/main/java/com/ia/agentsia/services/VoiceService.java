package com.ia.agentsia.services;

import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoiceService {

    private final OpenAiAudioSpeechModel speechModel;

    public byte[] generateWelcomeAudio(String attendeeName) {

        OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
                .model("tts-1")
                .voice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .speed(1.0)
                .build();

        String welcomeMessage = String.format(
                "¡Hola %s! Tu código QR ha sido verificado con éxito. Bienvenido al evento masivo de Spring con Inteligencia Artificial 2026. Disfruta tu estadía.",
                attendeeName);

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(welcomeMessage, options);

        TextToSpeechResponse response = speechModel.call(prompt);

        return response.getResult().getOutput();
    }

}
