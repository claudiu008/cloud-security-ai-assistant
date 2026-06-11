package com.cld.ai.controller;

import com.cld.ai.model.AiAnalysisRequest;
import com.cld.ai.model.AiAnalysisResponse;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/ai/analyze")
    public AiAnalysisResponse analyzeFinding(@RequestBody AiAnalysisRequest finding) {
        String explanation = "Security finding detected: " + finding.type()
                + ". The API call '" + finding.apiCall()
                + "' was executed by user '" + finding.username()
                + "' from IP " + finding.sourceIp()
                + " in region " + finding.region()
                + ". Severity is " + finding.severity() + ".";

        return new AiAnalysisResponse(
                explanation,
                List.of(
                        "Verify whether this activity was expected",
                        "Check AWS CloudTrail for related events",
                        "Review the source IP address",
                        "Apply least privilege permissions",
                        "Escalate the incident if the activity is suspicious"));
    }
}