package com.ia.agentsia.controllers;

import java.util.List;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
// import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tools")
@RequiredArgsConstructor
public class ToolController {

    // Definir la instancia a ocupar como atributo
    private final OpenAiChatModel openAiChatModel;

    // El ToolCallback declarado en ToolConfig
    // private final ToolCallback weatherFunctionInfo;

    @GetMapping
    public ResponseEntity<String> getWeatherInfo() {
        UserMessage userMessage = new UserMessage("What's the weather in San Francisco?");

        /*
         * OpenAiChatOptions options = OpenAiChatOptions.builder()
         * .toolCallbacks(weatherFunctionInfo).build();
         */

        ChatResponse chatResponse = openAiChatModel.call(new Prompt(List.of(userMessage),
                OpenAiChatOptions.builder().toolNames("weatherFunction").build()));

        String result = chatResponse.getResult().getOutput().getText();

        return ResponseEntity.ok(result);
    }

}
