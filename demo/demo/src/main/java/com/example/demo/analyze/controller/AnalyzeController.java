package com.example.demo.analyze.controller;

import com.example.demo.analyze.AnalyzeDTO;
import com.example.demo.analyze.service.AnalyzeService;
import com.example.demo.analyze.service.OllamaService;
import com.example.demo.prompt.PromptService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/analyze")
public class AnalyzeController {
    @Autowired
    private final PromptService promptService;

    private final AnalyzeService analyzeService;
    private final OllamaService ollamaService;

    public AnalyzeController(AnalyzeService analyzeService,
                             PromptService promptService, OllamaService ollamaService) {
        this.analyzeService = analyzeService;
        this.promptService = promptService;
        this.ollamaService = ollamaService;
    }

    @Operation(
            summary = "Generate text with OpenAI (사진은 공개된 인터넷 링크만 가능 현재는)",
            description = "Analyze image with OpenAI GPT-4o\", description = \"Send image URL and system prompt to OpenAI for emotional transcription/classification.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping("/generate")
    public ResponseEntity<String> generateResponse(@RequestBody AnalyzeDTO request) {
        System.out.println("request.imageUrl = " + request.imageUrl);

        String systemPrompt;
        try {
            systemPrompt = promptService.getSystemPrompt();
        } catch (Exception e) {
            System.out.println("no system prompt.");
            throw new RuntimeException(e);
        }
        System.out.println("request.instruction_prompt = " + request.systemPrompt);

        String result = analyzeService.createModelResponse(request.imageUrl, systemPrompt);

        ObjectMapper objectMapper = new ObjectMapper();
        String text;
        try {
            JsonNode root = objectMapper.readTree(result);
            text = root.path("output").get(0).path("content").get(0).path("text").asText();
          //  System.out.println(text);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(text);
    }

    @PostMapping("/generate-ollama")
    public ResponseEntity<String> generateResponse(@RequestBody String text) {
        String model = "llama3";
        String result = ollamaService.generate(text, model);
        return ResponseEntity.ok(result);
    }




}