# Cloud Security AI Assistant

Cloud Security AI Assistant is a secured Spring Boot and Spring Cloud microservices application for analyzing cloud security findings.

The application simulates AWS GuardDuty-style security findings, generates AI-style risk explanations, creates security reports, reads findings from Amazon DynamoDB, and stores generated reports in Amazon S3.

The project is designed for the Cyber Security Programming laboratory and final exam.

---

## 1. Main features

The project includes:

```text
Spring Boot microservices
Spring Cloud Eureka service discovery
Spring Cloud Gateway routing
OpenFeign service-to-service communication
Spring Security
Keycloak external IAM
JWT authentication
Role-based authorization
Local H2 database fallback
Amazon DynamoDB integration
Amazon S3 report storage
AI-style security analysis
Report generation
Dockerfiles
Docker Compose orchestration
```

---

## 2. Architecture

The application is composed of the following services:

| Service           | Port | Purpose                                                        |
| ----------------- | ---: | -------------------------------------------------------------- |
| discovery-service | 8761 | Eureka Server for service discovery                            |
| gateway-service   | 8080 | API Gateway, routing, JWT validation, role-based authorization |
| finding-service   | 8081 | Exposes security findings from H2 and DynamoDB                 |
| ai-service        | 8082 | Generates AI-style security analysis                           |
| report-service    | 8083 | Generates reports and uploads them to Amazon S3                |
| keycloak          | 8084 | External IAM provider                                          |

---

## 3. Architecture flow

### Local H2 findings flow

```text
User
  -> Gateway
  -> Finding Service
  -> H2 Database
  -> Security Findings
```

Endpoint:

```text
GET /api/findings
```

---

### DynamoDB findings flow

```text
User
  -> JWT token
  -> Gateway
  -> Finding Service
  -> Amazon DynamoDB
  -> Security Findings
```

Endpoint:

```text
GET /api/findings/dynamodb
```

DynamoDB table:

```text
cloud-security-findings
```

Partition key:

```text
id
```

---

### Report generation flow

```text
User
  -> JWT token
  -> Gateway
  -> Finding Service
  -> AI Service
  -> Report Service
  -> Security Report
```

Endpoint:

```text
POST /api/findings/1/report
```

---

### S3 report storage flow

```text
User
  -> JWT token
  -> Gateway
  -> Finding Service
  -> AI Service
  -> Report Service
  -> Amazon S3
  -> Stored Security Report
```

Endpoint:

```text
POST /api/findings/1/report/store
```

S3 bucket:

```text
cloud-security-ai-assistant-bucket
```

S3 folder:

```text
reports/
```

---

## 4. Implemented AWS services

The project currently integrates with:

```text
Amazon DynamoDB
Amazon S3
```

### Amazon DynamoDB

DynamoDB is used as the AWS database for security findings.

Table:

```text
cloud-security-findings
```

Example endpoint:

```text
GET /api/findings/dynamodb
```

This supports the laboratory requirement:

```text
AWS RDS or DynamoDB
```

---

### Amazon S3

S3 is used to store generated security reports.

Bucket:

```text
cloud-security-ai-assistant-bucket
```

Example endpoint:

```text
POST /api/findings/1/report/store
```

This supports the additional AWS service requirement.

---

## 5. Laboratory grading status

### Grade 5 requirement

Requirement:

```text
Spring Boot + Spring Cloud dependencies
1 AI feature
Deployed on AWS
AWS RDS or DynamoDB
```

Current status:

| Requirement    | Status              |
| -------------- | ------------------- |
| Spring Boot    | Implemented         |
| Spring Cloud   | Implemented         |
| AI feature     | Implemented         |
| AWS DynamoDB   | Implemented         |
| AWS deployment | Not implemented yet |

Conclusion:

```text
Grade 5 is almost complete, but AWS deployment is still required.
```

---

### Grade 6 requirement

Requirement:

```text
Grade 5 + one additional AWS service
```

Current additional AWS service:

```text
Amazon S3
```

Conclusion:

```text
After AWS deployment is completed, Grade 6 becomes defensible because DynamoDB covers the database requirement and S3 is the additional AWS service.
```

---

### Grade 7 and Grade 8

Possible future AWS services:

```text
AWS CloudWatch
AWS Secrets Manager
```

Current status:

```text
Not implemented yet.
```

