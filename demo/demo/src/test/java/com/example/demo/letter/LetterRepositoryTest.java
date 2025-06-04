package com.example.demo.letter;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;

@SpringBootTest
@Transactional
public class LetterRepositoryTest {
    @Autowired
    private LetterRepository letterRepository;

    @Test
    void saveAndFindById_Test() {
        Letter letter = new Letter();
        letter.setS3Key("test-s3-key");
        Letter saved = letterRepository.save(letter);
        assertThat(saved.getId()).isNotNull();

        Optional<Letter> foundOpt = letterRepository.findById(saved.getId());
        assertThat(foundOpt).isPresent();
        Letter found = foundOpt.get();
        assertThat(found.getS3Key()).isEqualTo("test-s3-key");
    }
}
