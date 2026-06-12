# Troubleshooting - Cloud Security AI Assistant

## 1. Port already in use

Error example:

```text
Port 8080 is already allocated
```

Cause:

Another container or local process already uses the same port.

Fix:

```bash
docker compose down
```

Then start again:

```bash
docker compose up
```

If the problem continues, check running containers:

```bash
docker ps
```

Stop a specific container:

```bash
docker stop <container_name>
```

## 2. Token expired

Error:

```text
HTTP/1.1 401 Unauthorized
```

Cause:

The JWT access token expired or was not sent.

Fix: get a new token.

```bash
TOKEN=$(curl -s -X POST "http://localhost:8084/realms/cloud-security/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=cloud-security-gateway" \
  -d "username=analyst" \
  -d "password=analyst123" | python3 -c "import sys, json; print(json.load(sys.stdin)['access_token'])")
```

Then call the protected endpoint again:

```bash
curl -i http://localhost:8080/api/findings \
  -H "Authorization: Bearer $TOKEN"
```

## 3. Keycloak realm does not exist

Error:

```text
Realm does not exist
```

Cause:

Keycloak did not import the realm correctly.

Fix:

Check that this file exists:

```bash
ls keycloak/realm-export.json
```

Restart clean:

```bash
docker compose down
docker compose up
```

The `cloud-security` realm should be imported automatically.

## 4. Maven wrapper permission denied

Error:

```text
./mvnw: Permission denied
```

Cause:

On Linux, the Maven wrapper files need executable permission.

Fix:

```bash
chmod +x discovery-service/mvnw
chmod +x gateway-service/mvnw
chmod +x finding-service/mvnw
chmod +x ai-service/mvnw
chmod +x report-service/mvnw
```

Then rebuild:

```bash
docker compose up --build
```

## 5. Services do not appear immediately in Eureka

Observation:

Eureka dashboard does not show all services immediately.

Cause:

Some services need a few seconds to register.

Fix:

Wait 30-60 seconds and refresh:

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

## 6. Check containers

List running containers:

```bash
docker ps
```

List all containers:

```bash
docker ps -a
```

View logs for one service:

```bash
docker logs gateway-service
docker logs finding-service
docker logs keycloak
```

## 7. Clean restart

Use this when the local environment becomes messy:

```bash
docker compose down
docker compose up
```

If a rebuild is needed:

```bash
docker compose up --build
```

## 8. Main demo commands

Public endpoint:

```bash
curl -i http://localhost:8080/public/status
```

Protected endpoint without token:

```bash
curl -i http://localhost:8080/api/findings
```

Get token:

```bash
TOKEN=$(curl -s -X POST "http://localhost:8084/realms/cloud-security/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=cloud-security-gateway" \
  -d "username=analyst" \
  -d "password=analyst123" | python3 -c "import sys, json; print(json.load(sys.stdin)['access_token'])")
```

Protected endpoint with token:

```bash
curl -i http://localhost:8080/api/findings \
  -H "Authorization: Bearer $TOKEN"
```

Generate report:

```bash
curl -i -X POST http://localhost:8080/api/findings/1/report \
  -H "Authorization: Bearer $TOKEN"
```
