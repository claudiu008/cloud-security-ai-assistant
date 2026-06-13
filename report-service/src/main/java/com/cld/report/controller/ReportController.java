package com.cld.report.controller;

import com.cld.report.model.ReportRequest;
import com.cld.report.service.S3ReportStorageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ReportController {

    private final S3ReportStorageService s3ReportStorageService;

    public ReportController(S3ReportStorageService s3ReportStorageService) {
        this.s3ReportStorageService = s3ReportStorageService;
    }

    @GetMapping("/reports/test")
    public String test() {
        return "Report Service is working";
    }

    @PostMapping("/reports/generate")
    public String generateReport(@RequestBody ReportRequest request) {
        return buildReport(request);
    }

    @PostMapping("/reports/generate-and-store")
    public Map<String, String> generateAndStoreReport(@RequestBody ReportRequest request) {
        String report = buildReport(request);
        String s3Key = s3ReportStorageService.storeReport(request.findingId(), report);

        return Map.of(
                "message", "Report generated and stored successfully",
                "bucket", "cloud-security-ai-assistant-bucket",
                "s3Key", s3Key
        );
    }

    private String buildReport(ReportRequest request) {
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
                formattedActions
        );
    }
}