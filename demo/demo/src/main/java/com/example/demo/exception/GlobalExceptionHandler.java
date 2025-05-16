package com.example.demo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ S3Exception.class, SdkException.class, IOException.class })
    public ResponseEntity<String> handleAws(Exception ex) {
        return ResponseEntity
                .status(502)
                .body("Upload failed: " + ex.getMessage());
    }
}