package com.example.demo.prompt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/prompts")
public class PromptController {

    @Autowired
    private PromptService promptService;

    // 1. Upload or update SYSTEM prompt as file
    @PostMapping("/system/upload")
    public ResponseEntity<String> uploadSystemPrompt(@RequestParam("file") MultipartFile file) {
        try {
            promptService.saveSystemPrompt(file);
            return ResponseEntity.ok("System prompt uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload system prompt: " + e.getMessage());
        }
    }

    // 2. Set SYSTEM prompt as plain text
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
    @PostMapping("/user/upload")
    public ResponseEntity<String> uploadUserPrompt(@RequestParam("file") MultipartFile file) {
        try {
            promptService.saveUserPrompt(file);
            return ResponseEntity.ok("User prompt uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload user prompt: " + e.getMessage());
        }
    }

    // 5. Set USER prompt as plain text
    @PostMapping("/user/set")
    public ResponseEntity<String> setUserPrompt(@RequestBody String prompt) {
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
