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

    // --- Existing System Prompt Endpoints ---
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

    @Operation(
            summary = "Set System Prompt (for OpenAI image analysis - previously used)",
            description = "Sets the system prompt for the OpenAI GPT-4o image analysis. Example: You are a mental health letter screening assistant..."
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

    @GetMapping("/system")
    public ResponseEntity<String> getSystemPrompt() {
        try {
            String prompt = promptService.getSystemPrompt();
            return ResponseEntity.ok(prompt);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("System prompt not found.");
        }
    }

    // --- Existing User Prompt Endpoints ---
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

    @GetMapping("/user")
    public ResponseEntity<String> getUserPrompt() {
        try {
            String prompt = promptService.getUserPrompt();
            return ResponseEntity.ok(prompt);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("User prompt not found.");
        }
    }

    // --- NEW: PII Prompt Endpoints ---
    @Operation(
            summary = "Set PII Removal Prompt",
            description = "Sets the prompt for the LLM PII (Personal Identifiable Information) removal process. Example: Redact personal information..."
    )
    @PostMapping("/pii/set")
    public ResponseEntity<String> setPiiPrompt(@RequestBody String prompt) {
        try {
            promptService.savePiiPrompt(prompt);
            return ResponseEntity.ok("PII prompt set.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to set PII prompt: " + e.getMessage());
        }
    }

    @GetMapping("/pii")
    public ResponseEntity<String> getPiiPrompt() {
        try {
            String prompt = promptService.getPiiPrompt();
            return ResponseEntity.ok(prompt);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("PII prompt not found.");
        }
    }

    @PostMapping(value = "/pii/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPiiPrompt(
            @Parameter(description = "Upload a PII prompt text file", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("file") MultipartFile file) {
        try {
            promptService.savePiiPrompt(file);
            return ResponseEntity.ok("PII prompt uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload PII prompt: " + e.getMessage());
        }
    }

    // --- NEW: Analysis Prompt Endpoints ---
    @Operation(
            summary = "Set Analysis Prompt",
            description = "Sets the prompt for the LLM mental danger analysis process. Example: You are a mental health letter screening assistant. Provide JSON output..."
    )
    @PostMapping("/analysis/set")
    public ResponseEntity<String> setAnalysisPrompt(@RequestBody String prompt) {
        try {
            promptService.saveAnalysisPrompt(prompt);
            return ResponseEntity.ok("Analysis prompt set.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to set Analysis prompt: " + e.getMessage());
        }
    }

    @GetMapping("/analysis")
    public ResponseEntity<String> getAnalysisPrompt() {
        try {
            String prompt = promptService.getAnalysisPrompt();
            return ResponseEntity.ok(prompt);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Analysis prompt not found.");
        }
    }

    @PostMapping(value = "/analysis/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAnalysisPrompt(
            @Parameter(description = "Upload an analysis prompt text file", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("file") MultipartFile file) {
        try {
            promptService.saveAnalysisPrompt(file);
            return ResponseEntity.ok("Analysis prompt uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload analysis prompt: " + e.getMessage());
        }
    }
}