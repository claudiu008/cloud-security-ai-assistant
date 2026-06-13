# Troubleshooting - Cloud Security AI Assistant

## 1. Port already in use

Error example:

```text
Port 8080 is already allocated
```

Cause:

Another container or local process already uses the same port.

Fix:

```powershell
docker compose down
```

Then start again:

```powershell
docker compose up
```

If the problem continues, check running containers:

```powershell
docker ps
```

Stop a specific container:

```powershell
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

Then call the protected endpoint again:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings" `
  -Headers @{ Authorization = "Bearer $token" }
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

```powershell
dir keycloak\realm-export.json
```

Restart clean:

```powershell
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

## 6. S3 upload fails

Possible error:

```text
Unable to load credentials
```

Cause:

AWS credentials are missing from `.env` or not passed to the container.

Fix:

Check that `.env` exists in the project root:

```powershell
dir .env
```

Expected variables:

```env
AWS_REGION=eu-central-1
S3_BUCKET_NAME=cloud-security-ai-assistant-bucket
AWS_ACCESS_KEY_ID=...
AWS_SECRET_ACCESS_KEY=...
```

Restart Docker Compose after editing `.env`:

```powershell
docker compose down
docker compose up --build
```

## 7. S3 access denied

Possible error:

```text
AccessDenied
```

Cause:

The IAM user does not have permission to upload to the bucket.

Fix:

Check IAM policy for the application user.

Required permission for upload:

```text
s3:PutObject
```

Required resource:

```text
arn:aws:s3:::cloud-security-ai-assistant-bucket/reports/*
```

The application uploads reports under:

```text
reports/
```

## 8. S3 bucket not found

Possible error:

```text
NoSuchBucket
```

Cause:

Bucket name is wrong or bucket does not exist.

Fix:

Check `.env`:

```env
S3_BUCKET_NAME=cloud-security-ai-assistant-bucket
```

Check AWS Console:

```text
S3 -> cloud-security-ai-assistant-bucket
```

## 9. S3 region mismatch

Possible error:

```text
The bucket is in this region...
```

Cause:

The application uses a different AWS region than the bucket.

Fix:

Check `.env`:

```env
AWS_REGION=eu-central-1
```

The project bucket should be in:

```text
Europe (Frankfurt) eu-central-1
```

## 10. .env appears in git status

Problem:

```text
.env appears as untracked file
```

Cause:

`.gitignore` does not ignore `.env`.

Fix:

Add this to `.gitignore`:

```gitignore
.env
.idea/
```

Then verify:

```powershell
git status
```

`.env` must not appear.

## 11. IntelliJ cannot resolve AWS SDK classes

Errors:

```text
Cannot resolve symbol software
Cannot resolve symbol S3Client
Cannot resolve symbol PutObjectRequest
Cannot resolve symbol RequestBody
```

Cause:

IntelliJ has not reloaded Maven dependencies.

Fix:

Open Maven tool window and click:

```text
Reload All Maven Projects
```

Alternative:

Right click `report-service/pom.xml` and choose:

```text
Maven -> Reload project
```

If needed:

```text
File -> Invalidate Caches -> Invalidate and Restart
```

## 12. Check containers

List running containers:

```powershell
docker ps
```

List all containers:

```powershell
docker ps -a
```

View logs for one service:

```powershell
docker logs gateway-service
docker logs finding-service
docker logs report-service
docker logs keycloak
```

## 13. Clean restart

Use this when the local environment becomes messy:

```powershell
docker compose down
docker compose up
```

If a rebuild is needed:

```powershell
docker compose up --build
```

## 14. Main demo commands

Public endpoint:

```powershell
curl.exe -i http://localhost:8080/public/status
```

Protected endpoint without token:

```powershell
curl.exe -i http://localhost:8080/api/findings
```

Get token:

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

Protected endpoint with token:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings" `
  -Headers @{ Authorization = "Bearer $token" }
```

Generate normal report:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings/1/report" `
  -Method POST `
  -Headers @{ Authorization = "Bearer $token" }
```

Generate and store report in S3:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings/1/report/store" `
  -Method POST `
  -Headers @{ Authorization = "Bearer $token" }
```

Expected S3 result:

```text
message : Report generated and stored successfully
bucket  : cloud-security-ai-assistant-bucket
s3Key   : reports/finding-1-...
```
