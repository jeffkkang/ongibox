package com.example.demo.ocr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ocr")
public class OcrController {

    @GetMapping("")
    public String home(){
        return "index";
    }
}
