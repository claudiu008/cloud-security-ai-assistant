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

## 2. Verify AWS S3 configuration

Check that the local `.env` file exists in the project root.

Expected variables:

```env
AWS_REGION=eu-central-1
S3_BUCKET_NAME=cloud-security-ai-assistant-bucket
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

Expected:

```text
nothing to commit, working tree clean
```

The `.env` file should not appear in Git status.

## 3. Stop the system cleanly after testing

After testing:

```powershell
Ctrl + C
docker compose down
```

The Docker images remain on the laptop, so the project can start faster later.

## 4. At laboratory

Go to the project folder:

```powershell
cd "C:\Users\claud\OneDrive\Documents\unitbv\Cyber_Security_Programming\projects\cloud-security-ai-assistant"
```

Start the project:

```powershell
docker compose up
```

Use `--build` only if code changed:

```powershell
docker compose up --build
```

## 5. Demo order

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

### Step 4 - Protected endpoint with token

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

The Gateway validates the JWT and checks the `SECURITY_ANALYST` role.

### Step 5 - Generate normal report

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

This shows the protected microservices flow: Gateway, Finding Service, AI Service, Report Service.

### Step 6 - Generate and store report in Amazon S3

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

This shows the full protected flow plus AWS S3 integration. The generated report is stored in a private S3 bucket.

### Step 7 - Verify report in S3

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

## 6. If something fails

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

If S3 upload fails, verify:

```text
.env exists
AWS credentials are correct
S3 bucket exists
Bucket region is eu-central-1
IAM user has s3:PutObject permission
```

## 7. Files to open during presentation

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
report-service/src/main/java/com/cld/report/controller/ReportController.java
report-service/src/main/java/com/cld/report/config/S3Config.java
report-service/src/main/java/com/cld/report/service/S3ReportStorageService.java
```

## 8. Main sentence for presentation

Cloud Security AI Assistant is a secured Spring Boot microservices system for analyzing cloud security findings. It uses Eureka for service discovery, Spring Cloud Gateway for routing and security, Keycloak for external IAM, JWT for authentication, role-based authorization, H2 database, Docker Compose for orchestration, and Amazon S3 for storing generated security reports.
