package com.example.demo.letter;

import com.example.demo.analyze.service.OllamaService;
import com.example.demo.prompt.PromptService;
import com.fasterxml.jackson.core.JsonProcessingException; // Import this
import com.fasterxml.jackson.databind.ObjectMapper; // Import this
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // Add this annotation
public class LetterService {

    private final LetterRepository letterRepository;
    private final OllamaService ollamaService;
    private final ObjectMapper objectMapper;
    private final PromptService promptService; // Inject PromptService

    // Sample hardcoded text for OCR simulation (kept for now, will eventually be real OCR)
    private static final String SAMPLE_OCR_TEXT = "안녕하세요, 저는 김철수입니다. 제 이메일은 kcs@example.com 이고 전화번호는 010-1234-5678 입니다. 이것은 민감한 내용이 포함된 테스트 편지입니다. 잘 부탁드립니다.";

    private static final String OLLAMA_MODEL_FOR_PII = "tinyllama";
    private static final String OLLAMA_MODEL_FOR_ANALYSIS = "tinyllama"; // Or 'llama3' or similar for better JSON

    public LetterService(LetterRepository letterRepository, OllamaService ollamaService,
                         ObjectMapper objectMapper, PromptService promptService) { // Add PromptService to constructor
        this.letterRepository = letterRepository;
        this.ollamaService = ollamaService;
        this.objectMapper = objectMapper;
        this.promptService = promptService; // Initialize
    }

    // New method to get all letters
    public List<Letter> getAllLetters() {
        return letterRepository.findAll();
    }

    public Letter createInitialLetter(String s3Key) {
        Letter letter = new Letter();
        letter.setS3Key(s3Key);
        // All other fields will be null or their default values
        return letterRepository.save(letter);
    }

    public Letter createLetter(CreateLetterRequest request) {
        Letter letter = new Letter();
        letter.setS3Key(request.getS3Key());
        letter.setOcrText(request.getOcrText());
        return letterRepository.save(letter);
    } //for manual data popullation

    @Transactional // Ensures the operation is atomic
    public Letter triggerOcrProcessing(Long letterId) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("Letter not found with id: " + letterId));

        // Simulate OCR by setting hardcoded text
        // TODO: replace this with actual OCR service later.
        letter.setOcrText(SAMPLE_OCR_TEXT);
        // PreUpdate in Letter entity will automatically update 'updatedAt'
        return letterRepository.save(letter);
    }

    @Transactional
    public Letter triggerPiiRemovalProcessing(Long letterId) throws Exception { // Propagate Exception from PromptService
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("Letter not found with id: " + letterId));

        String inputText = letter.getOcrText();
        if (inputText == null || inputText.trim().isEmpty()) {
            throw new IllegalStateException("OCR text is not available for letter id: " + letterId + ". Please run OCR first.");
        }

        // Fetch PII prompt from PromptService
        String piiPromptTemplate = promptService.getPiiPrompt();
        if (piiPromptTemplate == null || piiPromptTemplate.trim().isEmpty()) {
            throw new IllegalStateException("PII removal prompt is not set. Please set it via /prompts/pii/set.");
        }
        String fullPrompt = String.format(piiPromptTemplate, inputText);

        String llmResponse = ollamaService.generate(fullPrompt, OLLAMA_MODEL_FOR_PII);

        letter.setLlmRefinedText(llmResponse);
        return letterRepository.save(letter);
    }

    @Transactional
    public Letter triggerAnalysisProcessing(Long letterId) throws JsonProcessingException, Exception { // Propagate Exception from PromptService
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("Letter not found with id: " + letterId));

        String textToAnalyze = letter.getLlmRefinedText();
        if (textToAnalyze == null || textToAnalyze.trim().isEmpty()) {
            textToAnalyze = letter.getOcrText(); // Fallback
        }

        if (textToAnalyze == null || textToAnalyze.trim().isEmpty()) {
            throw new IllegalStateException("No text (OCR or refined) available for analysis for letter id: " + letterId + ".");
        }

        // Fetch Analysis prompt from PromptService
        String analysisPromptTemplate = promptService.getAnalysisPrompt();
        if (analysisPromptTemplate == null || analysisPromptTemplate.trim().isEmpty()) {
            throw new IllegalStateException("Analysis prompt is not set. Please set it via /prompts/analysis/set.");
        }
        String fullPrompt = String.format(analysisPromptTemplate, textToAnalyze);

        String rawLlmResponse = ollamaService.generate(fullPrompt, OLLAMA_MODEL_FOR_ANALYSIS);

        LetterAnalysisResultDTO analysisResult = null;
        try {
            analysisResult = objectMapper.readValue(rawLlmResponse, LetterAnalysisResultDTO.class);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to parse LLM analysis response for letterId: " + letterId + ". Raw response: " + rawLlmResponse);
            throw e;
        }

        letter.setDangerScore(analysisResult.getDangerScore());
        letter.setDangerLabel(analysisResult.getDangerLabel());
        letter.setRationale(analysisResult.getRationale());
        letter.setFalsePositiveScore(analysisResult.getFalsePositiveScore());

        return letterRepository.save(letter);
    }
}