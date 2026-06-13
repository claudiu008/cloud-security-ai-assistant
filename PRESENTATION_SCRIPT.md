# Presentation Script - Cloud Security AI Assistant

## 1. Project introduction

This project is called **Cloud Security AI Assistant**.

It is a secured Spring Boot microservices application that analyzes cloud security findings, generates AI-style risk explanations, produces security reports, and stores generated reports in Amazon S3.

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
report-service     -> Generates reports and uploads them to S3
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
docker compose up
```

or, if rebuilding is needed:

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

Then I generate a full protected security report:

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

This demonstrates the protected microservices flow:

```text
Keycloak
  -> JWT token
  -> Gateway security validation
  -> Finding Service
  -> AI Service
  -> Report Service
  -> Security Report
```

## 11. Amazon S3 storage flow

Finally, I generate and store the report in Amazon S3:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings/1/report/store" `
  -Method POST `
  -Headers @{ Authorization = "Bearer $token" }
```

Expected result:

```text
message : Report generated and stored successfully
bucket  : cloud-security-ai-assistant-bucket
s3Key   : reports/finding-1-...
```

This demonstrates the full local and AWS flow:

```text
Keycloak
  -> JWT token
  -> Gateway security validation
  -> Finding Service
  -> AI Service
  -> Report Service
  -> Amazon S3
  -> Stored Security Report
```

The report is stored in a private S3 bucket:

```text
cloud-security-ai-assistant-bucket
```

The object is stored under:

```text
reports/
```

## 12. AWS security explanation

The S3 bucket is private and public access is blocked.

AWS credentials are not stored in Java source code.

They are loaded through environment variables:

```text
AWS_REGION
S3_BUCKET_NAME
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
```

The `.env` file is ignored by Git, so secrets are not pushed to GitHub.

The IAM user used by the application has limited permissions for uploading reports to the project S3 bucket.

## 13. What the project demonstrates

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
Amazon S3 report storage
Environment-based AWS configuration
```

## 14. Current AWS integration

The project currently integrates with:

```text
Amazon S3
```

S3 is used to store generated cloud security reports.

## 15. Future AWS extension

The next planned steps are:

```text
AWS RDS or DynamoDB for persistent findings
AWS ECS or EC2 for deployment
AWS CloudWatch for monitoring
CI/CD pipeline for automated deployment
```
