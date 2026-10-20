package com.ia.agentsia.controllers;

import org.springframework.ai.openai.OpenAiAudioSpeechModel;
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

    private final OpenAiAudioSpeechModel openAiAudioSpeechModel;

    /*
     * @GetMapping
     * public byte[] tts(@RequestParam String message) {
     * 
     * }
     */

}
