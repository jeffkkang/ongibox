package com.example.demo.letter;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // Add this annotation
public class LetterService {

    private final LetterRepository letterRepository; // Inject LetterRepository

    private static final String SAMPLE_OCR_TEXT = "안녕하세요, 저는 김철수입니다. 제 이메일은 kcs@example.com 이고 전화번호는 010-1234-5678 입니다. 이것은 민감한 내용이 포함된 테스트 편지입니다. 잘 부탁드립니다.";

    // Add constructor for dependency injection
    public LetterService(LetterRepository letterRepository) {
        this.letterRepository = letterRepository;
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
    }

    @Transactional // Ensures the operation is atomic
    public Letter triggerOcrProcessing(Long letterId) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("Letter not found with id: " + letterId));

        // Simulate OCR by setting hardcoded text
        letter.setOcrText(SAMPLE_OCR_TEXT);
        // PreUpdate in Letter entity will automatically update 'updatedAt'
        return letterRepository.save(letter);
    }
}