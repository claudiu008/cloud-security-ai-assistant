package com.cld.finding.controller;

import com.cld.finding.client.AiClient;
import com.cld.finding.model.AiAnalysisRequest;
import com.cld.finding.model.AiAnalysisResponse;
import com.cld.finding.model.SecurityFinding;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FindingController {

    private final AiClient aiClient;

    private final List<SecurityFinding> findings = new ArrayList<>(List.of(
            new SecurityFinding(
                    1L,
                    "RootCredentialUsage",
                    "DescribeRegions",
                    "root",
                    "86.120.10.55",
                    "eu-central-1",
                    "LOW"),
            new SecurityFinding(
                    2L,
                    "RootCredentialUsage",
                    "GetAccountSummary",
                    "root",
                    "86.120.10.55",
                    "eu-central-1",
                    "LOW")));

    public FindingController(AiClient aiClient) {
        this.aiClient = aiClient;
    }

    @GetMapping("/findings/test")
    public String test() {
        return "Finding Service is working";
    }

    @GetMapping("/findings")
    public List<SecurityFinding> getFindings() {
        return findings;
    }

    @PostMapping("/findings")
    public SecurityFinding createFinding(@RequestBody SecurityFinding finding) {
        findings.add(finding);
        return finding;
    }

    @PostMapping("/findings/{id}/analyze")
    public AiAnalysisResponse analyzeFinding(@PathVariable Long id) {
        SecurityFinding finding = findings.stream()
                .filter(item -> item.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Finding not found"));

        AiAnalysisRequest request = new AiAnalysisRequest(
                finding.id(),
                finding.type(),
                finding.apiCall(),
                finding.username(),
                finding.sourceIp(),
                finding.region(),
                finding.severity());

        return aiClient.analyzeFinding(request);
    }
}