package com.example.demo.ocr.controller;

import com.example.demo.ocr.service.OcrService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/openai")
public class OcrController{

    private final OcrService responseService;

    public OcrController(OcrService responseService) {
        this.responseService = responseService;
    }

    @Operation(
            summary = "Generate text with OpenAI",
            description = "Uses OpenAI API to generate a response to the input text.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping("/generate")
    public ResponseEntity<String> generateResponse(@RequestBody(
            description = "Text prompt as a JSON map",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"text\": \"Tell me a bedtime story.\"}")
            )
    ) @org.springframework.web.bind.annotation.RequestBody Map<String, String> input) {
        //swagger
        String userInput = input.get("text");
        String result = responseService.createModelResponse(userInput);
        return ResponseEntity.ok(result);
    }
}