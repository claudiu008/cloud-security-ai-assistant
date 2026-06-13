# Presentation Script - Cloud Security AI Assistant

## 1. Project introduction

This project is called **Cloud Security AI Assistant**.

It is a secured Spring Boot and Spring Cloud microservices application for cloud security findings.

The application simulates AWS GuardDuty-style findings, generates AI-style risk explanations, creates security reports, reads findings from Amazon DynamoDB, and stores generated reports in Amazon S3.

The project is prepared for the Cyber Security Programming laboratory and final exam.

---

## 2. Architecture overview

The application uses a microservices architecture.

Services:

```text
discovery-service  -> Eureka Server
gateway-service    -> API Gateway + Security
finding-service    -> Exposes findings from H2 and DynamoDB
ai-service         -> Generates AI-style analysis
report-service     -> Generates reports and uploads them to S3
keycloak           -> External IAM provider
```

All services run locally using Docker Compose.

---

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

---

## 4. Docker Compose orchestration

The whole system starts with one command:

```powershell
docker compose up
```

or, if rebuilding is needed:

```powershell
docker compose up --build
```

Docker Compose starts all required containers:

```text
discovery-service
gateway-service
finding-service
ai-service
report-service
keycloak
```

This replaces running each Spring Boot service manually.

---

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

This proves that the Gateway is running and public routes work without authentication.

---

## 6. Protected endpoint without token

Then I test a protected endpoint without authentication:

```powershell
curl.exe -i http://localhost:8080/api/findings
```

Expected result:

```text
HTTP/1.1 401 Unauthorized
```

This proves that Spring Security protects the API.

Unauthenticated users cannot access protected routes.

---

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

---

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

I can verify that the token was generated:

```powershell
$token.Length
```

The JWT is then sent to the Gateway using the Authorization header.

---

## 9. Protected local H2 findings endpoint

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

This endpoint reads findings from the local H2 database.

H2 is kept as a local fallback database for development and stable local testing.

This proves that authenticated users with the `SECURITY_ANALYST` role can access protected APIs.

---

## 10. Protected DynamoDB findings endpoint

Next, I call the DynamoDB endpoint:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings/dynamodb" `
  -Headers @{ Authorization = "Bearer $token" }
```

Expected result:

```text
RootCredentialUsage - DescribeRegions
RootCredentialUsage - GetAccountSummary
```

The order can be different because DynamoDB Scan does not guarantee item ordering.

This endpoint reads findings from Amazon DynamoDB.

DynamoDB table:

```text
cloud-security-findings
```

Partition key:

```text
id
```

The flow is:

```text
User
  -> JWT token
  -> Gateway
  -> Finding Service
  -> Amazon DynamoDB
  -> Security Findings
```

This demonstrates the AWS database requirement from the laboratory grading criteria.

---

## 11. Full protected report flow

Then I generate a normal security report:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings/1/report" `
  -Method POST `
  -Headers @{ Authorization = "Bearer $token" }
```

Expected result:

```text
CLOUD SECURITY REPORT
=====================
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

The AI Service generates a risk explanation and recommended actions.

The Report Service creates the final text report.

---

## 12. Amazon S3 storage flow

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

---

## 13. AWS S3 verification

In AWS Console, I open:

```text
S3 -> cloud-security-ai-assistant-bucket -> Objects -> reports/
```

I open the generated report file.

Expected content:

```text
CLOUD SECURITY REPORT
=====================
```

This proves that the application generated a report and stored it in a real AWS S3 bucket.

---

## 14. AWS security explanation

AWS credentials are not stored in Java source code.

They are loaded through environment variables:

```text
AWS_REGION
S3_BUCKET_NAME
DYNAMODB_FINDINGS_TABLE_NAME
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
```

The `.env` file is ignored by Git, so secrets are not pushed to GitHub.

The S3 bucket is private and public access is blocked.

The IAM user used by the application has limited permissions for the project resources.

---

## 15. AWS services implemented

The project currently uses two AWS services:

```text
Amazon DynamoDB
Amazon S3
```

DynamoDB is used as the AWS database for cloud security findings.

S3 is used as an additional AWS service for storing generated reports.

For the laboratory grading criteria:

```text
DynamoDB supports the Grade 5 database requirement.
S3 supports the Grade 6 additional AWS service requirement.
```

---

## 16. Current status for laboratory grading

Grade 5 requires:

```text
Spring Boot + Spring Cloud dependencies
1 AI feature
AWS deployment
AWS RDS or DynamoDB
```

Current status:

```text
Spring Boot        -> implemented
Spring Cloud       -> implemented
AI feature         -> implemented
DynamoDB           -> implemented
AWS deployment     -> not implemented yet
```

So Grade 5 is almost complete, but AWS deployment is still missing.

Grade 6 requires one additional AWS service.

Current additional AWS service:

```text
Amazon S3
```

After AWS deployment is completed, Grade 6 becomes defensible because DynamoDB covers the database requirement and S3 is the additional AWS service.

---

## 17. What the project demonstrates

This project demonstrates:

```text
Spring Boot microservices
Spring Cloud Gateway
Eureka Service Discovery
OpenFeign communication
Spring Data JPA
H2 local fallback database
Amazon DynamoDB database integration
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

---

## 18. Final exam alignment

The project is also aligned with the final exam direction.

It includes:

```text
microservices
Eureka
Gateway
database
Docker
Docker Compose
Spring Security
external IAM
roles and permissions
AWS service integration
AI-assisted architecture and implementation
```

The remaining final exam improvement is AWS deployment.

---

## 19. Remaining next step

The remaining priority is AWS deployment using one accepted deployment service:

```text
ECS
Fargate
EKS
CloudFront
Elastic Beanstalk
```

The planned deployment target is:

```text
AWS Elastic Beanstalk with Docker Compose
```

After that, the project can cover:

```text
Grade 5: Spring Boot + Spring Cloud + AI + AWS deployment + DynamoDB
Grade 6: Grade 5 + S3 additional AWS service
```

---

## 20. Short final presentation sentence

Cloud Security AI Assistant is a secured Spring Boot microservices application that analyzes cloud security findings, generates AI-style risk explanations, reads findings from Amazon DynamoDB, creates security reports, and stores generated reports in Amazon S3. The system is protected with Keycloak, JWT authentication, and role-based authorization, and it currently runs locally using Docker Compose while being prepared for AWS deployment.
