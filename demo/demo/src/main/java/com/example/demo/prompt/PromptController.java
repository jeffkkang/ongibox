package com.example.demo.prompt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/prompts")
public class PromptController {

    @Autowired
    private PromptService promptService;

    // 1. Upload or update SYSTEM prompt as file
    @PostMapping(value = "/system/upload" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadSystemPrompt(
            @Parameter(description = "Upload file", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("file") MultipartFile file) {
        try {
            promptService.saveSystemPrompt(file);
            return ResponseEntity.ok("System prompt uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload system prompt: " + e.getMessage());
        }
    }

    // 2. Set SYSTEM prompt as plain text
    @Operation(
            summary = "프롬프트 수정해서 사용",
            description = "You are a mental health letter screening assistant. For each letter, (1) transcribe carefully, (2) analyze for mental danger, (3) assign a 0–5 score and a label (Safe, Mild, Moderate, Severe, Extreme), (4) explain your reasoning step by step, and (5) respond only in the following JSON structure:\n" +
                    "\n" +
                    "{\n" +
                    "\"danger_score\": [0~5],\n" +
                    "\"danger_label\": \"...\",\n" +
                    "\"rationale\": \"...\",\n" +
                    "\"transcription\": \"...\",\n" +
                    "}\n" +
                    "\n" +
                    "Use chain-of-thought reasoning for the rationale."
    )
    @PostMapping("/system/set")
    public ResponseEntity<String> setSystemPrompt(@RequestBody String prompt) {
        try {
            promptService.saveSystemPrompt(prompt);
            return ResponseEntity.ok("System prompt set.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to set system prompt: " + e.getMessage());
        }
    }

    // 3. Get current SYSTEM prompt
    @GetMapping("/system")
    public ResponseEntity<String> getSystemPrompt() {
        try {
            String prompt = promptService.getSystemPrompt();
            return ResponseEntity.ok(prompt);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("System prompt not found.");
        }
    }

    // 4. (Optional) Upload USER prompt as file
    @PostMapping(value = "/user/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadUserPrompt(
            @Parameter(description = "Upload a user prompt text file", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("file") MultipartFile file) {
        try {
            promptService.saveUserPrompt(file);
            return ResponseEntity.ok("User prompt uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload user prompt: " + e.getMessage());
        }
    }

    // 5. Set USER prompt as plain text
    @PostMapping("/user/set")
    public ResponseEntity<String> setUserPrompt(
            @Parameter(description = "Upload a user prompt by string.")
            @RequestBody String prompt) {
        try {
            promptService.saveUserPrompt(prompt);
            return ResponseEntity.ok("User prompt set.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to set user prompt: " + e.getMessage());
        }
    }

    // 6. Get current USER prompt
    @GetMapping("/user")
    public ResponseEntity<String> getUserPrompt() {
        try {
            String prompt = promptService.getUserPrompt();
            return ResponseEntity.ok(prompt);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("User prompt not found.");
        }
    }
}
