package com.cld.finding.client;

import com.cld.finding.model.AiAnalysisRequest;
import com.cld.finding.model.AiAnalysisResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "AI-SERVICE")
public interface AiClient {

    @PostMapping("/ai/analyze")
    AiAnalysisResponse analyzeFinding(@RequestBody AiAnalysisRequest request);
}