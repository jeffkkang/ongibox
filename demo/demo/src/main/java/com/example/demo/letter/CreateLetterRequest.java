package com.example.demo.letter;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema; // Import for Swagger annotations

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLetterRequest {

    @Schema(description = "Unique S3 key for the letter's image file", example = "my-prefix/a1b2c3d4-testfile.jpg")
    private String s3Key;

    @Schema(description = "Raw OCR text extracted from the letter", example = "안녕하세요, 저는 김철수입니다.")
    private String ocrText;

    // Optional: Add more fields if you want to allow manual input for things like llmRefinedText, etc.
    // For now, let's keep it minimal for manual creation.
}