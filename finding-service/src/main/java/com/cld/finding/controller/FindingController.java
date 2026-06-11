package com.cld.finding.controller;

import com.cld.finding.client.AiClient;
import com.cld.finding.model.AiAnalysisRequest;
import com.cld.finding.model.AiAnalysisResponse;
import com.cld.finding.model.SecurityFinding;
import com.cld.finding.repository.FindingRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FindingController {

    private final AiClient aiClient;
    private final FindingRepository findingRepository;

    public FindingController(AiClient aiClient, FindingRepository findingRepository) {
        this.aiClient = aiClient;
        this.findingRepository = findingRepository;
    }

    @GetMapping("/findings/test")
    public String test() {
        return "Finding Service is working";
    }

    @GetMapping("/findings")
    public List<SecurityFinding> getFindings() {
        return findingRepository.findAll();
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
}