package com.example.demo.storage.repository;

import com.example.demo.storage.entity.Letter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface LetterRepository extends JpaRepository<Letter, UUID> {}