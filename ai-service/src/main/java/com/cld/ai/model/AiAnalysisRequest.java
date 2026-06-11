package com.cld.ai.model;

public record AiAnalysisRequest(
        Long id,
        String type,
        String apiCall,
        String username,
        String sourceIp,
        String region,
        String severity) {
}