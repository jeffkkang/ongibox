package com.example.demo.ocr.controller;

import com.example.demo.ocr.OcrRequest;
import com.example.demo.ocr.service.OcrService;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/openai")
public class OcrController{

    private final OcrService responseService;

    public OcrController(OcrService responseService) {
        this.responseService = responseService;
    }

    public String clean(String input) {
        return Optional.ofNullable(input)
                .map(s -> s.trim().replaceAll("[\\p{C}]", ""))
                .orElse("");
    }

    @Operation(
            summary = "Generate text with OpenAI",
            description = "Analyze image with OpenAI GPT-4o\", description = \"Send image URL and system prompt to OpenAI for emotional transcription/classification.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping("/generate")
    public ResponseEntity<String> generateResponse(@RequestBody OcrRequest request) {
        System.out.println("request.imageUrl = " + request.imageUrl);
        System.out.println("request.instruction_prompt = " + request.instructionPrompt);
//        String imageUrl = "https://haja.net/files/attach/images/170/856/009/069e2c73692815323955ef70b3159cf7.jpg";
//        String instruction_prompt = "You are not a medical professional and do not make diagnoses. " +
//                "You may describe tone, mood, or emotional language without making clinical judgments.  Please carefully transcribe the Korean handwriting and return:\n" +
//                "        1. Whether this looks like a dangerous mental health situation (Yes/No)\n" +
//                "        2. The transcribed text. the transcribed letter should be readable Korean Hangul characters.";
        String result = responseService.createModelResponse(request.imageUrl, request.instructionPrompt);
//        String result = responseService.createModelResponse(imageUrl, instruction_prompt);
        return ResponseEntity.ok(result);
    }


}