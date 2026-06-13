package com.cld.finding.service;

import com.cld.finding.model.SecurityFinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

import java.util.List;
import java.util.Map;

@Service
public class DynamoDbFindingService {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public DynamoDbFindingService(
            DynamoDbClient dynamoDbClient,
            @Value("${aws.dynamodb.findings-table-name}") String tableName
    ) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
    }

    public List<SecurityFinding> findAll() {
        ScanRequest request = ScanRequest.builder()
                .tableName(tableName)
                .build();

        return dynamoDbClient.scan(request)
                .items()
                .stream()
                .map(this::mapToSecurityFinding)
                .toList();
    }

    public SecurityFinding findById(String id) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(
                        "id", AttributeValue.builder().s(id).build()
                ))
                .build();

        Map<String, AttributeValue> item = dynamoDbClient.getItem(request).item();

        if (item == null || item.isEmpty()) {
            return null;
        }

        return mapToSecurityFinding(item);
    }

    private SecurityFinding mapToSecurityFinding(Map<String, AttributeValue> item) {
        SecurityFinding finding = new SecurityFinding();

        finding.setId(Long.valueOf(getStringValue(item, "id")));
        finding.setType(getStringValue(item, "type"));
        finding.setApiCall(getStringValue(item, "apiCall"));
        finding.setUsername(getStringValue(item, "username"));
        finding.setSourceIp(getStringValue(item, "sourceIp"));
        finding.setRegion(getStringValue(item, "region"));
        finding.setSeverity(getStringValue(item, "severity"));

        return finding;
    }

    private String getStringValue(Map<String, AttributeValue> item, String key) {
        AttributeValue value = item.get(key);

        if (value == null || value.s() == null) {
            return "";
        }

        return value.s();
    }
}