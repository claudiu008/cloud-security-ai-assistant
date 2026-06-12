package com.cld.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {

    @GetMapping("/public/status")
    public String status() {
        return "Cloud Security AI Assistant Gateway is running";
    }
}