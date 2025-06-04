package com.example.demo.letter;

import org.springframework.stereotype.Service;
import java.util.List;

@Service // Add this annotation
public class LetterService {

    private final LetterRepository letterRepository; // Inject LetterRepository

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
}