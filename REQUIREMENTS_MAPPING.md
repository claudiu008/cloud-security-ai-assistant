# Requirements Mapping - Cloud Security AI Assistant

This document maps the current implementation of Cloud Security AI Assistant to the laboratory and final exam requirements.

---

## 1. Current project status

Cloud Security AI Assistant is a Spring Boot / Spring Cloud microservices application for cloud security findings.

The application currently includes:

```text
Spring Boot microservices
Spring Cloud Eureka
Spring Cloud Gateway
OpenFeign service-to-service communication
H2 local database
AWS DynamoDB integration
AWS S3 integration
AI-style security analysis
Report generation
Dockerfiles
Docker Compose orchestration
Keycloak external IAM
JWT authentication
Role-based authorization
```

---

## 2. Implemented services

| Service           | Purpose                                                        |
| ----------------- | -------------------------------------------------------------- |
| discovery-service | Eureka Server for service discovery                            |
| gateway-service   | API Gateway, routing, JWT validation, role-based authorization |
| finding-service   | Stores and exposes security findings                           |
| ai-service        | Generates AI-style risk explanation and recommended actions    |
| report-service    | Generates security reports and stores reports in S3            |
| keycloak          | External IAM provider for authentication and roles             |

---

## 3. Spring Boot and Spring Cloud requirement

Requirement:

```text
The application is developed using Spring Boot and Spring Cloud dependencies.
```

Status:

```text
Implemented ✅
```

Implemented using:

```text
Spring Boot
Spring Cloud Eureka
Spring Cloud Gateway
Spring Cloud OpenFeign
Spring Data JPA
```

Evidence:

```text
discovery-service
gateway-service
finding-service
ai-service
report-service
```

---

## 4. Microservices requirement

Requirement:

```text
The project must follow microservices principles.
```

Status:

```text
Implemented ✅
```

The project has multiple independent services:

```text
discovery-service
gateway-service
finding-service
ai-service
report-service
```

Each service has its own Spring Boot application and Dockerfile.

The services communicate through:

```text
Eureka service discovery
Spring Cloud Gateway routing
OpenFeign clients
```

---

## 5. AI feature requirement

Requirement:

```text
The application must contain 1 AI feature.
```

Status:

```text
Implemented ✅
```

Implemented feature:

```text
AI-style cloud security finding analysis
```

The `ai-service` receives a security finding and generates:

```text
risk explanation
recommended actions
```

Example finding:

```text
RootCredentialUsage
DescribeRegions
root
86.120.10.55
eu-central-1
LOW
```

---

## 6. Database requirement

Requirement:

```text
Use AWS RDS or DynamoDB.
```

Status:

```text
Implemented with Amazon DynamoDB ✅
```

Implemented AWS database:

```text
Amazon DynamoDB
```

Table:

```text
cloud-security-findings
```

Partition key:

```text
id
```

Type:

```text
String
```

The application can read findings from DynamoDB using:

```text
GET /api/findings/dynamodb
```

The DynamoDB flow is:

```text
User
  -> Gateway
  -> Finding Service
  -> Amazon DynamoDB
  -> Security Findings
```

This supports the Grade 5 database requirement.

---

## 7. Local fallback database

The project still keeps H2 for local fallback and stable demo testing.

Local endpoint:

```text
GET /api/findings
```

This reads from the local H2 database.

AWS DynamoDB endpoint:

```text
GET /api/findings/dynamodb
```

This reads from Amazon DynamoDB.

This separation is intentional because it keeps the local demo stable while also proving AWS database integration.

---

## 8. AWS S3 integration

Implemented AWS service:

```text
Amazon S3
```

Purpose:

```text
Store generated cloud security reports.
```

Bucket:

```text
cloud-security-ai-assistant-bucket
```

S3 folder:

```text
reports/
```

Endpoint:

```text
POST /api/findings/1/report/store
```

Flow:

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

Status:

```text
Implemented and tested ✅
```

This can be used as an additional AWS service for Grade 6.

---

## 9. Security requirement

The application implements security using:

```text
Spring Security
Keycloak
JWT access tokens
Role-based authorization
```

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

Without token:

```text
HTTP 401 Unauthorized
```

With valid JWT token and role:

```text
HTTP 200 OK
```

Status:

```text
Implemented ✅
```

---

## 10. Docker requirement

The project includes Docker support.

Implemented:

```text
Dockerfile for each Spring Boot service
Docker Compose for orchestration
```

