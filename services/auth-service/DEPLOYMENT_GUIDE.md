# 🚀 Authentication Service - Deployment & Testing Guide

## Prerequisites

### Required Software

1. **Java Development Kit (JDK) 17 or higher**

   - Download: https://adoptium.net/
   - Verify: `java -version`

2. **Apache Maven 3.6+**

   - Download: https://maven.apache.org/download.cgi
   - Verify: `mvn -version`

3. **PostgreSQL 14**

   - Download: https://www.postgresql.org/download/
   - Or use Docker (recommended)

4. **Docker Desktop** (Optional but recommended)

   - Download: https://www.docker.com/products/docker-desktop

5. **Postman** (For API testing)
   - Download: https://www.postman.com/downloads/

## 🐳 Quick Start with Docker (Recommended)

This is the easiest way to get started!

### Step 1: Start the Auth Service with Docker Compose

```bash
# From project root directory
cd docker
docker compose up auth_db auth_service -d
```

This will:

- Create and start PostgreSQL database on port 5433
- Build and start the auth-service on port 8081
- Configure all environment variables automatically

### Step 2: Verify Service is Running

```bash
# Check container status
docker compose ps

# View logs
docker compose logs -f auth_service

# Test health endpoint
curl http://localhost:8081/api/v1/auth/health
```

Expected response:

```json
{
  "message": "Auth service is running",
  "data": "OK",
  "timestamp": "2024-12-07T10:30:00"
}
```

### Step 3: Access Swagger UI

Open your browser and navigate to:

```
http://localhost:8081/swagger-ui.html
```

You can now test all endpoints directly from the Swagger interface!

## 💻 Local Development Setup (Without Docker)

### Step 1: Set Up PostgreSQL Database

#### Option A: Using PostgreSQL Locally

```bash
# Create database
createdb auth_db

# Or using psql
psql -U postgres
CREATE DATABASE auth_db;
\q
```

#### Option B: Using Docker for Database Only

```bash
docker run --name auth-postgres \
  -e POSTGRES_DB=auth_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5433:5432 \
  -d postgres:14-alpine
```

### Step 2: Configure Environment Variables

#### Windows (PowerShell):

```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="5433"
$env:DB_NAME="auth_db"
$env:DB_USER="postgres"
$env:DB_PASSWORD="postgres"
$env:JWT_SECRET="UniversityManagementSystemSecretKey2024VeryLongSecretForHS512Algorithm"
```

#### Linux/Mac (Bash):

```bash
export DB_HOST=localhost
export DB_PORT=5433
export DB_NAME=auth_db
export DB_USER=postgres
export DB_PASSWORD=postgres
export JWT_SECRET=UniversityManagementSystemSecretKey2024VeryLongSecretForHS512Algorithm
```

### Step 3: Build the Application

```bash
cd services/auth-service
mvn clean install
```

### Step 4: Run the Application

#### Using Maven:

```bash
mvn spring-boot:run
```

#### Using Startup Scripts:

```bash
# Windows
run.bat

# Linux/Mac
chmod +x run.sh
./run.sh
```

#### Using JAR directly:

```bash
java -jar target/auth-service-1.0.0.jar
```

### Step 5: Verify Service

```bash
curl http://localhost:8081/api/v1/auth/health
```

## 🧪 Testing the Service

### Method 1: Using Postman (Recommended)

1. **Import the Collection**

   - Open Postman
   - Click Import
   - Select `postman_collection.json` from the auth-service folder
   - Create a new environment with variable `baseUrl = http://localhost:8081`

2. **Test Registration**

   - Run "Register Student" request
   - Access token will be automatically saved to environment

3. **Test Protected Endpoints**
   - Run "Get Current User" request
   - The saved access token will be used automatically

### Method 2: Using cURL

#### 1. Register a New User

```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "email": "john.doe@university.com",
    "password": "password123",
    "role": "STUDENT"
  }'
```

Save the `accessToken` and `refreshToken` from the response.

#### 2. Login

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "password": "password123"
  }'
```

#### 3. Get Current User (Protected Endpoint)

```bash
curl -X GET http://localhost:8081/api/v1/auth/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

#### 4. Refresh Token

```bash
curl -X POST http://localhost:8081/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN_HERE"
  }'
```

#### 5. Logout

```bash
curl -X POST http://localhost:8081/api/v1/auth/logout \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

### Method 3: Using Swagger UI

1. Open browser: http://localhost:8081/swagger-ui.html
2. Try the `/api/v1/auth/register` endpoint
3. Copy the `accessToken` from the response
4. Click "Authorize" button at the top
5. Enter: `Bearer YOUR_ACCESS_TOKEN_HERE`
6. Now you can test protected endpoints

## 📊 Sample Test Scenarios

### Scenario 1: Complete User Journey

```bash
# 1. Register as student
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice.student",
    "email": "alice@university.com",
    "password": "secure123",
    "role": "STUDENT"
  }'

