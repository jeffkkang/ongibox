package com.example.demo.storage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final S3Client s3;
    private final S3Presigner presigner;

    @Value("${s3.bucket}") private String bucket;
    @Value("${s3.prefix}") private String prefix;

    public String upload(MultipartFile file) throws IOException {
        String key = prefix + UUID.randomUUID() + "-" + sanitize(file.getOriginalFilename());

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try (InputStream in = file.getInputStream()) {
            s3.putObject(putReq, RequestBody.fromInputStream(in, file.getSize()));
        }
        return key;
    }

    public URL presignedGet(String key, Duration ttl) {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest psReq = GetObjectPresignRequest.builder()
                .getObjectRequest(getReq)
                .signatureDuration(ttl)
                .build();

        return presigner.presignGetObject(psReq).url();
    }

    private String sanitize(String name) {
        return name == null ? "unknown"
                : name.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
