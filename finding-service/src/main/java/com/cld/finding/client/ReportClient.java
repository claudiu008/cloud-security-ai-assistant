package com.cld.finding.client;

import com.cld.finding.model.ReportRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "REPORT-SERVICE")
public interface ReportClient {

    @PostMapping("/reports/generate")
    String generateReport(@RequestBody ReportRequest request);
}