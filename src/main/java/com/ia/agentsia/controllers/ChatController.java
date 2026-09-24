package com.ia.agentsia.controllers;

import java.util.Map;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ia.agentsia.dtos.AuthorBook;
import com.ia.agentsia.dtos.AuthorBook2;
import com.ia.agentsia.dtos.ResponseDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {

    private final OpenAiChatModel openAiChatModel;

    // Método 1: Sin manejo de contexto ni persistencia
    @GetMapping("/generate")
    public ResponseEntity<ResponseDTO<String>> generateText(@RequestParam String message) {

        ChatResponse chatResponse = openAiChatModel.call(new Prompt(message));

        String result = chatResponse.getResult().getOutput().getText();

        return ResponseEntity.ok(new ResponseDTO<>(200, "success", result));

    }

    // Método 2: Manejo de templates
    @GetMapping("/generate/prompt")
    public ResponseEntity<ResponseDTO<String>> generatePrompt(@RequestParam String author,
            @RequestParam String bookName) {

        PromptTemplate promptTemplate = new PromptTemplate(
                "Tell me about ${author} and his ${bookName} and only print 500 characters");

        Prompt prompt = promptTemplate.create(Map.of("author", author, "bookName", bookName));

        ChatResponse chatResponse = openAiChatModel.call(prompt);
        String result = chatResponse.getResult().getOutput().getText();

        return ResponseEntity.ok(new ResponseDTO<>(200, "success", result));

    }

    // Método 3: Manejo de formato mediante una clase Record
    @GetMapping("/generate/output")
    public ResponseEntity<AuthorBook> generateOutput(@RequestParam String author) {

        BeanOutputConverter<AuthorBook> outputConverter = new BeanOutputConverter<>(AuthorBook.class);

        String template = """
                Tell me book title of ${author}. {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(Map.of("author", author, "format", outputConverter.getFormat()));

        ChatResponse chatResponse = openAiChatModel.call(prompt);
        String result = chatResponse.getResult().getOutput().getText();

        AuthorBook authorBook = outputConverter.convert(result);

        return ResponseEntity.ok(authorBook);

    }

    // Método 4: Manejo de formato mediante el uso de Model
    @GetMapping("/generate/output2")
    public ResponseEntity<AuthorBook2> generateOutput2(@RequestParam String author) {

        BeanOutputConverter<AuthorBook2> outputConverter = new BeanOutputConverter<>(AuthorBook2.class);

        String template = """
                Tell me book title of ${author}. {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(Map.of("author", author, "format", outputConverter.getFormat()));

        ChatResponse chatResponse = openAiChatModel.call(prompt);
        String result = chatResponse.getResult().getOutput().getText();

        AuthorBook2 authorBook = outputConverter.convert(result);

        return ResponseEntity.ok(authorBook);

    }

}
