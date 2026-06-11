package com.cld.report.controller;

import com.cld.report.model.ReportRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReportController {

    @GetMapping("/reports/test")
    public String test() {
        return "Report Service is working";
    }

    @PostMapping("/reports/generate")
    public String generateReport(@RequestBody ReportRequest request) {
        List<String> actions = request.recommendedActions();

        String formattedActions = actions == null || actions.isEmpty()
                ? "- No recommended actions provided"
                : "- " + String.join("\n- ", actions);

        return """
                CLOUD SECURITY REPORT
                =====================

                Finding ID: %d
                Type: %s
                API Call: %s
                Username: %s
                Source IP: %s
                Region: %s
                Severity: %s

                Risk Explanation:
                %s

                Recommended Actions:
                %s
                """.formatted(
                request.findingId(),
                request.type(),
                request.apiCall(),
                request.username(),
                request.sourceIp(),
                request.region(),
                request.severity(),
                request.riskExplanation(),
                formattedActions);
    }
}