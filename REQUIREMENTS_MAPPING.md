# Requirements Mapping - Cloud Security AI Assistant

## Laboratory Requirements Mapping

| Requirement                    | Status              | Implementation                                                                  |
| ------------------------------ | ------------------- | ------------------------------------------------------------------------------- |
| Spring Boot application        | Implemented         | All services are Spring Boot applications                                       |
| Spring Cloud dependencies      | Implemented         | Eureka Server, Eureka Client, Spring Cloud Gateway, OpenFeign                   |
| Microservices architecture     | Implemented         | discovery-service, gateway-service, finding-service, ai-service, report-service |
| Service discovery              | Implemented         | Eureka Server in discovery-service                                              |
| API Gateway                    | Implemented         | Spring Cloud Gateway in gateway-service                                         |
| Database                       | Implemented locally | H2 database in finding-service                                                  |
| AI feature                     | Implemented locally | ai-service generates AI-style security risk explanations                        |
| Report generation              | Implemented         | report-service generates cloud security reports                                 |
| AWS service integration        | Implemented         | Amazon S3 stores generated security reports                                     |
| Docker images                  | Implemented         | Dockerfile for each service                                                     |
| Docker Compose orchestration   | Implemented         | docker-compose.yml starts the full system                                       |
| Security                       | Implemented         | Spring Security in gateway-service                                              |
| External IAM                   | Implemented         | Keycloak                                                                        |
| JWT authentication             | Implemented         | Gateway validates JWT tokens issued by Keycloak                                 |
| Role-based authorization       | Implemented         | Access to `/api/**` requires `SECURITY_ANALYST` role                            |
| Public/protected endpoint demo | Implemented         | `/public/status` public, `/api/findings` protected                              |
| AWS deployment                 | Planned             | ECS or EC2 deployment planned                                                   |
| CI/CD                          | Planned             | GitHub Actions or AWS pipeline planned                                          |

## Final Exam Requirements Mapping

| Requirement                  | Status              | Implementation                          |
| ---------------------------- | ------------------- | --------------------------------------- |
| Spring Boot app              | Implemented         | Multiple Spring Boot services           |
| Spring Cloud dependencies    | Implemented         | Eureka, Gateway, OpenFeign              |
| Eureka                       | Implemented         | discovery-service                       |
| Gateway                      | Implemented         | gateway-service                         |
| Database                     | Implemented locally | H2 database                             |
| Docker image                 | Implemented         | Each service has Dockerfile             |
| Docker Compose or Kubernetes | Implemented         | Docker Compose                          |
| Spring Security              | Implemented         | Gateway security configuration          |
| External IAM                 | Implemented         | Keycloak                                |
| Users, roles, rights         | Implemented         | user `analyst`, role `SECURITY_ANALYST` |
| Secure API access            | Implemented         | JWT + role-based authorization          |
| AI component                 | Implemented locally | AI-style finding analysis               |
| Report generation            | Implemented         | report-service generates reports        |
| AWS service                  | Implemented         | Amazon S3 stores generated reports      |
| Architecture explanation     | Implemented         | README, demo guide, presentation script |
| AWS deployment               | Planned             | Next phase                              |

## Current Local and AWS Demo

The current demo proves:

```text
1. Docker Compose starts all services.
2. Eureka shows all microservices registered.
3. Public endpoint works without token.
4. Protected endpoint returns 401 without token.
5. Keycloak generates JWT token.
6. Gateway validates token.
7. Gateway checks SECURITY_ANALYST role.
8. Finding Service returns cloud security findings.
9. Finding Service calls AI Service.
10. Finding Service calls Report Service.
11. Final security report is generated.
12. Report Service uploads the generated report to Amazon S3.
13. The report appears in the private S3 bucket under reports/.
```

## Current Architecture

```text
User
  -> Keycloak authentication
  -> JWT token
  -> Spring Cloud Gateway
  -> Finding Service
  -> H2 Database
  -> AI Service
  -> Report Service
  -> Amazon S3
  -> Stored Security Report
```

## Implemented Services

### discovery-service

Purpose:

```text
Eureka Server for service discovery.
```

Implemented features:

```text
Registers and exposes microservice registry.
Allows Gateway and services to discover each other.
Runs on port 8761.
```

### gateway-service

Purpose:

```text
Central API Gateway and security entry point.
```

Implemented features:

```text
Routes requests to internal services.
Validates JWT tokens from Keycloak.
Checks SECURITY_ANALYST role.
Exposes public and protected endpoints.
Runs on port 8080.
```

Security rules:

```text
/public/** -> public
/api/**    -> requires JWT + SECURITY_ANALYST role
```

### finding-service

Purpose:

```text
Stores and exposes cloud security findings.
```

Implemented features:

```text
Uses H2 database.
Loads demo findings automatically.
Exposes findings API.
Calls AI Service through OpenFeign.
Calls Report Service through OpenFeign.
Runs on port 8081.
```

Demo findings:

```text
RootCredentialUsage - DescribeRegions
RootCredentialUsage - GetAccountSummary
```

### ai-service

Purpose:

```text
Generates AI-style security risk analysis.
```

Implemented features:

