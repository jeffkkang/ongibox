package com.example.demo.ocr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class OcrRequest {

    @Schema(
            description = "Public image URL to be analyzed",
            example = "https://haja.net/files/attach/images/170/856/009/069e2c73692815323955ef70b3159cf7.jpg"
    )
    public String imageUrl;

    @Schema(
            description = "System prompt describing what the assistant should do",
            example = "You are not a medical professional and do not make diagnoses. You may describe tone, mood, or emotional language without making clinical judgments. Please carefully transcribe the Korean handwriting and return:\n 1. Whether this looks like a dangerous mental health situation (Yes/No)\n 2. The transcribed text. The transcribed letter should be readable Korean Hangul characters."
    )
    public String instructionPrompt;
}