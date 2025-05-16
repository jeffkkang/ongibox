package com.example.demo.storage.controller;

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

    /* ---------- upload & immediate URL ---------- */
    @Operation(summary = "should upload only image files")
    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> upload(@RequestPart("file") MultipartFile file) throws Exception {

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body(new UploadResponse(null, null, "Only image/* allowed"));
        }

        String key = storage.upload(file);
        URL url   = storage.presignedUrl(key, Duration.ofMinutes(10));

        return ResponseEntity.ok(new UploadResponse(key, url.toString(), "OK"));
    }

    /* ---------- get a fresh presigned URL later ---------- */
    @GetMapping("/url")
    public ResponseEntity<String> getUrl(@RequestParam String key,
                                         @RequestParam(defaultValue = "5") long minutes) {
        URL url = storage.presignedUrl(key, Duration.ofMinutes(minutes));
        return ResponseEntity.ok(url.toString());
    }

    /* DTO */
    private record UploadResponse(String key, String presignedUrl, String status) {}
}