```text
Receives security finding data.
Generates risk explanation.
Generates recommended security actions.
Runs on port 8082.
```

### report-service

Purpose:

```text
Generates security reports and stores them in Amazon S3.
```

Implemented features:

```text
Generates text-based cloud security reports.
Provides normal report generation endpoint.
Provides report generation and S3 storage endpoint.
Uses AWS SDK for Java.
Uploads reports to private S3 bucket.
Runs on port 8083.
```

S3 bucket:

```text
cloud-security-ai-assistant-bucket
```

S3 object path:

```text
reports/finding-<findingId>-<timestamp>.txt
```

### keycloak

Purpose:

```text
External IAM provider.
```

Implemented features:

```text
Provides authentication.
Issues JWT access tokens.
Manages realm, client, user, and role.
Imported automatically through Docker Compose.
Runs on port 8084.
```

Configured IAM objects:

```text
Realm: cloud-security
Client: cloud-security-gateway
User: analyst
Role: SECURITY_ANALYST
```

## AWS Integration

### Implemented AWS Service: Amazon S3

The project uses Amazon S3 to store generated cloud security reports.

Implemented behavior:

```text
1. User authenticates through Keycloak.
2. User calls protected report storage endpoint.
3. Gateway validates JWT and role.
4. Finding Service retrieves the finding.
5. Finding Service calls AI Service.
6. Finding Service calls Report Service.
7. Report Service generates report text.
8. Report Service uploads the report to Amazon S3.
9. The generated report appears in the private S3 bucket.
```

Endpoint:

```text
POST /api/findings/{id}/report/store
```

Example result:

```text
message: Report generated and stored successfully
bucket: cloud-security-ai-assistant-bucket
s3Key: reports/finding-1-...
```

Security:

```text
The S3 bucket is private.
Block Public Access is enabled.
AWS credentials are not hardcoded.
AWS credentials are loaded from local environment variables.
```

## Environment and Secret Management

The project uses a local `.env` file for AWS configuration.

Environment variables:

```text
AWS_REGION
S3_BUCKET_NAME
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
```

The `.env` file is ignored by Git:

```text
.env
```

This prevents AWS credentials from being committed to GitHub.

## Docker Mapping

| Component         | Docker Support     |
| ----------------- | ------------------ |
| discovery-service | Dockerfile         |
| gateway-service   | Dockerfile         |
| finding-service   | Dockerfile         |
| ai-service        | Dockerfile         |
| report-service    | Dockerfile         |
| keycloak          | Docker image       |
| full system       | docker-compose.yml |

The complete system starts with:

```bash
docker compose up --build
```

or, if images are already built:

```bash
docker compose up
```

## Security Mapping

| Security Requirement  | Implementation                         |
| --------------------- | -------------------------------------- |
| External IAM          | Keycloak                               |
| Authentication        | JWT token from Keycloak                |
| Authorization         | SECURITY_ANALYST role                  |
| Gateway security      | Spring Security OAuth2 Resource Server |
| Public endpoint       | `/public/status`                       |
| Protected endpoints   | `/api/**`                              |
| Unauthorized behavior | 401 Unauthorized                       |
| Credential safety     | AWS credentials in `.env`, not in code |

## Demo Commands

### Public endpoint

```bash
curl -i http://localhost:8080/public/status
```

Expected:

```text
HTTP/1.1 200
Cloud Security AI Assistant Gateway is running
```

### Protected endpoint without token

```bash
curl -i http://localhost:8080/api/findings
```

Expected:

```text
HTTP/1.1 401 Unauthorized
```

### Get token

```bash
TOKEN=$(curl -s -X POST "http://localhost:8084/realms/cloud-security/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=cloud-security-gateway" \
  -d "username=analyst" \
  -d "password=analyst123" | python3 -c "import sys, json; print(json.load(sys.stdin)['access_token'])")
```

### Protected findings endpoint

```bash
curl -i http://localhost:8080/api/findings \
  -H "Authorization: Bearer $TOKEN"
```

Expected:

```text
RootCredentialUsage - DescribeRegions
RootCredentialUsage - GetAccountSummary
```

### Generate normal report

```bash
curl -i -X POST http://localhost:8080/api/findings/1/report \
  -H "Authorization: Bearer $TOKEN"
```

Expected:

```text
CLOUD SECURITY REPORT
```

### Generate and store report in S3

```bash
curl -i -X POST http://localhost:8080/api/findings/1/report/store \
  -H "Authorization: Bearer $TOKEN"
```

Expected:

```text
message: Report generated and stored successfully
bucket: cloud-security-ai-assistant-bucket
s3Key: reports/finding-1-...
```

## Current Project Status

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
AWS environment configuration
```

Planned:

```text
AWS RDS or DynamoDB for persistent cloud database
AWS deployment using ECS or EC2
CloudWatch monitoring
CI/CD pipeline
```

## Short Evaluation Summary

Cloud Security AI Assistant currently satisfies the core local microservices, Docker, security, IAM, database, AI-style analysis, report generation, and AWS service integration requirements.

The project now includes Amazon S3 integration for storing generated security reports.

The next phase is full AWS deployment and optional migration from local H2 database to AWS RDS or DynamoDB.
