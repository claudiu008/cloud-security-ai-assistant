# Cloud Security AI Assistant

Cloud Security AI Assistant is a secured Spring Boot microservices application designed to analyze cloud security findings, generate AI-style risk explanations, produce security reports, and store generated reports in Amazon S3.

The project demonstrates a local cloud-security architecture using Spring Boot, Spring Cloud, Eureka Service Discovery, Spring Cloud Gateway, Docker Compose, Keycloak IAM, JWT authentication, role-based authorization, H2 database persistence, OpenFeign service-to-service communication, and AWS S3 integration.

## Architecture

The system contains the following services:

| Service           | Port | Description                                                |
| ----------------- | ---: | ---------------------------------------------------------- |
| discovery-service | 8761 | Eureka service discovery server                            |
| gateway-service   | 8080 | API Gateway and security entry point                       |
| finding-service   | 8081 | Stores and exposes cloud security findings                 |
| ai-service        | 8082 | Generates AI-style security analysis                       |
| report-service    | 8083 | Generates security reports and stores them in Amazon S3    |
| keycloak          | 8084 | External IAM provider for authentication and authorization |

## Main Flow

```text
User
  -> Keycloak login
  -> JWT access token
  -> Spring Cloud Gateway
  -> Finding Service
  -> AI Service
  -> Report Service
  -> Security Report
  -> Amazon S3
```

## Technologies Used

* Java
* Spring Boot
* Spring Cloud
* Eureka Server / Eureka Client
* Spring Cloud Gateway
* OpenFeign
* Spring Data JPA
* H2 Database
* Spring Security
* OAuth2 Resource Server
* JWT
* Keycloak
* Docker
* Docker Compose
* AWS S3
* AWS SDK for Java
* Git / GitHub

## Security

The application uses Keycloak as an external IAM provider.

Configured Keycloak objects:

```text
Realm: cloud-security
Client: cloud-security-gateway
Role: SECURITY_ANALYST
User: analyst
```

The Gateway validates JWT Bearer tokens issued by Keycloak.

Access rules:

```text
/public/**     -> public
/api/**        -> requires valid JWT + SECURITY_ANALYST role
```

Requests without a valid token receive:

```text
401 Unauthorized
```

Authenticated users with the correct role can access the protected API routes.

## AWS S3 Integration

The application stores generated security reports in a private Amazon S3 bucket.

Bucket used:

```text
cloud-security-ai-assistant-bucket
```

Region:

```text
eu-central-1
```

S3 object path format:

```text
reports/finding-<findingId>-<timestamp>.txt
```

Example:

```text
reports/finding-1-20260613-143000.txt
```

The S3 bucket is private and public access is blocked.

AWS credentials are not stored in source code. They are provided through local environment variables.

## Environment Variables

The project uses a local `.env` file for AWS configuration.

Create a `.env` file in the project root:

```env
AWS_REGION=eu-central-1
S3_BUCKET_NAME=cloud-security-ai-assistant-bucket
AWS_ACCESS_KEY_ID=your_access_key_here
AWS_SECRET_ACCESS_KEY=your_secret_key_here
```

Important:

```text
.env must never be committed to GitHub.
```

The `.gitignore` file contains:

```gitignore
.env
.idea/
```

## Running the Project Locally with Docker Compose

From the project root, run:

```powershell
docker compose up --build
```

This starts all services:

```text
discovery-service
gateway-service
finding-service
ai-service
report-service
keycloak
```

If the images are already built and no code changed, you can run:

```powershell
docker compose up
```

To stop everything:

```powershell
docker compose down
```

## Eureka Dashboard

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

## Keycloak Admin Console

Open:

```text
http://localhost:8084
```

Admin credentials for local development:

```text
Username: admin
Password: admin
```

The Keycloak realm is imported automatically from:

```text
keycloak/realm-export.json
```

This makes the local environment reproducible. The realm, client, role, and test user are recreated when Keycloak starts.

## Public Endpoint Test

This endpoint does not require authentication:

```powershell
curl.exe -i http://localhost:8080/public/status
```

Expected result:

```text
HTTP/1.1 200
Cloud Security AI Assistant Gateway is running
```

On Linux:

```bash
curl -i http://localhost:8080/public/status
```

## Protected Endpoint Test Without Token

```powershell
curl.exe -i http://localhost:8080/api/findings
```

Expected result:

```text
HTTP/1.1 401 Unauthorized
```

On Linux:

```bash
curl -i http://localhost:8080/api/findings
```

## Get JWT Token from Keycloak

PowerShell:

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

Linux:

```bash
TOKEN=$(curl -s -X POST "http://localhost:8084/realms/cloud-security/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=cloud-security-gateway" \
  -d "username=analyst" \
  -d "password=analyst123" | python3 -c "import sys, json; print(json.load(sys.stdin)['access_token'])")
```

