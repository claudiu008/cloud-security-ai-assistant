package com.cld.report.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController {

    @GetMapping("/reports/test")
    public String test() {
        return "Report Service is working";
    }
}