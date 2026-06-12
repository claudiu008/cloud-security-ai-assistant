# Cloud Security AI Assistant

Cloud Security AI Assistant is a Spring Boot microservices application designed to analyze cloud security findings, generate AI-style risk explanations, and produce security reports.

The project demonstrates a local cloud-security architecture using Spring Boot, Spring Cloud, Eureka Service Discovery, Spring Cloud Gateway, Docker Compose, Keycloak IAM, JWT authentication, and role-based authorization.

## Architecture

The system contains the following services:

| Service           | Port | Description                                                |
| ----------------- | ---: | ---------------------------------------------------------- |
| discovery-service | 8761 | Eureka service discovery server                            |
| gateway-service   | 8080 | API Gateway and security entry point                       |
| finding-service   | 8081 | Stores and exposes cloud security findings                 |
| ai-service        | 8082 | Generates AI-style security analysis                       |
| report-service    | 8083 | Generates security reports                                 |
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

## Protected Endpoint Test Without Token

```powershell
curl.exe -i http://localhost:8080/api/findings
```

Expected result:

```text
HTTP/1.1 401 Unauthorized
```

## Get JWT Token from Keycloak

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

## Protected Endpoint Test With Token

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings" `
  -Headers @{ Authorization = "Bearer $token" }
```

Expected result:

```text
Finding 1: RootCredentialUsage - DescribeRegions
Finding 2: RootCredentialUsage - GetAccountSummary
```

## Generate Full Protected Security Report

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

## Demo Scenario

The demo finding is inspired by an AWS GuardDuty scenario where root credentials are used for API calls such as:

```text
DescribeRegions
GetAccountSummary
```

The application stores these findings, sends them to the AI service for analysis, and generates a cloud security report.

## Local Development Notes

The H2 database is used for local development and demo purposes.

The demo findings are automatically loaded when `finding-service` starts.

Docker Compose is used to orchestrate all microservices locally.

Inside Docker, services communicate using container service names, for example:

```text
http://discovery-service:8761/eureka
http://keycloak:8080
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
Dockerfiles for all services
Docker Compose orchestration
Keycloak external IAM
JWT authentication
Role-based authorization
Public and protected endpoints
```

Planned cloud extensions:

```text
AWS S3 for report storage
AWS RDS or DynamoDB for persistent cloud database
AWS deployment using ECS or EC2
CloudWatch monitoring
CI/CD pipeline
```
