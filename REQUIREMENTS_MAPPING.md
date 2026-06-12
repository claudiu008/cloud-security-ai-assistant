# Requirements Mapping - Cloud Security AI Assistant

## Laboratory Requirements Mapping

| Requirement                    | Status              | Implementation                                                                  |
| ------------------------------ | ------------------- | ------------------------------------------------------------------------------- |
| Spring Boot application        | Implemented         | All services are Spring Boot applications                                       |
| Spring Cloud dependencies      | Implemented         | Eureka Client, Eureka Server, Spring Cloud Gateway, OpenFeign                   |
| Microservices architecture     | Implemented         | discovery-service, gateway-service, finding-service, ai-service, report-service |
| Service discovery              | Implemented         | Eureka Server                                                                   |
| API Gateway                    | Implemented         | Spring Cloud Gateway                                                            |
| Database                       | Implemented locally | H2 database in finding-service                                                  |
| AI feature                     | Implemented locally | ai-service generates AI-style security risk explanations                        |
| Docker images                  | Implemented         | Dockerfile for each service                                                     |
| Docker Compose orchestration   | Implemented         | docker-compose.yml starts the full system                                       |
| Security                       | Implemented         | Spring Security in gateway-service                                              |
| External IAM                   | Implemented         | Keycloak                                                                        |
| JWT authentication             | Implemented         | Gateway validates JWT tokens issued by Keycloak                                 |
| Role-based authorization       | Implemented         | Access to `/api/**` requires `SECURITY_ANALYST` role                            |
| Public/protected endpoint demo | Implemented         | `/public/status` public, `/api/findings` protected                              |
| Report generation              | Implemented         | report-service generates security report                                        |
| AWS service integration        | Planned             | S3 / RDS / ECS planned for cloud extension                                      |
| AWS deployment                 | Planned             | ECS or EC2 planned                                                              |
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
| Architecture explanation     | Implemented         | README, demo guide, presentation script |
| AWS deployment               | Planned             | Next phase                              |

## Current Local Demo

The local demo proves:

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
  -> Security Report
```

## What is still planned for AWS

The local system is complete and ready for cloud extension.

Planned AWS steps:

```text
1. Create AWS Budget alert.
2. Add AWS S3 for report storage.
3. Optionally replace H2 with AWS RDS PostgreSQL or DynamoDB.
4. Deploy containers using AWS ECS or EC2.
5. Add CloudWatch logs/monitoring.
6. Add CI/CD pipeline.
```

## Short Evaluation Summary

Cloud Security AI Assistant currently satisfies the core local microservices, Docker, security, IAM, database, AI-style analysis, and report generation requirements.

The next phase is AWS integration and deployment.
