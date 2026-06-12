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