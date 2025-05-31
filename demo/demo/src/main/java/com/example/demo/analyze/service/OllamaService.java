package com.example.demo.analyze.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Service
public class OllamaService {
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generate(String prompt, String model) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", model);
        request.put("prompt", prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        // 바뀐 부분: String.class로 응답 받기!
        ResponseEntity<String> response = restTemplate.postForEntity(OLLAMA_URL, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            // Ollama는 streaming NDJSON을 반환, 각 줄이 JSON임
            String[] lines = response.getBody().split("\n");
            StringBuilder result = new StringBuilder();
            try {
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        Map map = objectMapper.readValue(line, Map.class);
                        Object res = map.get("response");
                        if (res != null) result.append(res.toString());
                    }
                }
                return result.length() > 0 ? result.toString() : "No response";
            } catch (Exception e) {
                return "Parse error: " + e.getMessage();
            }
        } else {
            return "Error: " + response.getStatusCode();
        }
    }
}
