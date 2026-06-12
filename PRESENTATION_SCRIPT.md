# Presentation Script - Cloud Security AI Assistant

## 1. Project introduction

This project is called **Cloud Security AI Assistant**.

It is a secured Spring Boot microservices application that analyzes cloud security findings, generates AI-style risk explanations, and produces security reports.

The project is inspired by AWS GuardDuty-style findings, especially root credential usage events such as:

```text
DescribeRegions
GetAccountSummary
```

## 2. Architecture overview

The application uses a microservices architecture.

Services:

```text
discovery-service  -> Eureka Server
gateway-service    -> API Gateway + Security
finding-service    -> Stores security findings
ai-service         -> Generates AI-style analysis
report-service     -> Generates security reports
keycloak           -> External IAM provider
```

All services run locally using Docker Compose.

## 3. Service discovery

I use Eureka for service discovery.

Open:

```text
http://localhost:8761
```

Expected services:

```text
GATEWAY-SERVICE
FINDING-SERVICE
AI-SERVICE
REPORT-SERVICE
```

This shows that the microservices register themselves dynamically with Eureka.

## 4. Docker Compose orchestration

The whole system starts with one command:

```powershell
docker compose up --build
```

Docker Compose starts all containers:

```text
discovery-service
gateway-service
finding-service
ai-service
report-service
keycloak
```

This replaces running each Spring Boot service manually.

## 5. Public endpoint demo

First, I test a public endpoint:

```powershell
curl.exe -i http://localhost:8080/public/status
```

Expected result:

```text
HTTP/1.1 200
Cloud Security AI Assistant Gateway is running
```

This proves the Gateway is running and public routes work without authentication.

## 6. Protected endpoint without token

Then I test a protected endpoint without authentication:

```powershell
curl.exe -i http://localhost:8080/api/findings
```

Expected result:

```text
HTTP/1.1 401 Unauthorized
```

This proves Spring Security protects the API.

## 7. Keycloak authentication

The project uses Keycloak as an external IAM provider.

Configured IAM objects:

```text
Realm: cloud-security
Client: cloud-security-gateway
Role: SECURITY_ANALYST
User: analyst
```

The Keycloak realm is imported automatically when Docker Compose starts, so the environment is reproducible.

## 8. Get JWT token

I authenticate the `analyst` user and get a JWT access token:

```powershell
$response = Invoke-RestMethod `
  -Uri "http://localhost:8084/realms/cloud-security/protocol/openid-connect/token" `
  -Method POST `
  -ContentType "application/x-www-form-urlencoded" `
  -Body @{
    grant_type = "password"
    client_id = "cloud-security-gateway"
    username = "analyst"
    password = "analyst123"
  }

$token = $response.access_token
```

The JWT is then sent to the Gateway using the Authorization header.

## 9. Protected endpoint with token

Now I call the protected findings endpoint with the JWT token:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings" `
  -Headers @{ Authorization = "Bearer $token" }
```

Expected result:

```text
RootCredentialUsage - DescribeRegions
RootCredentialUsage - GetAccountSummary
```

This proves that authenticated users with the `SECURITY_ANALYST` role can access protected APIs.

## 10. Full protected report flow

Finally, I generate a full protected security report:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings/1/report" `
  -Method POST `
  -Headers @{ Authorization = "Bearer $token" }
```

Expected result:

```text
CLOUD SECURITY REPORT
```

This demonstrates the complete flow:

```text
Keycloak
  -> JWT token
  -> Gateway security validation
  -> Finding Service
  -> AI Service
  -> Report Service
  -> Security Report
```

## 11. What the project demonstrates

This project demonstrates:

```text
Spring Boot microservices
Spring Cloud Gateway
Eureka Service Discovery
OpenFeign communication
Spring Data JPA
H2 database
Dockerfiles
Docker Compose
Keycloak external IAM
JWT authentication
Role-based authorization
Public and protected endpoints
AI-style security analysis
Report generation
```

## 12. Future AWS extension

The next planned step is to connect the local system to AWS services.

Possible AWS extensions:

```text
AWS S3 for storing generated reports
AWS RDS or DynamoDB for persistent findings
AWS ECS or EC2 for deployment
AWS CloudWatch for monitoring
CI/CD pipeline for automated deployment
```
