package com.example.demo.view;

import com.example.demo.letter.Letter;
import com.example.demo.letter.LetterService;
import com.example.demo.prompt.PromptService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {
    private final LetterService letterService;
    private final PromptService promptService;

    public DashboardController(LetterService letterService, PromptService promptService) {
        this.letterService = letterService;
        this.promptService = promptService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        List<Letter> letters = letterService.getAllLetters();
        String piiPrompt = "";
        String analysisPrompt = "";
        try {
            piiPrompt = promptService.getPiiPrompt();
        } catch (Exception ignored) {
        }
        try {
            analysisPrompt = promptService.getAnalysisPrompt();
        } catch (Exception ignored) {
        }
        model.addAttribute("letters", letters);
        model.addAttribute("piiPrompt", piiPrompt);
        model.addAttribute("analysisPrompt", analysisPrompt);
        return "dashboard";
    }
}