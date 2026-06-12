# Demo Steps - Cloud Security AI Assistant

## 1. Start the application

```powershell
docker compose up --build
```

This starts:

```text
discovery-service
gateway-service
finding-service
ai-service
report-service
keycloak
```

## 2. Open Eureka Dashboard

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

## 3. Test public endpoint

```powershell
curl.exe -i http://localhost:8080/public/status
```

Expected:

```text
HTTP/1.1 200
Cloud Security AI Assistant Gateway is running
```

## 4. Test protected endpoint without token

```powershell
curl.exe -i http://localhost:8080/api/findings
```

Expected:

```text
HTTP/1.1 401 Unauthorized
```

## 5. Get JWT token from Keycloak

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

## 6. Test protected findings endpoint with token

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

## 7. Generate protected security report

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

## Demo explanation

This demo shows:

```text
Keycloak authentication
JWT token generation
Gateway JWT validation
Role-based authorization with SECURITY_ANALYST
Service discovery with Eureka
Routing through Spring Cloud Gateway
Finding Service database access
AI Service analysis
Report Service report generation
Docker Compose orchestration
```

## Main architecture sentence

The project is a secured Spring Boot microservices system for cloud security findings. It uses Spring Cloud Gateway, Eureka, OpenFeign, Keycloak, JWT authentication, role-based authorization, H2 database, Docker, and Docker Compose.