Docker Compose starts:

```text
discovery-service
gateway-service
finding-service
ai-service
report-service
keycloak
```

Start command:

```powershell
docker compose up --build
```

Status:

```text
Implemented ✅
```

---

## 11. AWS deployment requirement

Requirement for Grade 5:

```text
The application is deployed on AWS using ECS, Fargate, EKS, CloudFront, or Beanstalk.
```

Current status:

```text
Not implemented yet ❌
```

The application currently runs locally with Docker Compose.

Planned deployment target:

```text
AWS Elastic Beanstalk with Docker Compose
```

Reason:

```text
Elastic Beanstalk is explicitly accepted by the laboratory requirement and is faster to prepare for a Docker Compose based project than a full ECS/Fargate setup.
```

This is the remaining required step for completing Grade 5.

---

## 12. Laboratory grading mapping

### Grade 5

Requirement:

```text
Spring Boot + Spring Cloud dependencies
1 AI feature
Deployed on AWS
AWS RDS or DynamoDB
```

Current status:

| Requirement    | Status                |
| -------------- | --------------------- |
| Spring Boot    | ✅ Implemented         |
| Spring Cloud   | ✅ Implemented         |
| AI feature     | ✅ Implemented         |
| AWS DynamoDB   | ✅ Implemented         |
| AWS deployment | ❌ Not implemented yet |

Conclusion:

```text
Grade 5 is not fully complete yet because AWS deployment is still missing.
```

---

### Grade 6

Requirement:

```text
Grade 5 + one additional AWS service
```

Current additional AWS service:

```text
Amazon S3
```

Status:

```text
S3 implemented ✅
```

Conclusion:

```text
Grade 6 becomes defensible after AWS deployment is completed, because DynamoDB covers the database requirement and S3 is an additional AWS service.
```

---

### Grade 7

Requirement:

```text
Grade 6 + one more additional AWS service
Total: 4 AWS services
```

Current AWS services:

```text
Amazon DynamoDB
Amazon S3
```

Planned possible additional service:

```text
AWS CloudWatch
```

Current status:

```text
Not complete yet ❌
```

---

### Grade 8

Requirement:

```text
Grade 7 + one more additional AWS service
Total: 5 AWS services
```

Possible future services:

```text
AWS CloudWatch
AWS Secrets Manager
```

Current status:

```text
Not complete yet ❌
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
Docker implemented ✅
AWS CI/CD not implemented yet ❌
AWS CDK not implemented yet ❌
```

Possible future implementation:

```text
GitHub Actions + AWS deployment
```

---

## 13. Final exam mapping

The project is also aligned with the final exam direction.

Expected final exam concepts:

```text
Spring Boot application
Spring Cloud dependencies
Microservices principles
Eureka
Gateway
Database
Docker image
Docker Compose or Kubernetes
Spring Security
External IAM application
Roles and permissions
AWS deployment
AI-assisted code and architecture generation
```

Current status:

| Requirement             | Status             |
| ----------------------- | ------------------ |
| Spring Boot             | ✅                  |
| Spring Cloud            | ✅                  |
| Microservices           | ✅                  |
| Eureka                  | ✅                  |
| Gateway                 | ✅                  |
| Database                | ✅ H2 + DynamoDB    |
| Docker images           | ✅                  |
| Docker Compose          | ✅                  |
| Spring Security         | ✅                  |
| External IAM            | ✅ Keycloak         |
| Roles and permissions   | ✅ SECURITY_ANALYST |
| AWS service integration | ✅ DynamoDB + S3    |
| AWS deployment          | ❌ Planned          |

---

## 14. Current strongest demo flow

Recommended laboratory demo flow:

```text
1. Start project with Docker Compose
2. Show Eureka service discovery
3. Call public endpoint
4. Call protected endpoint without token -> 401
5. Get JWT token from Keycloak
6. Call protected H2 findings endpoint
7. Call protected DynamoDB findings endpoint
8. Generate normal report
9. Generate and store report in S3
10. Verify report in AWS S3 Console
```

This demonstrates:

```text
microservices
service discovery
gateway routing
security
external IAM
JWT authentication
role-based authorization
local database
AWS DynamoDB
AWS S3
Docker Compose
```

---

## 15. Remaining priority

The next priority for laboratory grading is:

```text
Deploy the application on AWS using Elastic Beanstalk.
```

After that, Grade 5 becomes complete, and Grade 6 becomes defensible because S3 is already implemented as an additional AWS service.
