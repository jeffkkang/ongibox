package com.example.demo.storage.controller;

import com.example.demo.storage.entity.Letter;
import com.example.demo.storage.repository.LetterRepository;
import com.example.demo.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

// ...rest of your class



@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storage;
    private final LetterRepository repo;

    /* --- Upload endpoint ------------------------------------------------- */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LetterDTO> upload(@RequestPart("file") MultipartFile file)
            throws Exception {

        if (!file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body(null);
        }
        UUID id  = UUID.randomUUID();
        String key = storage.upload(file);         // stored in S3

        repo.save(new Letter(id, key));
        return ResponseEntity.ok(new LetterDTO(id));
    }

    /* --- Temp download URL ----------------------------------------------- */
    @GetMapping("/{id}/image-url")
    public ResponseEntity<String> getUrl(@PathVariable UUID id) {
        Letter letter = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        URL url = storage.presignedGet(letter.getS3Key(), Duration.ofMinutes(10));
        return ResponseEntity.ok(url.toString());
    }

    /* Simple DTO */
    private record LetterDTO(UUID id) {}
}
