package com.example.demo.letter;

import com.example.demo.analyze.service.OllamaService;
import com.example.demo.prompt.PromptService;
import com.fasterxml.jackson.core.JsonProcessingException; // Import this
import com.fasterxml.jackson.databind.ObjectMapper; // Import this
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // Add this annotation
public class LetterService {

    private final LetterRepository letterRepository;
    private final OllamaService ollamaService;
    private final ObjectMapper objectMapper;
    private final PromptService promptService; // Inject PromptService

    // Sample hardcoded text for OCR simulation (kept for now, will eventually be real OCR)
    private static final String SAMPLE_OCR_TEXT = "안녕하세요, 저는 20살 대학생이에요. 이제 막 대학교에 들어갔는데 과에는 다 언니오빠들이라 또래처럼 대하기도 힘들고, 혹여나 미움 받을까 눈치만 보고… 소극적으로 바닥만 보며 다니고 있어요. 이 상황이 지속되다 보니 제 자신이 너무 싫고, 부족해보이고, 창피해요…\n" +
            "\n" +
            "그냥 사람들 눈에 보이지 않았으면 좋겠고, 숨어만 있고 싶어요. 제 자신이 너무 싫을 땐 어떻게 해야 할까요…세상에서 사라지고 싶어요. 나쁜 생각도 많이 하는데 죽는 건 무섭고, 살아가는 건 고통스럽고 대체 저는 어떻게 해야할지 모르겠어요.\n" +
            "\n" +
            "당당하고 밝게 지내고 싶은데, 제 존재 자체가 너무 민폐같고, 그냥 제가 너무너무 싫어요. 사람들에게 호감의 대상도 아니구요. 저를 좋아하는 사람이 있을까라는 생각도 들어요. 매일매일이 무기력하고, 그냥 괴롭습니다. 내일이 오지 않았으면 좋겠어요.\n" +
            "\n" +
            "저는 언제쯤 저를 사랑할 수 있을까요? 온기우편함은 좋은 사람들이 모여 봉사로 사연을 읽어주고, 편지를 보내주는 곳이라고 들었어요. 이 글을 읽어주시는 분은 행복한 일이 가득하셨으면 좋겠습니다. 두서 없는 글을 읽어주셔서 감사합니다.";

    private static final String OLLAMA_MODEL_FOR_PII = "llama3";
    private static final String OLLAMA_MODEL_FOR_ANALYSIS = "llama3"; // Or 'llama3' or similar for better JSON

    public LetterService(LetterRepository letterRepository, OllamaService ollamaService,
                         ObjectMapper objectMapper, PromptService promptService) { // Add PromptService to constructor
        this.letterRepository = letterRepository;
        this.ollamaService = ollamaService;
        this.objectMapper = objectMapper;
        this.promptService = promptService; // Initialize
    }

    // New method to get all letters
    public List<Letter> getAllLetters() {
        return letterRepository.findAll();
    }

    // Retrieve a single letter by id
    public Letter getLetterById(Long id) {
        return letterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Letter not found with id: " + id));
    }


    public Letter createInitialLetter(String s3Key) {
        Letter letter = new Letter();
        letter.setS3Key(s3Key);
        // All other fields will be null or their default values
        return letterRepository.save(letter);
    }

    public Letter createLetter(CreateLetterRequest request) {
        Letter letter = new Letter();
        letter.setS3Key(request.getS3Key());
        letter.setOcrText(request.getOcrText());
        return letterRepository.save(letter);
    } //for manual data popullation

    @Transactional // Ensures the operation is atomic
    public Letter triggerOcrProcessing(Long letterId) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("Letter not found with id: " + letterId));

        // Simulate OCR by setting hardcoded text
        // TODO: replace this with actual OCR service later.
        letter.setOcrText(SAMPLE_OCR_TEXT);
        // PreUpdate in Letter entity will automatically update 'updatedAt'
        return letterRepository.save(letter);
    }

    @Transactional
    public Letter triggerPiiRemovalProcessing(Long letterId) throws Exception { // Propagate Exception from PromptService
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("Letter not found with id: " + letterId));

        String inputText = letter.getOcrText();
        if (inputText == null || inputText.trim().isEmpty()) {
            throw new IllegalStateException("OCR text is not available for letter id: " + letterId + ". Please run OCR first.");
        }

        // Fetch PII prompt from PromptService
        String piiPromptTemplate = promptService.getPiiPrompt();
        if (piiPromptTemplate == null || piiPromptTemplate.trim().isEmpty()) {
            throw new IllegalStateException("PII removal prompt is not set. Please set it via /prompts/pii/set.");
        }
        String fullPrompt = String.format(piiPromptTemplate, inputText);

        String llmResponse = ollamaService.generate(fullPrompt, OLLAMA_MODEL_FOR_PII);

        letter.setLlmRefinedText(llmResponse);
        return letterRepository.save(letter);
    }

    @Transactional
    public Letter triggerAnalysisProcessing(Long letterId) throws JsonProcessingException, Exception { // Propagate Exception from PromptService
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("Letter not found with id: " + letterId));

        String textToAnalyze = letter.getLlmRefinedText();
        if (textToAnalyze == null || textToAnalyze.trim().isEmpty()) {
            textToAnalyze = letter.getOcrText(); // Fallback
        }

        if (textToAnalyze == null || textToAnalyze.trim().isEmpty()) {
            throw new IllegalStateException("No text (OCR or refined) available for analysis for letter id: " + letterId + ".");
        }

        // Fetch Analysis prompt from PromptService
        String analysisPromptTemplate = promptService.getAnalysisPrompt();
        if (analysisPromptTemplate == null || analysisPromptTemplate.trim().isEmpty()) {
            throw new IllegalStateException("Analysis prompt is not set. Please set it via /prompts/analysis/set.");
        }
        String fullPrompt = String.format(analysisPromptTemplate, textToAnalyze);

        String rawLlmResponse = ollamaService.generate(fullPrompt, OLLAMA_MODEL_FOR_ANALYSIS);

        LetterAnalysisResultDTO analysisResult = null;
        try {
            analysisResult = objectMapper.readValue(rawLlmResponse, LetterAnalysisResultDTO.class);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to parse LLM analysis response for letterId: " + letterId + ". Raw response: " + rawLlmResponse);
            throw e;
        }

        letter.setDangerScore(analysisResult.getDangerScore());
        letter.setDangerLabel(analysisResult.getDangerLabel());
        letter.setRationale(analysisResult.getRationale());
        letter.setFalsePositiveScore(analysisResult.getFalsePositiveScore());

        return letterRepository.save(letter);
    }

    @Transactional
    public Letter updateHumanReviewedDanger(Long letterId, Boolean humanReviewed) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("Letter not found with id: " + letterId));

        letter.setHumanReviewedDanger(humanReviewed);
        return letterRepository.save(letter);
    }
}