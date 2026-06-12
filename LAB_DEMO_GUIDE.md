# Laboratory Demo Guide - Cloud Security AI Assistant

## 1. Start the system

Command:

```bash
docker compose up
```

What I explain:

This command starts the entire microservices system using Docker Compose. Each service runs in its own container: Eureka, Gateway, Finding Service, AI Service, Report Service, and Keycloak.

Docker Compose is used because the project has multiple services that must run together and communicate through Docker's internal network.

## 2. Open Eureka Dashboard

Open in browser:

```text
http://localhost:8761
```

What I explain:

Eureka is used for service discovery. Each microservice registers itself in Eureka, so other services can find it dynamically.

Expected services:

```text
GATEWAY-SERVICE
FINDING-SERVICE
AI-SERVICE
REPORT-SERVICE
```

This proves that the microservices are running and registered.

## 3. Test public endpoint

Command:

```bash
curl -i http://localhost:8080/public/status
```

What I explain:

This endpoint is public and does not require authentication. It is used only to confirm that the Gateway is running.

Expected result:

```text
HTTP/1.1 200
Cloud Security AI Assistant Gateway is running
```

## 4. Test protected endpoint without token

Command:

```bash
curl -i http://localhost:8080/api/findings
```

What I explain:

This endpoint is protected by Spring Security. Because I do not send a JWT token, the Gateway rejects the request.

Expected result:

```text
HTTP/1.1 401 Unauthorized
```

This proves that unauthenticated users cannot access the API.

## 5. Get JWT token from Keycloak

Command:

```bash
TOKEN=$(curl -s -X POST "http://localhost:8084/realms/cloud-security/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=cloud-security-gateway" \
  -d "username=analyst" \
  -d "password=analyst123" | python3 -c "import sys, json; print(json.load(sys.stdin)['access_token'])")
```

What I explain:

Keycloak is used as an external IAM provider. The user `analyst` authenticates with Keycloak, and Keycloak returns a JWT access token.

The user has the `SECURITY_ANALYST` role.

The Gateway validates this JWT token before allowing access to protected routes.

## 6. Test protected endpoint with token

Command:

```bash
curl -i http://localhost:8080/api/findings \
  -H "Authorization: Bearer $TOKEN"
```

What I explain:

Now I send the JWT token in the Authorization header.

The Gateway validates the token and checks that the user has the `SECURITY_ANALYST` role.

Because the token is valid, the request is forwarded to the Finding Service.

Expected result:

```text
RootCredentialUsage - DescribeRegions
RootCredentialUsage - GetAccountSummary
```

## 7. Generate full security report

Command:

```bash
curl -i -X POST http://localhost:8080/api/findings/1/report \
  -H "Authorization: Bearer $TOKEN"
```

What I explain:

This demonstrates the full protected microservices flow.

The request goes through the Gateway, then reaches the Finding Service. The Finding Service retrieves the finding from the database, calls the AI Service for risk analysis, then calls the Report Service to generate the final report.

Flow:

```text
User
  -> JWT token
  -> Gateway
  -> Finding Service
  -> AI Service
  -> Report Service
  -> Security Report
```

Expected result:

```text
CLOUD SECURITY REPORT
```

## 8. Final explanation

This project demonstrates:

```text
Spring Boot microservices
Spring Cloud Gateway
Eureka Service Discovery
OpenFeign communication
H2 database
Docker Compose orchestration
Keycloak external IAM
JWT authentication
Role-based authorization
AI-style security analysis
Report generation
```

The system is currently fully functional locally and prepared for future AWS integration.
