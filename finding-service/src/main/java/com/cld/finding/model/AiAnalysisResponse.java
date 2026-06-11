package com.cld.finding.model;

import java.util.List;

public record AiAnalysisResponse(
        String riskExplanation,
        List<String> recommendedActions) {
}