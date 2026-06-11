package com.cld.finding.model;

public record AiAnalysisRequest(
        Long id,
        String type,
        String apiCall,
        String username,
        String sourceIp,
        String region,
        String severity) {
}