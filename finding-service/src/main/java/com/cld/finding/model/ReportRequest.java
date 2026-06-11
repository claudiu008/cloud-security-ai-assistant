package com.cld.finding.model;

import java.util.List;

public record ReportRequest(
        Long findingId,
        String type,
        String apiCall,
        String username,
        String sourceIp,
        String region,
        String severity,
        String riskExplanation,
        List<String> recommendedActions) {
}