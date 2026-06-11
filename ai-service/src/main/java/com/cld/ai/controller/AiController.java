package com.cld.ai.controller;

import com.cld.ai.model.AiAnalysisResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AiController {

    @GetMapping("/ai/test")
    public String test() {
        return "AI Service is working";
    }

    @GetMapping("/ai/analyze/root-usage")
    public AiAnalysisResponse analyzeRootUsage() {
        return new AiAnalysisResponse(
                "Root credentials were used for AWS API activity. In a production environment, root account usage should be rare and must be investigated.",
                List.of(
                        "Verify whether the root activity was expected",
                        "Check AWS CloudTrail for source IP, time, and API action",
                        "Enable MFA on the root account",
                        "Use IAM users or roles for daily administration",
                        "Rotate credentials if compromise is suspected"));
    }
}