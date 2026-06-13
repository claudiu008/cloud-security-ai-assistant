package com.cld.finding.controller;

import com.cld.finding.client.AiClient;
import com.cld.finding.model.AiAnalysisRequest;
import com.cld.finding.model.AiAnalysisResponse;
import com.cld.finding.model.SecurityFinding;
import com.cld.finding.repository.FindingRepository;
import org.springframework.web.bind.annotation.*;
import com.cld.finding.client.ReportClient;
import com.cld.finding.model.ReportRequest;
import com.cld.finding.service.DynamoDbFindingService;

import java.util.List;
import java.util.Map;

@RestController
public class FindingController {

    private final AiClient aiClient;
    private final ReportClient reportClient;
    private final FindingRepository findingRepository;
    private final DynamoDbFindingService dynamoDbFindingService;

    public FindingController(
            FindingRepository findingRepository,
            AiClient aiClient,
            ReportClient reportClient,
            DynamoDbFindingService dynamoDbFindingService
    ) {
        this.findingRepository = findingRepository;
        this.aiClient = aiClient;
        this.reportClient = reportClient;
        this.dynamoDbFindingService = dynamoDbFindingService;
    }

    @GetMapping("/findings/test")
    public String test() {
        return "Finding Service is working";
    }

    @GetMapping("/findings")
    public List<SecurityFinding> getFindings() {
        return findingRepository.findAll();
    }

    @GetMapping("/findings/dynamodb")
    public List<SecurityFinding> getFindingsFromDynamoDb() {
        return dynamoDbFindingService.findAll();
    }

    @GetMapping("/findings/dynamodb/{id}")
    public SecurityFinding getFindingFromDynamoDbById(@PathVariable String id) {
        return dynamoDbFindingService.findById(id);
    }

    @PostMapping("/findings")
    public SecurityFinding createFinding(@RequestBody SecurityFinding finding) {
        return findingRepository.save(finding);
    }

    @PostMapping("/findings/{id}/analyze")
    public AiAnalysisResponse analyzeFinding(@PathVariable Long id) {
        SecurityFinding finding = findingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Finding not found"));

        AiAnalysisRequest request = new AiAnalysisRequest(
                finding.getId(),
                finding.getType(),
                finding.getApiCall(),
                finding.getUsername(),
                finding.getSourceIp(),
                finding.getRegion(),
                finding.getSeverity());

        return aiClient.analyzeFinding(request);
    }

    @PostMapping("/findings/{id}/report")
    public String generateReport(@PathVariable Long id) {
        SecurityFinding finding = findingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Finding not found"));

        AiAnalysisRequest aiRequest = new AiAnalysisRequest(
                finding.getId(),
                finding.getType(),
                finding.getApiCall(),
                finding.getUsername(),
                finding.getSourceIp(),
                finding.getRegion(),
                finding.getSeverity());

        AiAnalysisResponse aiResponse = aiClient.analyzeFinding(aiRequest);

        ReportRequest reportRequest = new ReportRequest(
                finding.getId(),
                finding.getType(),
                finding.getApiCall(),
                finding.getUsername(),
                finding.getSourceIp(),
                finding.getRegion(),
                finding.getSeverity(),
                aiResponse.riskExplanation(),
                aiResponse.recommendedActions());

        return reportClient.generateReport(reportRequest);
    }

    @PostMapping("/findings/{id}/report/store")
    public Map<String, String> generateAndStoreReport(@PathVariable Long id) {
        SecurityFinding finding = findingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Finding not found"));

        AiAnalysisRequest aiRequest = new AiAnalysisRequest(
                finding.getId(),
                finding.getType(),
                finding.getApiCall(),
                finding.getUsername(),
                finding.getSourceIp(),
                finding.getRegion(),
                finding.getSeverity()
        );

        AiAnalysisResponse aiResponse = aiClient.analyzeFinding(aiRequest);

        ReportRequest reportRequest = new ReportRequest(
                finding.getId(),
                finding.getType(),
                finding.getApiCall(),
                finding.getUsername(),
                finding.getSourceIp(),
                finding.getRegion(),
                finding.getSeverity(),
                aiResponse.riskExplanation(),
                aiResponse.recommendedActions()
        );

        return reportClient.generateAndStoreReport(reportRequest);
    }
}