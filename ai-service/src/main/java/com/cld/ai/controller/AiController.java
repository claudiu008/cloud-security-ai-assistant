package com.cld.ai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiController {

    @GetMapping("/ai/test")
    public String test() {
        return "AI Service is working";
    }
}