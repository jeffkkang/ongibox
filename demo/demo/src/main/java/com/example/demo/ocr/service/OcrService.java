package com.example.demo.ocr.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OcrService {

    @Value("${api.openai.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String createModelResponse(String input){
        String url = "https://api.openai.com/v1/responses";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 👇 Build content: input_text + input_image
        Map<String, Object> inputText = new HashMap<>();
        inputText.put("type", "input_text");
        inputText.put("text", "");

        Map<String, Object> inputImage = new HashMap<>();
        String imageUrl = "https://haja.net/files/attach/images/170/856/009/069e2c73692815323955ef70b3159cf7.jpg";
        inputImage.put("type", "input_image");
        inputImage.put("image_url", imageUrl);

        List<Map<String, Object>> content = List.of(inputText, inputImage);

        // 👇 Build message
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", content);

        // 👇 Build request
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        requestBody.put("input", List.of(userMessage));
        String instruction_prompt = "You are not a medical professional and do not make diagnoses. " +
                "You may describe tone, mood, or emotional language without making clinical judgments.  Please carefully transcribe the Korean handwriting and return:\n" +
                "        1. Whether this looks like a dangerous mental health situation (Yes/No)\n" +
                "        2. The transcribed text. the transcribed letter should be readable Korean Hangul characters.";
        requestBody.put("instructions", instruction_prompt);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_output_tokens", 300);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        return response.getBody();
    }


}
