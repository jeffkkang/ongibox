package com.example.demo.prompt;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PromptService {

    // Inject base directory from application.properties
    @Value("${prompt.base-dir}")
    private String baseDir;
    private Path SYSTEM_PROMPT_PATH;
    private Path USER_PROMPT_PATH;

    @PostConstruct
    public void initPaths() {
        this.SYSTEM_PROMPT_PATH = Paths.get(baseDir, "system-prompt.txt");
        this.USER_PROMPT_PATH   = Paths.get(baseDir, "user-prompt.txt");
        System.out.println("system-prompt path: " + SYSTEM_PROMPT_PATH);
    }

    // Save system prompt as file
    public void saveSystemPrompt(MultipartFile file) throws Exception {
        Files.createDirectories(SYSTEM_PROMPT_PATH.getParent());
        file.transferTo(SYSTEM_PROMPT_PATH);
    }

    // Save system prompt as text
    public void saveSystemPrompt(String prompt) throws Exception {
        Files.createDirectories(SYSTEM_PROMPT_PATH.getParent());
        Files.writeString(SYSTEM_PROMPT_PATH, prompt);
    }

    public String getSystemPrompt() throws Exception {
        return Files.readString(SYSTEM_PROMPT_PATH);
    }

    // User prompt versions
    public void saveUserPrompt(MultipartFile file) throws Exception {
        Files.createDirectories(USER_PROMPT_PATH.getParent());
        file.transferTo(USER_PROMPT_PATH);
    }

    public void saveUserPrompt(String prompt) throws Exception {
        Files.createDirectories(USER_PROMPT_PATH.getParent());
        Files.writeString(USER_PROMPT_PATH, prompt);
    }

    public String getUserPrompt() throws Exception {
        return Files.readString(USER_PROMPT_PATH);
    }
}
