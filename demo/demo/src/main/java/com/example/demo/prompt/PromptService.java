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

    @Value("${prompt.base-dir}")
    private String baseDir;
    private Path SYSTEM_PROMPT_PATH;
    private Path USER_PROMPT_PATH;
    private Path PII_PROMPT_PATH; // New path for PII prompt
    private Path ANALYSIS_PROMPT_PATH; // New path for Analysis prompt

    @PostConstruct
    public void initPaths() {
        this.SYSTEM_PROMPT_PATH = Paths.get(baseDir, "system-prompt.txt");
        this.USER_PROMPT_PATH   = Paths.get(baseDir, "user-prompt.txt");
        this.PII_PROMPT_PATH    = Paths.get(baseDir, "pii-prompt.txt"); // Initialize new path
        this.ANALYSIS_PROMPT_PATH = Paths.get(baseDir, "analysis-prompt.txt"); // Initialize new path

//        System.out.println("system-prompt path: " + SYSTEM_PROMPT_PATH);
//        System.out.println("pii-prompt path: " + PII_PROMPT_PATH); // Log new paths
//        System.out.println("analysis-prompt path: " + ANALYSIS_PROMPT_PATH); // Log new paths
    }

    // --- System Prompt methods (existing) ---
    public void saveSystemPrompt(MultipartFile file) throws Exception {
        Files.createDirectories(SYSTEM_PROMPT_PATH.getParent());
        file.transferTo(SYSTEM_PROMPT_PATH);
    }

    public void saveSystemPrompt(String prompt) throws Exception {
        Files.createDirectories(SYSTEM_PROMPT_PATH.getParent());
        Files.writeString(SYSTEM_PROMPT_PATH, prompt);
    }

    public String getSystemPrompt() throws Exception {
        return Files.readString(SYSTEM_PROMPT_PATH);
    }

    // --- User Prompt methods (existing) ---
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

    // --- NEW: PII Prompt methods ---
    public void savePiiPrompt(MultipartFile file) throws Exception {
        Files.createDirectories(PII_PROMPT_PATH.getParent());
        file.transferTo(PII_PROMPT_PATH);
    }

    public void savePiiPrompt(String prompt) throws Exception {
        Files.createDirectories(PII_PROMPT_PATH.getParent());
        Files.writeString(PII_PROMPT_PATH, prompt);
    }

    public String getPiiPrompt() throws Exception {
        return Files.readString(PII_PROMPT_PATH);
    }

    // --- NEW: Analysis Prompt methods ---
    public void saveAnalysisPrompt(MultipartFile file) throws Exception {
        Files.createDirectories(ANALYSIS_PROMPT_PATH.getParent());
        file.transferTo(ANALYSIS_PROMPT_PATH);
    }

    public void saveAnalysisPrompt(String prompt) throws Exception {
        Files.createDirectories(ANALYSIS_PROMPT_PATH.getParent());
        Files.writeString(ANALYSIS_PROMPT_PATH, prompt);
    }

    public String getAnalysisPrompt() throws Exception {
        return Files.readString(ANALYSIS_PROMPT_PATH);
    }
}