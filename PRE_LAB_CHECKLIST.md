# Pre-Lab Checklist - Cloud Security AI Assistant

## 1. Before leaving for laboratory

Run this once at home, with internet connection:

```bash
cd ~/unitbv/projects/cloud-security-ai-assistant
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

## 2. Stop the system cleanly

After testing:

```bash
Ctrl + C
docker compose down
```

The Docker images remain on the laptop, so the project can start faster later.

## 3. At laboratory

Go to the project folder:

```bash
cd ~/unitbv/projects/cloud-security-ai-assistant
```

Start the project:

```bash
docker compose up
```

Use `--build` only if code changed:

```bash
docker compose up --build
```

## 4. Demo order

### Step 1 - Public endpoint

```bash
curl -i http://localhost:8080/public/status
```

Expected:

```text
HTTP/1.1 200
Cloud Security AI Assistant Gateway is running
```

Explain:

The Gateway is running and this route is public.

### Step 2 - Protected endpoint without token

```bash
curl -i http://localhost:8080/api/findings
```

Expected:

```text
HTTP/1.1 401 Unauthorized
```

Explain:

The API is protected by Spring Security. Without JWT token, access is denied.

### Step 3 - Get JWT token

```bash
TOKEN=$(curl -s -X POST "http://localhost:8084/realms/cloud-security/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=cloud-security-gateway" \
  -d "username=analyst" \
  -d "password=analyst123" | python3 -c "import sys, json; print(json.load(sys.stdin)['access_token'])")
```

Explain:

The `analyst` user authenticates through Keycloak and receives a JWT access token.

### Step 4 - Protected endpoint with token

```bash
curl -i http://localhost:8080/api/findings \
  -H "Authorization: Bearer $TOKEN"
```

Expected:

```text
RootCredentialUsage - DescribeRegions
RootCredentialUsage - GetAccountSummary
```

Explain:

The Gateway validates the JWT and checks the `SECURITY_ANALYST` role.

### Step 5 - Generate report

```bash
curl -i -X POST http://localhost:8080/api/findings/1/report \
  -H "Authorization: Bearer $TOKEN"
```

Expected:

```text
CLOUD SECURITY REPORT
```

Explain:

This shows the full protected flow: Gateway, Finding Service, AI Service, Report Service.

## 5. If something fails

Check containers:

```bash
docker ps
```

Clean restart:

```bash
docker compose down
docker compose up
```

If the token returns `401`, generate a new token.

If Eureka does not show all services immediately, wait 30-60 seconds and refresh the browser.

## 6. Files to open during presentation

Useful files:

```text
README.md
LAB_DEMO_GUIDE.md
PRESENTATION_SCRIPT.md
TROUBLESHOOTING.md
docker-compose.yml
gateway-service/src/main/java/com/cld/gateway/config/SecurityConfig.java
keycloak/realm-export.json
```

## 7. Main sentence for presentation

Cloud Security AI Assistant is a secured Spring Boot microservices system for analyzing cloud security findings. It uses Eureka for service discovery, Spring Cloud Gateway for routing and security, Keycloak for external IAM, JWT for authentication, role-based authorization, Docker Compose for orchestration, and separate services for findings, AI-style analysis, and report generation.
