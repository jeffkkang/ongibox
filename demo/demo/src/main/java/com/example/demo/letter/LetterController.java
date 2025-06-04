package com.example.demo.letter;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException; // Make sure to import this if you handle it directly

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Add this annotation
@RequestMapping("/letters") // Add this annotation to define base path
public class LetterController {

    private final LetterService letterService; // Inject LetterService

    // Add constructor for dependency injection
    public LetterController(LetterService letterService) {
        this.letterService = letterService;
    }

    @Operation(
            summary = "Get all stored letters",
            description = "Retrieves a list of all letter entries from the database."
    )
    @GetMapping
    public ResponseEntity<List<Letter>> getAllLetters() {
        List<Letter> letters = letterService.getAllLetters();
        return ResponseEntity.ok(letters);
    }

    @Operation(
            summary = "Manually add a new letter entry",
            description = "Allows manual creation of a letter record by providing S3 key and OCR text. Other fields will be null initially.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Letter created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping
    public ResponseEntity<Letter> createLetter(@RequestBody CreateLetterRequest request) {
        // Basic validation: s3Key is mandatory according to your Letter entity definition
        if (request.getS3Key() == null || request.getS3Key().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Or return a more specific error DTO
        }

        Letter newLetter = letterService.createLetter(request);
        return new ResponseEntity<>(newLetter, HttpStatus.CREATED); // Return 201 Created status
    }

    @Operation(
            summary = "Trigger OCR process for a specific letter",
            description = "Initiates a simulated OCR process for the letter identified by its ID. " +
                    "The letter's 'ocrText' field will be populated with hardcoded sample text. " +
                    "This simulates text extraction from the letter's image.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OCR process completed, letter updated."),
                    @ApiResponse(responseCode = "404", description = "Letter not found with the given ID.")
            }
    )
    @PostMapping("/{id}/trigger-ocr")
    public ResponseEntity<?> triggerOcrForLetter(
            @Parameter(description = "ID of the letter to process") @PathVariable Long id) {
        try {
            Letter updatedLetter = letterService.triggerOcrProcessing(id);
            return ResponseEntity.ok(updatedLetter);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Trigger PII removal process for a specific letter",
            description = "Initiates the PII (Personal Identifiable Information) removal process. " +
                    "Requires 'ocrText' to be present. The 'llmRefinedText' field will be updated " +
                    "with the redacted version using a simulated LLM call via OllamaService.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "PII removal completed, letter updated."),
                    @ApiResponse(responseCode = "404", description = "Letter not found with the given ID."),
                    @ApiResponse(responseCode = "400", description = "OCR text not available for PII removal.")
            }
    )
    @PostMapping("/{id}/trigger-pii-removal")
    public ResponseEntity<?> triggerPiiRemovalForLetter(
            @Parameter(description = "ID of the letter to process") @PathVariable Long id) {
        try {
            Letter updatedLetter = letterService.triggerPiiRemovalProcessing(id);
            return ResponseEntity.ok(updatedLetter);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400 for missing OCR text
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during PII removal process: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Trigger danger analysis process for a specific letter",
            description = "Initiates the mental/emotional danger analysis process using an LLM. " +
                    "Requires either 'llmRefinedText' or 'ocrText' to be present. " +
                    "Updates 'dangerScore', 'dangerLabel', 'rationale', and 'falsePositiveScore'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Analysis completed, letter updated."),
                    @ApiResponse(responseCode = "404", description = "Letter not found with the given ID."),
                    @ApiResponse(responseCode = "400", description = "No text available for analysis."),
                    @ApiResponse(responseCode = "500", description = "LLM response parsing failed or other internal error.")
            }
    )
    @PostMapping("/{id}/trigger-analysis")
    public ResponseEntity<?> triggerAnalysisForLetter(
            @Parameter(description = "ID of the letter to process") @PathVariable Long id) {
        try {
            Letter updatedLetter = letterService.triggerAnalysisProcessing(id);
            return ResponseEntity.ok(updatedLetter);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (JsonProcessingException e) { // Catch specific JSON parsing errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to parse LLM analysis response: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during analysis process: " + e.getMessage());
        }
    }

}