## Protected Endpoint Test With Token

PowerShell:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings" `
  -Headers @{ Authorization = "Bearer $token" }
```

Linux:

```bash
curl -i http://localhost:8080/api/findings \
  -H "Authorization: Bearer $TOKEN"
```

Expected result:

```text
Finding 1: RootCredentialUsage - DescribeRegions
Finding 2: RootCredentialUsage - GetAccountSummary
```

## Generate Full Protected Security Report

PowerShell:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings/1/report" `
  -Method POST `
  -Headers @{ Authorization = "Bearer $token" }
```

Linux:

```bash
curl -i -X POST http://localhost:8080/api/findings/1/report \
  -H "Authorization: Bearer $TOKEN"
```

Expected result:

```text
CLOUD SECURITY REPORT
=====================

Finding ID: 1
Type: RootCredentialUsage
API Call: DescribeRegions
Username: root
Source IP: 86.120.10.55
Region: eu-central-1
Severity: LOW

Risk Explanation:
...

Recommended Actions:
...
```

## Generate and Store Security Report in Amazon S3

The application can also generate a report and store it in the private S3 bucket.

PowerShell:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings/1/report/store" `
  -Method POST `
  -Headers @{ Authorization = "Bearer $token" }
```

Linux:

```bash
curl -i -X POST http://localhost:8080/api/findings/1/report/store \
  -H "Authorization: Bearer $TOKEN"
```

Expected result:

```text
message : Report generated and stored successfully
bucket  : cloud-security-ai-assistant-bucket
s3Key   : reports/finding-1-...
```

The generated report appears in S3 under:

```text
reports/
```

Example report content:

```text
CLOUD SECURITY REPORT
=====================

Finding ID: 1
Type: RootCredentialUsage
API Call: DescribeRegions
Username: root
Source IP: 86.120.10.55
Region: eu-central-1
Severity: LOW

Risk Explanation:
Security finding detected: RootCredentialUsage. The API call 'DescribeRegions' was executed by user 'root' from IP 86.120.10.55 in region eu-central-1. Severity is LOW.

Recommended Actions:
- Verify whether this activity was expected
- Check AWS CloudTrail for related events
- Review the source IP address
- Apply least privilege permissions
- Escalate the incident if the activity is suspicious
```

## Demo Scenario

The demo finding is inspired by an AWS GuardDuty-style scenario where root credentials are used for API calls such as:

```text
DescribeRegions
GetAccountSummary
```

The application stores these findings, sends them to the AI service for analysis, generates a cloud security report, and can upload that report to Amazon S3.

## Local Development Notes

The H2 database is used for local development and demo purposes.

The demo findings are automatically loaded when `finding-service` starts.

Docker Compose is used to orchestrate all microservices locally.

Inside Docker, services communicate using container service names, for example:

```text
http://discovery-service:8761/eureka
http://keycloak:8080
```

The application uses environment variables for AWS configuration:

```text
AWS_REGION
S3_BUCKET_NAME
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
```

No AWS credentials are hardcoded in Java source code.

## Important Files

```text
docker-compose.yml
keycloak/realm-export.json
gateway-service/src/main/java/com/cld/gateway/config/SecurityConfig.java
finding-service/src/main/java/com/cld/finding/controller/FindingController.java
finding-service/src/main/java/com/cld/finding/client/ReportClient.java
ai-service/src/main/java/com/cld/ai/controller/AiController.java
report-service/src/main/java/com/cld/report/controller/ReportController.java
report-service/src/main/java/com/cld/report/config/S3Config.java
report-service/src/main/java/com/cld/report/service/S3ReportStorageService.java
```

## Project Status

Implemented:

```text
Spring Boot microservices
Spring Cloud Gateway
Eureka Service Discovery
OpenFeign service-to-service communication
H2 database persistence
AI-style analysis service
Report generation service
Amazon S3 report storage
Dockerfiles for all services
Docker Compose orchestration
Keycloak external IAM
JWT authentication
Role-based authorization
Public and protected endpoints
Reproducible Keycloak realm import
```

Implemented AWS service:

```text
Amazon S3 for storing generated security reports
```

Planned cloud extensions:

```text
AWS RDS or DynamoDB for persistent cloud database
AWS deployment using ECS or EC2
CloudWatch monitoring
CI/CD pipeline
```

## Laboratory Summary

Cloud Security AI Assistant currently demonstrates a secured Spring Boot microservices architecture with service discovery, API Gateway routing, database persistence, AI-style analysis, report generation, external IAM with Keycloak, JWT authentication, role-based authorization, Docker Compose orchestration, and Amazon S3 report storage.

The project is functional locally and has started AWS integration through S3.
