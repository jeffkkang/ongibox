package com.example.demo.letter;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "letters")
public class Letter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String s3Key;

    @Lob
    private String ocrText;

    @Lob
    private String llmRefinedText;

    private Float dangerScore;
    private Float falsePositiveScore;

    @Column
    private String dangerLabel;

    @Lob
    private String rationale;

    private Boolean humanReviewedDanger; // null이면 미확인

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    // --- 엔티티 생성 시점에 createdAt, updatedAt 자동 처리 ---
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

