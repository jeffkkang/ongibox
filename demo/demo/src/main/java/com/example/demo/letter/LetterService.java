package com.example.demo.letter;

import com.example.demo.analyze.service.OllamaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // Add this annotation
public class LetterService {

    private final LetterRepository letterRepository;
    private final OllamaService ollamaService;

    private static final String SAMPLE_OCR_TEXT = "안녕하세요, 저는 김철수입니다. 제 이메일은 kcs@example.com 이고 전화번호는 010-1234-5678 입니다. 이것은 민감한 내용이 포함된 테스트 편지입니다. 잘 부탁드립니다.";
    private static final String PII_REMOVAL_PROMPT_TEMPLATE = "Redact any personal identifiable information (like names, emails, phone numbers) from the following text. Replace redacted info with '[REDACTED]'. Return only the redacted text:\n\n%s";
    private static final String OLLAMA_MODEL_FOR_PII = "tinyllama"; // Or another suitable Ollama model
    // Add constructor for dependency injection
    public LetterService(LetterRepository letterRepository, OllamaService ollamaService) {
        this.letterRepository = letterRepository;
        this.ollamaService = ollamaService;
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
        letter.setOcrText(SAMPLE_OCR_TEXT);
        // PreUpdate in Letter entity will automatically update 'updatedAt'
        return letterRepository.save(letter);
    }

    @Transactional
    public Letter triggerPiiRemovalProcessing(Long letterId) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("Letter not found with id: " + letterId));

        String ocrText = letter.getOcrText();
        if (ocrText == null || ocrText.trim().isEmpty()) {
            // Or handle this as a specific business exception if PII removal *requires* OCR text
            throw new IllegalStateException("OCR text is not available for letter id: " + letterId + ". Please run OCR first.");
        }

        // Prepare the prompt for Ollama
        String fullPrompt = String.format(PII_REMOVAL_PROMPT_TEMPLATE, ocrText);

        // Call OllamaService for PII removal
        // TODO: For actual implementation, ensure OllamaService is running and configured
        // TODO: The response from OllamaService might need more robust parsing depending on the model's output
        String llmResponse = ollamaService.generate(fullPrompt, OLLAMA_MODEL_FOR_PII);

        // Update the letter with the refined text
        letter.setLlmRefinedText(llmResponse);
        return letterRepository.save(letter);
    }
}