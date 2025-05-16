package com.example.demo.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
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

    @Value("${s3.bucket}") String bucket;
    @Value("${s3.prefix}") String prefix;

    // 1) upload file, return the S3 object key
    public String upload(MultipartFile file) throws IOException {
        String safeName = sanitize(file.getOriginalFilename());
        String key = prefix + UUID.randomUUID() + "-" + safeName;

        PutObjectRequest put = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try (InputStream in = file.getInputStream()) {
            s3.putObject(put, RequestBody.fromInputStream(in, file.getSize()));
        }
        return key;
    }

    // 2) presigned GET url (private bucket)
    public URL presignedUrl(String key, Duration ttl) {
        GetObjectRequest get = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest ps = GetObjectPresignRequest.builder()
                .getObjectRequest(get)
                .signatureDuration(ttl)
                .build();

        return presigner.presignGetObject(ps).url();
    }

    private String sanitize(String name) {
        return name == null ? "unknown"
                : name.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
