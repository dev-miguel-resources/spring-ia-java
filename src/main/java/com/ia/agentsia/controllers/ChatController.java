package com.ia.agentsia.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
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
import com.ia.agentsia.utils.ChatHistory;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {

    private final OpenAiChatModel openAiChatModel;
    private final ChatHistory chatHistory;
    private final ChatMemory chatMemory;
    private final JdbcChatMemoryRepository chatMemoryRepository;

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

    // Método 5: Historial de conversación sin límite y con contexto
    @GetMapping("/generateConversation")
    public ResponseEntity<ResponseDTO<String>> generateConversation(@RequestParam String message) {

        // quiero recuperar un mensaje con lo que el usuario está mandando en la
        // solicitud
        // String username = SecurityContextHolder.getAuthentication().getName();
        chatHistory.addMessage("1", new UserMessage(message));

        ChatResponse chatResponse = openAiChatModel.call(new Prompt(chatHistory.getAll("1")));

        String result = chatResponse.getResult().getOutput().getText();

        return ResponseEntity.ok(new ResponseDTO<>(200, "success", result));

    }

    // Método 6: Maneja en memoria un default size de 20 mensajes como máximo para
    // el contexto, luego,
    // sobreescribe el más antiguo. Formato: FIFO
    @GetMapping("/memory")
    public ResponseEntity<ResponseDTO<String>> memory(@RequestParam String message) {

        // quiero recuperar un mensaje con lo que el usuario está mandando en la
        // solicitud
        // String username = SecurityContextHolder.getAuthentication().getName();
        chatMemory.add("1", List.of(new UserMessage(message)));

        // Recuperar los mensajes con el alias 1
        ChatResponse chatResponse = openAiChatModel.call(new Prompt(chatMemory.get("1")));

        String result = chatResponse.getResult().getOutput().getText();

        return ResponseEntity.ok(new ResponseDTO<>(200, "success", result));

    }

    // Método 7: Maneja en memoria un default size de 20 mensajes como máximo para
    // el contexto, luego,
    // sobreescribe el más antiguo. Formato: FIFO
    // guardar el contexto de preguntas y respuestas
    @GetMapping("/memory2")
    public ResponseEntity<ResponseDTO<String>> memory2(@RequestParam String message) {

        // quiero recuperar un mensaje con lo que el usuario está mandando en la
        // solicitud
        // String username = SecurityContextHolder.getAuthentication().getName();
        chatMemory.add("1", List.of(new UserMessage(message)));

        // Recuperar los mensajes con el alias 1
        ChatResponse chatResponse = openAiChatModel.call(new Prompt(chatMemory.get("1")));

        String result = chatResponse.getResult().getOutput().getText();

        // Guardar el contexto de la respuesta de la IA
        chatMemory.add("1", List.of(new AssistantMessage(result)));

        return ResponseEntity.ok(new ResponseDTO<>(200, "success", result));

    }

    // Método 8: Manteniendo las preguntas en bdd
    @GetMapping("/memoryrepo")
    public ResponseEntity<ResponseDTO<String>> memoryRepo(@RequestParam String username,
            @RequestParam String message) {

        ChatMemory chatMemoryRepo = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(5) // FIFO
                .build();

        // guardando los mensajes del usuario
        chatMemoryRepo.add(username, List.of(new UserMessage(message)));

        // Recuperar los mensajes con el alias 1
        ChatResponse chatResponse = openAiChatModel.call(new Prompt(chatMemory.get(username)));

        String result = chatResponse.getResult().getOutput().getText();

        return ResponseEntity.ok(new ResponseDTO<>(200, "success", result));

    }

    // Método 9: Persistiendo las preguntas y respuestas en bdd
    @GetMapping("/memoryrepo2")
    public ResponseEntity<ResponseDTO<String>> memoryRepo2(@RequestParam String username,
            @RequestParam String message) {

        ChatMemory chatMemoryRepo = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(5) // FIFO
                .build();

        // guardando los mensajes del usuario
        chatMemoryRepo.add(username, List.of(new UserMessage(message)));

        // Recuperar los mensajes con el alias 1
        ChatResponse chatResponse = openAiChatModel.call(new Prompt(chatMemory.get(username)));

        String result = chatResponse.getResult().getOutput().getText();

        // Guardamos también la respuesta de la IA en la ventana de persistencia
        chatMemoryRepo.add(username, List.of(new AssistantMessage(result)));

        return ResponseEntity.ok(new ResponseDTO<>(200, "success", result));

    }

}
