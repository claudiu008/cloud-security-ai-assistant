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
docker ps
```

Then start again:

```powershell
docker compose up
```

---

## 2. Token expired or invalid

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

---

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

---

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

---

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

---

## 6. Finding Service exits after startup

Symptom:

```text
finding-service exited with code 1
```

Fix:

Check the real error:

```powershell
docker logs finding-service --tail 200
```

Look for:

```text
ERROR
Exception
Caused by
APPLICATION FAILED TO START
```

Known fixed issue:

If `DataLoader.java` uses manual IDs like `1L` and `2L` while `SecurityFinding.id` has `@GeneratedValue`, Hibernate can fail.

Correct `DataLoader.java` should save findings without manual ID values:

```java
findingRepository.save(new SecurityFinding(
        "RootCredentialUsage",
        "DescribeRegions",
        "root",
        "86.120.10.55",
        "eu-central-1",
        "LOW"
));
```

---

## 7. DynamoDB endpoint returns 404

Error:

```text
HTTP/1.1 404 Not Found
```

Check direct endpoint:

```powershell
curl.exe -i http://localhost:8081/findings/dynamodb
```

Expected:

```text
HTTP/1.1 200
```

If direct endpoint returns 404, check `FindingController.java`.

The endpoint should be:

```java
@GetMapping("/findings/dynamodb")
public List<SecurityFinding> getFindingsFromDynamoDb() {
    return dynamoDbFindingService.findAll();
}
```

If direct endpoint works but Gateway endpoint fails, check:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings/dynamodb" `
  -Headers @{ Authorization = "Bearer $token" }
```

If it returns 401, get a new token.

---

## 8. DynamoDB endpoint returns AccessDenied

Possible error:

```text
AccessDeniedException
```

Cause:

The IAM user does not have permission to read from the DynamoDB table.

Required permissions:

```text
dynamodb:GetItem
dynamodb:PutItem
dynamodb:Scan
```

Required resource:

```text
arn:aws:dynamodb:eu-central-1:<ACCOUNT_ID>:table/cloud-security-findings
```

Fix:

Check IAM policy attached to the application user.

Expected policy name:

```text
CloudSecurityFindingsDynamoDBPolicy
```

---

## 9. DynamoDB table not found

Possible error:

```text
ResourceNotFoundException
```

Cause:

The table name is wrong, the table does not exist, or the region is wrong.

Fix:

Check `.env`:

```env
AWS_REGION=eu-central-1
DYNAMODB_FINDINGS_TABLE_NAME=cloud-security-findings
```

Check AWS Console:

```text
DynamoDB -> Tables -> cloud-security-findings
```

Expected status:

```text
Active
```

---

## 10. DynamoDB returns items in different order

Observation:

The endpoint returns:

```text
id 2
id 1
```

instead of:

```text
id 1
id 2
```

Cause:

DynamoDB `Scan` does not guarantee ordering.

Fix:

No fix needed for demo.

The data is correct.

---

## 11. S3 upload fails because credentials are missing

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
DYNAMODB_FINDINGS_TABLE_NAME=cloud-security-findings
AWS_ACCESS_KEY_ID=...
AWS_SECRET_ACCESS_KEY=...
```

Restart Docker Compose after editing `.env`:

```powershell
docker compose down
docker compose up --build
```

---

## 12. S3 access denied

Possible error:

```text
AccessDenied
```

Cause:

The IAM user does not have permission to upload to the bucket.

Required permission:

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

---

## 13. S3 bucket not found

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

---

## 14. .env appears in git status

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

---

## 15. IntelliJ cannot resolve AWS SDK classes

Errors:

```text
Cannot resolve symbol software
Cannot resolve symbol DynamoDbClient
Cannot resolve symbol S3Client
Cannot resolve symbol AttributeValue
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

Right click the service `pom.xml` and choose:

```text
Maven -> Reload project
```

If needed:

```text
File -> Invalidate Caches -> Invalidate and Restart
```

---

## 16. Docker rebuild downloads dependencies often

Cause:

Docker rebuild runs Maven inside the image build.

When code or `pom.xml` changes, Maven may download dependencies again.

Recommended workflow during development:

```powershell
cd finding-service
.\mvnw.cmd package -DskipTests
```

Only after `BUILD SUCCESS`, rebuild Docker:

```powershell
cd ..
docker compose up --build
```

---

## 17. Check containers

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

---

## 18. Clean restart

Use this when the local environment becomes messy:

```powershell
docker compose down
docker compose up
```

If a rebuild is needed:

```powershell
docker compose up --build
```

---

## 19. Main demo commands

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

Protected H2 endpoint:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings" `
  -Headers @{ Authorization = "Bearer $token" }
```

Protected DynamoDB endpoint:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/findings/dynamodb" `
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
