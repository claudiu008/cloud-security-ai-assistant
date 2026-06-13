package com.cld.report.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class S3ReportStorageService {

    private final S3Client s3Client;
    private final String bucketName;

    public S3ReportStorageService(
            S3Client s3Client,
            @Value("${aws.s3.bucket-name}") String bucketName
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public String storeReport(Long findingId, String reportContent) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        String key = "reports/finding-" + findingId + "-" + timestamp + ".txt";

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("text/plain")
                .build();

        s3Client.putObject(request, RequestBody.fromString(reportContent));

        return key;
    }
}