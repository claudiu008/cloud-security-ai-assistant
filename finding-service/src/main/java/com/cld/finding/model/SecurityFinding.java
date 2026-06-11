package com.cld.finding.model;

public record SecurityFinding(
        Long id,
        String type,
        String apiCall,
        String username,
        String sourceIp,
        String region,
        String severity) {
}