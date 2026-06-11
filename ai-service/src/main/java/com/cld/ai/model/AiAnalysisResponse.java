package com.cld.ai.model;

import java.util.List;

public record AiAnalysisResponse(
        String riskExplanation,
        List<String> recommendedActions) {
}