package com.cld.finding.controller;

import com.cld.finding.model.SecurityFinding;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FindingController {

    private final List<SecurityFinding> findings = new ArrayList<>(List.of(
            new SecurityFinding(
                    1L,
                    "RootCredentialUsage",
                    "DescribeRegions",
                    "root",
                    "86.120.10.55",
                    "eu-central-1",
                    "LOW"),
            new SecurityFinding(
                    2L,
                    "RootCredentialUsage",
                    "GetAccountSummary",
                    "root",
                    "86.120.10.55",
                    "eu-central-1",
                    "LOW")));

    @GetMapping("/findings/test")
    public String test() {
        return "Finding Service is working";
    }

    @GetMapping("/findings")
    public List<SecurityFinding> getFindings() {
        return findings;
    }

    @PostMapping("/findings")
    public SecurityFinding createFinding(@RequestBody SecurityFinding finding) {
        findings.add(finding);
        return finding;
    }
}