# Save the accessToken as TOKEN1

# 2. Get user info
curl -X GET http://localhost:8081/api/v1/auth/me \
  -H "Authorization: Bearer TOKEN1"

# 3. Logout
curl -X POST http://localhost:8081/api/v1/auth/logout \
  -H "Authorization: Bearer TOKEN1"

# 4. Login again
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice.student",
    "password": "secure123"
  }'
```

### Scenario 2: Testing Different Roles

```bash
# Register Student
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "student1",
    "email": "student1@university.com",
    "password": "password123",
    "role": "STUDENT"
  }'

# Register Teacher
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teacher1",
    "email": "teacher1@university.com",
    "password": "password123",
    "role": "TEACHER"
  }'

# Register Admin
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin1",
    "email": "admin1@university.com",
    "password": "password123",
    "role": "ADMIN"
  }'
```

### Scenario 3: Error Handling Tests

```bash
# Test duplicate username
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice.student",
    "email": "different@university.com",
    "password": "password123",
    "role": "STUDENT"
  }'
# Expected: 409 Conflict

# Test invalid email
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "invalid-email",
    "password": "password123",
    "role": "STUDENT"
  }'
# Expected: 400 Bad Request

# Test wrong password
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice.student",
    "password": "wrongpassword"
  }'
# Expected: 401 Unauthorized
```

## 🐛 Troubleshooting

### Problem: Service won't start

**Solution 1: Check Java version**

```bash
java -version
# Should be 17 or higher
```

**Solution 2: Check if port 8081 is available**

```bash
# Windows
netstat -an | findstr 8081

# Linux/Mac
lsof -i :8081
```

**Solution 3: Check database connection**

```bash
# Test database connection
psql -h localhost -p 5433 -U postgres -d auth_db
```

### Problem: "Cannot connect to database"

**Solution:**

```bash
# Check if database is running
docker compose ps auth_db

# Check database logs
docker compose logs auth_db

# Restart database
docker compose restart auth_db
```

### Problem: "JWT token validation fails"

**Solution:**

- Ensure the JWT_SECRET is the same in all services
- Check token hasn't expired (1 hour for access token)
- Verify Authorization header format: `Bearer <token>`

### Problem: Maven build fails

**Solution:**

```bash
# Clean Maven cache
mvn clean

# Update dependencies
mvn dependency:resolve

# Build with verbose output
mvn clean install -X
```

## 📈 Performance Testing

### Using Apache Bench (ab)

```bash
# Install Apache Bench
# Windows: Download from Apache website
# Linux: sudo apt-get install apache2-utils
# Mac: brew install ab

# Test health endpoint
ab -n 1000 -c 10 http://localhost:8081/api/v1/auth/health

# Test login endpoint (create test-login.json first)
ab -n 100 -c 5 -p test-login.json -T application/json \
  http://localhost:8081/api/v1/auth/login
```

### Expected Performance

- Health check: <10ms
- Registration: <200ms
- Login: <150ms
- Token validation: <50ms

## 🔄 Database Management

### View All Users

```sql
-- Connect to database
psql -h localhost -p 5433 -U postgres -d auth_db

-- List all users
SELECT id, username, email, role, created_at, last_login FROM users;

-- Count users by role
SELECT role, COUNT(*) FROM users GROUP BY role;
```

### Clear All Data

```sql
-- Delete all refresh tokens
DELETE FROM refresh_tokens;

-- Delete all users
DELETE FROM users;
```

### Backup Database

```bash
# Backup
pg_dump -h localhost -p 5433 -U postgres auth_db > auth_backup.sql

# Restore
psql -h localhost -p 5433 -U postgres auth_db < auth_backup.sql
```

## 📦 Deployment to Production

### 1. Update application-docker.yml

Set production values:

```yaml
logging:
  level:
    com.universite.auth: INFO # Change from DEBUG
```

### 2. Build Production Docker Image

```bash
cd services/auth-service
docker build -t auth-service:production .
```

### 3. Run with Production Config

```bash
docker run -d \
  --name auth-service-prod \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e JWT_SECRET=<strong-production-secret> \
  auth-service:production
```

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/) - JWT Token Debugger
- [Postman Learning Center](https://learning.postman.com/)

## ✅ Checklist

Before submitting or deploying:

- [ ] All tests pass: `mvn test`
- [ ] Service starts successfully
- [ ] All endpoints respond correctly
- [ ] Swagger documentation accessible
- [ ] Docker container builds and runs
- [ ] Database migrations work
- [ ] JWT tokens generated and validated
- [ ] Error handling works properly
- [ ] Logs are meaningful
- [ ] Security configurations reviewed

---

**Need Help?**

- Check logs: `docker compose logs -f auth_service`
- Review README.md for detailed documentation
- Test with Swagger UI: http://localhost:8081/swagger-ui.html
