package com.example.demo.letter;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LetterRepository extends JpaRepository<Letter, Long> {
    // S3 키로 검색하는 메서드(예시)
    Optional<Letter> findByS3Key(String s3Key);
}
