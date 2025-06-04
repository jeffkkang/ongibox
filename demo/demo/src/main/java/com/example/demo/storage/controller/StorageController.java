package com.example.demo.storage.controller;

import com.example.demo.letter.Letter; // Import Letter
import com.example.demo.letter.LetterService; // Import LetterService
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.time.Duration;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class StorageController {

    private final com.example.demo.storage.StorageService storage;
    private final LetterService letterService; // Inject LetterService

    /* ---------- upload & immediate URL ---------- */
    @Operation(summary = "Upload image file to S3 and create a new letter record in DB")
    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> upload(@RequestPart("file") MultipartFile file) throws Exception {

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body(new UploadResponse(null, null, "Only image/* allowed", null));
        }

        // 1. Upload to S3
        String key = storage.upload(file);

        // 2. Create initial Letter record in DB
        Letter newLetter = letterService.createInitialLetter(key);

        // 3. Generate presigned URL
        URL url = storage.presignedUrl(key, Duration.ofMinutes(10));

        // Return S3 key, presigned URL, and the new letter's ID
        return ResponseEntity.ok(new UploadResponse(key, url.toString(), "OK", newLetter.getId()));
    }

    /* ---------- get a fresh presigned URL later ---------- */
    @GetMapping("/url")
    public ResponseEntity<String> getUrl(@RequestParam String key,
                                         @RequestParam(defaultValue = "5") long minutes) {
        URL url = storage.presignedUrl(key, Duration.ofMinutes(minutes));
        return ResponseEntity.ok(url.toString());
    }

    /* DTO: Updated to include letterId */
    private record UploadResponse(String key, String presignedUrl, String status, Long letterId) {}
}