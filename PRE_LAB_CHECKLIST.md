# Pre-Lab Checklist - Cloud Security AI Assistant

## 1. Before leaving for laboratory

Run this once at home, with internet connection:

```powershell
cd "C:\Users\claud\OneDrive\Documents\unitbv\Cyber_Security_Programming\projects\cloud-security-ai-assistant"
docker compose up --build
```

Confirm that all services start correctly.

Open Eureka:

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

---

## 2. Verify AWS configuration

Check that the local `.env` file exists in the project root.

Expected variables:

```env
AWS_REGION=eu-central-1
S3_BUCKET_NAME=cloud-security-ai-assistant-bucket
DYNAMODB_FINDINGS_TABLE_NAME=cloud-security-findings
AWS_ACCESS_KEY_ID=...
AWS_SECRET_ACCESS_KEY=...
```

Important:

```text
.env must not be committed to GitHub.
```

Verify:

```powershell
git status
```

The `.env` file must not appear in Git status.

---

## 3. Verify DynamoDB table

In AWS Console, check:

```text
DynamoDB -> Tables -> cloud-security-findings
```

Expected:

```text
Table status: Active
Partition key: id
Partition key type: String
```

Open:

```text
Explore table items
```

Expected items:

```text
id 1 -> RootCredentialUsage -> DescribeRegions
id 2 -> RootCredentialUsage -> GetAccountSummary
```

---

## 4. Verify S3 bucket

In AWS Console, check:

```text
S3 -> cloud-security-ai-assistant-bucket
```

Expected folder:

```text
reports/
```

The bucket should be private.

Public access should be blocked.

---

## 5. Start project for laboratory

Go to the project folder:

```powershell
cd "C:\Users\claud\OneDrive\Documents\unitbv\Cyber_Security_Programming\projects\cloud-security-ai-assistant"
```

Start the project:

```powershell
docker compose up
```

Use rebuild only if code changed:

```powershell
docker compose up --build
```

---

## 6. Demo order

### Step 1 - Public endpoint

```powershell
curl.exe -i http://localhost:8080/public/status
```

Expected:

```text
HTTP/1.1 200
Cloud Security AI Assistant Gateway is running
```

Explain:

The Gateway is running and this route is public.

---

### Step 2 - Protected endpoint without token

```powershell
curl.exe -i http://localhost:8080/api/findings
```

Expected:

```text
HTTP/1.1 401 Unauthorized
```

Explain:

The API is protected by Spring Security. Without JWT token, access is denied.

---

### Step 3 - Get JWT token

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

Explain:

The `analyst` user authenticates through Keycloak and receives a JWT access token.

---

### Step 4 - Protected H2 endpoint

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings" `
  -Headers @{ Authorization = "Bearer $token" }
```

Expected:

```text
RootCredentialUsage - DescribeRegions
RootCredentialUsage - GetAccountSummary
```

Explain:

This endpoint reads findings from the local H2 database.

H2 is used as a local fallback database.

---

### Step 5 - Protected DynamoDB endpoint

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings/dynamodb" `
  -Headers @{ Authorization = "Bearer $token" }
```

Expected:

```text
RootCredentialUsage - DescribeRegions
RootCredentialUsage - GetAccountSummary
```

Note:

```text
The order may be different because DynamoDB Scan does not guarantee ordering.
```

Explain:

This endpoint reads findings from Amazon DynamoDB.

DynamoDB table:

```text
cloud-security-findings
```

This demonstrates the AWS database requirement.

---

### Step 6 - Generate normal report

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings/1/report" `
  -Method POST `
  -Headers @{ Authorization = "Bearer $token" }
```

Expected:

```text
CLOUD SECURITY REPORT
```

Explain:

This shows the protected microservices flow:

```text
Gateway -> Finding Service -> AI Service -> Report Service
```

---

### Step 7 - Generate and store report in Amazon S3

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings/1/report/store" `
  -Method POST `
  -Headers @{ Authorization = "Bearer $token" }
```

Expected:

```text
message : Report generated and stored successfully
bucket  : cloud-security-ai-assistant-bucket
s3Key   : reports/finding-1-...
```

Explain:

This shows the full protected flow plus AWS S3 integration.

The generated report is stored in a private S3 bucket.

---

### Step 8 - Verify report in S3

Open AWS Console:

```text
S3 -> cloud-security-ai-assistant-bucket -> Objects -> reports/
```

Expected:

```text
finding-1-<timestamp>.txt
```

Open the file and verify:

```text
CLOUD SECURITY REPORT
```

---

## 7. Laboratory grading explanation

Current implemented AWS services:

```text
Amazon DynamoDB
Amazon S3
```

DynamoDB is used as the AWS database for findings.

S3 is used as an additional AWS service for report storage.

Current status:

```text
Spring Boot                    ✅
Spring Cloud                   ✅
AI feature                     ✅
DynamoDB database              ✅
S3 additional AWS service      ✅
Docker / Docker Compose        ✅
Keycloak / JWT / roles         ✅
AWS deployment                 ❌ remaining step
```

The remaining Grade 5 requirement is AWS deployment using one accepted service:

```text
ECS
Fargate
EKS
CloudFront
Elastic Beanstalk
```

Planned target:

```text
Elastic Beanstalk with Docker Compose
```

---

## 8. If something fails

Check containers:

```powershell
docker ps
```

Clean restart:

```powershell
docker compose down
docker compose up
```

If the token returns `401`, generate a new token.

If Eureka does not show all services immediately, wait 30-60 seconds and refresh the browser.

If DynamoDB fails, verify:

```text
.env exists
AWS credentials are correct
DYNAMODB_FINDINGS_TABLE_NAME is cloud-security-findings
DynamoDB table exists
Table region is eu-central-1
IAM user has DynamoDB GetItem and Scan permissions
```

If S3 upload fails, verify:

```text
S3 bucket exists
Bucket region is eu-central-1
IAM user has S3 PutObject permission
```

---

## 9. Files to open during presentation

Useful files:

```text
README.md
LAB_DEMO_GUIDE.md
PRESENTATION_SCRIPT.md
REQUIREMENTS_MAPPING.md
TROUBLESHOOTING.md
docker-compose.yml
gateway-service/src/main/java/com/cld/gateway/config/SecurityConfig.java
keycloak/realm-export.json
finding-service/src/main/java/com/cld/finding/controller/FindingController.java
finding-service/src/main/java/com/cld/finding/service/DynamoDbFindingService.java
finding-service/src/main/java/com/cld/finding/config/DynamoDbConfig.java
report-service/src/main/java/com/cld/report/controller/ReportController.java
report-service/src/main/java/com/cld/report/config/S3Config.java
report-service/src/main/java/com/cld/report/service/S3ReportStorageService.java
```

---

## 10. Main sentence for presentation

Cloud Security AI Assistant is a secured Spring Boot microservices system for analyzing cloud security findings. It uses Eureka for service discovery, Spring Cloud Gateway for routing and security, Keycloak for external IAM, JWT authentication, role-based authorization, H2 as local fallback, Amazon DynamoDB as AWS database, Docker Compose for orchestration, and Amazon S3 for storing generated security reports.
