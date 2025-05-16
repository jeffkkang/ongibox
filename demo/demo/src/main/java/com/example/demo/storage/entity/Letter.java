package com.example.demo.storage.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "letters")
@Getter @Setter @NoArgsConstructor
public class Letter {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String s3Key;

    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Letter(UUID id, String s3Key) {
        this.id     = id;
        this.s3Key  = s3Key;
    }
}