---

### Grade 9

Requirement:

```text
AWS CI/CD + Docker
or
AWS CDK
```

Current status:

```text
Docker is implemented.
AWS CI/CD or AWS CDK is not implemented yet.
```

Possible future implementation:

```text
GitHub Actions + AWS deployment
```

---

## 6. Security

The application uses Keycloak as an external IAM provider.

Configured IAM objects:

```text
Realm: cloud-security
Client: cloud-security-gateway
User: analyst
Role: SECURITY_ANALYST
```

Public endpoint:

```text
GET /public/status
```

Protected endpoints:

```text
GET /api/findings
GET /api/findings/dynamodb
POST /api/findings/1/report
POST /api/findings/1/report/store
```

Without JWT token:

```text
HTTP 401 Unauthorized
```

With valid JWT token and `SECURITY_ANALYST` role:

```text
HTTP 200 OK
```

---

## 7. Environment variables

The project uses environment variables for AWS configuration.

Local `.env` file:

```env
AWS_REGION=eu-central-1
S3_BUCKET_NAME=cloud-security-ai-assistant-bucket
DYNAMODB_FINDINGS_TABLE_NAME=cloud-security-findings
AWS_ACCESS_KEY_ID=...
AWS_SECRET_ACCESS_KEY=...
```

Important:

```text
The .env file must not be committed to GitHub.
```

The `.gitignore` file should include:

```gitignore
.env
.idea/
```

---

## 8. Running the project locally

Start the full system:

```powershell
docker compose up --build
```

If nothing changed, use:

```powershell
docker compose up
```

Stop the system:

```powershell
docker compose down
```

---

## 9. Eureka Dashboard

Open:

```text
http://localhost:8761
```

Expected registered services:

```text
GATEWAY-SERVICE
FINDING-SERVICE
AI-SERVICE
REPORT-SERVICE
```

---

## 10. Public endpoint test

Command:

```powershell
curl.exe -i http://localhost:8080/public/status
```

Expected result:

```text
HTTP/1.1 200
Cloud Security AI Assistant Gateway is running
```

---

## 11. Protected endpoint without token

Command:

```powershell
curl.exe -i http://localhost:8080/api/findings
```

Expected result:

```text
HTTP/1.1 401 Unauthorized
```

---

## 12. Get JWT token from Keycloak

Command:

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

Optional check:

```powershell
$token.Length
```

Expected:

```text
A large number, not 0
```

---

## 13. Test local H2 findings endpoint

Command:

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

This endpoint reads from the local H2 database.

---

## 14. Test DynamoDB findings endpoint

Command:

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

Note:

```text
The order may be different because DynamoDB Scan does not guarantee item ordering.
```

This endpoint reads from Amazon DynamoDB.

---

## 15. Generate normal security report

Command:

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

---

## 16. Generate and store report in S3

Command:

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

Then verify in AWS Console:

```text
S3 -> cloud-security-ai-assistant-bucket -> Objects -> reports/
```

Open the generated `.txt` file.

Expected content:

```text
CLOUD SECURITY REPORT
=====================
```

---

## 17. Current demo order

Recommended laboratory demo flow:

```text
1. Start project with Docker Compose
2. Show Eureka Dashboard
3. Test public endpoint
4. Test protected endpoint without token
5. Get JWT token from Keycloak
6. Test protected H2 endpoint
7. Test protected DynamoDB endpoint
8. Generate normal security report
9. Generate and store report in S3
10. Verify generated report in AWS S3 Console
```

---

## 18. Current strongest explanation

Cloud Security AI Assistant is a secured Spring Boot microservices application that analyzes cloud security findings, generates AI-style risk explanations, creates reports, reads findings from Amazon DynamoDB, and stores generated reports in Amazon S3.

The system is protected using Keycloak, JWT authentication, and role-based authorization.

It runs locally using Docker Compose and is prepared for AWS deployment.

---

## 19. Remaining priority

The remaining priority for the laboratory is:

```text
Deploy the application on AWS using one accepted deployment service.
```

Planned deployment target:

```text
AWS Elastic Beanstalk with Docker Compose
```

After this deployment step, the project can cover:

```text
Grade 5: Spring Boot + Spring Cloud + AI + AWS deployment + DynamoDB
Grade 6: Grade 5 + S3 additional AWS service
```
