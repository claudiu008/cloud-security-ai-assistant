# Cloud Security AI Assistant

Cloud Security AI Assistant is a Spring Boot microservices application for analyzing cloud security findings.

The application demonstrates:

- Spring Boot microservices
- Spring Cloud Eureka service discovery
- Spring Cloud Gateway routing
- Spring Data JPA database persistence
- OpenFeign service-to-service communication
- AI-style security finding analysis
- Security report generation

## Services

| Service | Port | Responsibility |
|---|---:|---|
| discovery-service | 8761 | Eureka service registry |
| gateway-service | 8080 | Central API Gateway |
| finding-service | 8081 | Stores and manages security findings |
| ai-service | 8082 | Analyzes security findings |
| report-service | 8083 | Generates security reports |

## Main flow

The strongest application flow is:

```text
User
  -> Gateway Service
  -> Finding Service
  -> H2 Database
  -> AI Service
  -> Report Service
  -> Generated security report



## Demo Scenarios

### 1. Check Eureka service discovery

Open:

```text
http://localhost:8761
```

Expected result:

The following services should be registered and marked as `UP`:

```text
GATEWAY-SERVICE
FINDING-SERVICE
AI-SERVICE
REPORT-SERVICE
```

This demonstrates Eureka service discovery.

---

### 2. Get all security findings

```http
GET http://localhost:8080/api/findings
```

Expected result:

A JSON list of cloud security findings.

This demonstrates Gateway routing and database access through the finding-service.

---

### 3. Analyze a security finding

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings/1/analyze" `
  -Method POST
```

Expected result:

An AI-style risk explanation and recommended remediation actions.

This demonstrates service-to-service communication between finding-service and ai-service using OpenFeign.

---

### 4. Generate a full security report

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings/1/report" `
  -Method POST
```

Expected result:

A generated cloud security report.

This demonstrates the complete flow:

```text
Gateway
  -> Finding Service
  -> H2 Database
  -> AI Service
  -> Report Service
  -> Generated Report
```

---

### 5. Test individual services through the Gateway

```http
GET http://localhost:8080/api/ai/test
```

```http
GET http://localhost:8080/api/reports/test
```

Expected result:

```text
AI Service is working
Report Service is working
```

These endpoints verify that the Gateway can route requests to independent microservices.
