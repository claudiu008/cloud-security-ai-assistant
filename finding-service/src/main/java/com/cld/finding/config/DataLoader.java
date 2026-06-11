package com.cld.finding.config;

import com.cld.finding.model.SecurityFinding;
import com.cld.finding.repository.FindingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final FindingRepository findingRepository;

    public DataLoader(FindingRepository findingRepository) {
        this.findingRepository = findingRepository;
    }

    @Override
    public void run(String... args) {
        if (findingRepository.count() == 0) {
            findingRepository.save(new SecurityFinding(
                    1L,
                    "RootCredentialUsage",
                    "DescribeRegions",
                    "root",
                    "86.120.10.55",
                    "eu-central-1",
                    "LOW"));

            findingRepository.save(new SecurityFinding(
                    2L,
                    "RootCredentialUsage",
                    "GetAccountSummary",
                    "root",
                    "86.120.10.55",
                    "eu-central-1",
                    "LOW"));
        }
    }
}