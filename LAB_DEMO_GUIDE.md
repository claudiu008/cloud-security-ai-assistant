# Laboratory Demo Guide - Cloud Security AI Assistant

This guide contains the exact manual steps for presenting and testing the project during the laboratory.

The goal is to demonstrate:

* Docker Compose orchestration
* Eureka service discovery
* Spring Cloud Gateway routing
* Keycloak authentication
* JWT validation
* Role-based authorization
* Finding Service, AI Service, and Report Service communication
* Amazon S3 report storage

---

## 1. Check Git status before starting

Command:

```powershell
git status
```

Expected result:

```text
nothing to commit, working tree clean
```

What I explain:

Before starting the demo, I verify that the repository is clean and all changes are committed to GitHub.

---

## 2. Stop any previous containers

Command:

```powershell
docker compose down
```

What I explain:

This stops and removes the existing containers from a previous run. It gives me a clean local environment before starting the demo.

Docker images remain available locally, so they do not need to be downloaded again.

---

## 3. Start the full system

Command:

```powershell
docker compose up --build
```

What I explain:

This command builds and starts the complete microservices system.

Docker Compose starts all required containers:

```text
discovery-service
gateway-service
finding-service
ai-service
report-service
keycloak
```

Each service runs in its own container.

Docker Compose is used because the project has multiple services that must run together and communicate through Docker's internal network.

For a faster start, if no code changed, I can use:

```powershell
docker compose up
```

---

## 4. Open Eureka Dashboard

Open in browser:

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

What I explain:

Eureka is used for service discovery.

Each microservice registers itself in Eureka, so other services can discover it dynamically.

This proves that the services are running and registered.

If not all services appear immediately, I wait 30-60 seconds and refresh the page.

---

## 5. Test public endpoint

Command:

```powershell
curl.exe -i http://localhost:8080/public/status
```

Expected result:

```text
HTTP/1.1 200
Cloud Security AI Assistant Gateway is running
```

What I explain:

This endpoint is public and does not require authentication.

It confirms that the Gateway is running and accessible.

---

## 6. Test protected endpoint without token

Command:

```powershell
curl.exe -i http://localhost:8080/api/findings
```

Expected result:

```text
HTTP/1.1 401 Unauthorized
```

What I explain:

This endpoint is protected by Spring Security.

Because I do not send a JWT token, the Gateway rejects the request.

This proves that unauthenticated users cannot access the protected API.

---

## 7. Get JWT token from Keycloak

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

Optional verification:

```powershell
$token.Length
```

Expected result:

```text
A large number, not 0
```

What I explain:

Keycloak is used as an external IAM provider.

The user `analyst` authenticates with Keycloak.

Keycloak returns a JWT access token.

The user has the `SECURITY_ANALYST` role.

The Gateway validates this JWT token before allowing access to protected routes.

---

## 8. Test protected findings endpoint with token

Command:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings" `
  -Headers @{ Authorization = "Bearer $token" }
```

Expected result:

```text
id       : 1
type     : RootCredentialUsage
apiCall  : DescribeRegions
username : root
sourceIp : 86.120.10.55
region   : eu-central-1
severity : LOW

id       : 2
type     : RootCredentialUsage
apiCall  : GetAccountSummary
username : root
sourceIp : 86.120.10.55
region   : eu-central-1
severity : LOW
```

What I explain:

Now I send the JWT token in the `Authorization` header.

The Gateway validates the token and checks that the authenticated user has the `SECURITY_ANALYST` role.

Because the token is valid and the role is correct, the request is forwarded to the Finding Service.

The Finding Service returns the stored cloud security findings from the H2 database.

---

## 9. Generate normal security report

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

What I explain:

This demonstrates the protected microservices flow.

The request goes through:

```text
User
  -> Gateway
  -> Finding Service
  -> AI Service
  -> Report Service
  -> Security Report
```

The Finding Service retrieves the finding from the database.

Then it calls the AI Service to generate a risk explanation and recommended actions.

Then it calls the Report Service to generate the final text report.

---

## 10. Generate and store report in Amazon S3

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

What I explain:

This demonstrates the full protected flow plus AWS integration.

The request goes through:

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

The Report Service generates the report and uploads it to Amazon S3.

The bucket used is:

```text
cloud-security-ai-assistant-bucket
```

The object is stored under:

```text
reports/
```

---

## 11. Verify report in AWS S3

Open in AWS Console:

```text
S3 -> cloud-security-ai-assistant-bucket -> Objects -> reports/
```

Expected object:

```text
reports/finding-1-<timestamp>.txt
```

Open the generated `.txt` file.

Expected content:

```text
CLOUD SECURITY REPORT
=====================
```

What I explain:

The generated report is stored in a private S3 bucket.

Public access is blocked.

AWS credentials are not stored in Java source code.

They are provided through local environment variables from the `.env` file.

The `.env` file is ignored by Git, so secrets are not pushed to GitHub.

---

## 12. Clean stop after demo

Command:

```powershell
docker compose down
```

What I explain:

This stops the local environment cleanly.

The containers are removed, but the Docker images remain on the machine.

The project can be started again later with:

```powershell
docker compose up
```

---

## 13. Main architecture explanation

Cloud Security AI Assistant is a secured Spring Boot microservices system for cloud security findings.

It uses:

```text
Eureka for service discovery
Spring Cloud Gateway for routing and security
Keycloak as external IAM provider
JWT for authentication
Role-based authorization with SECURITY_ANALYST
H2 database for local persistence
OpenFeign for service-to-service communication
Docker Compose for orchestration
Amazon S3 for storing generated reports
```

---

## 14. What the project demonstrates

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

---

## 15. Short final presentation sentence

Cloud Security AI Assistant is a secured Spring Boot microservices application that analyzes cloud security findings, generates AI-style risk explanations, creates security reports, and stores generated reports in Amazon S3. The system is protected with Keycloak, JWT authentication, and role-based authorization, and it runs locally using Docker Compose.
