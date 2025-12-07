# API Gateway Testing Guide

## Testing Strategy

### 1. Unit Tests

Test individual components in isolation.

### 2. Integration Tests

Test gateway routes and filters with real services.

### 3. Load Tests

Test rate limiting and circuit breaker behavior.

## Running Tests

### All Tests

```bash
mvn test
```

### Specific Test Class

```bash
mvn test -Dtest=GatewayRoutesTest
```

### With Coverage

```bash
mvn clean test jacoco:report
```

## Manual Testing

### Prerequisites

1. Start Redis: `docker run -d -p 6379:6379 redis:7-alpine`
2. Start Auth Service on port 8081
3. Start Student Service on port 8082
4. Start API Gateway on port 8080

### Test 1: Health Check

```bash
curl http://localhost:8080/gateway/health
```

Expected Response:

```json
{
  "status": "UP",
  "service": "API Gateway",
  "timestamp": "2024-01-20T10:30:00",
  "message": "Gateway is running"
}
```

### Test 2: Gateway Info

```bash
curl http://localhost:8080/gateway/info
```

Expected Response:

```json
{
  "name": "University Management System - API Gateway",
  "version": "1.0.0",
  "description": "Centralized API Gateway for all microservices",
  "port": 8080,
  "routes": {
    "/api/auth/**": "Authentication Service (8081)",
    "/api/students/**": "Student Service (8082)",
    "/api/courses/**": "Courses Service (8083)",
    "/api/grades/**": "Grades Service (8084)",
    "/api/billing/**": "Billing Service (5000)"
  },
  "timestamp": "2024-01-20T10:30:00"
}
```

### Test 3: Public Endpoint (No Auth Required)

```bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User",
    "role": "STUDENT"
  }'
```

Expected Response:

```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "fullName": "Test User",
    "role": "STUDENT"
  }
}
```

### Test 4: Login and Get JWT Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

Expected Response:

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

**Save the `accessToken` for next tests!**

### Test 5: Protected Endpoint (Auth Required)

```bash
# Get current user info
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

Expected Response:

```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "fullName": "Test User",
    "role": "STUDENT",
    "createdAt": "2024-01-20T10:30:00"
  }
}
```

### Test 6: Unauthorized Access

```bash
# Try to access protected endpoint without token
curl http://localhost:8080/api/auth/me
```

Expected Response:

```json
{
  "timestamp": "2024-01-20T10:30:00",
  "path": "/api/auth/me",
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is missing or invalid"
}
```

### Test 7: Invalid Token

```bash
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer INVALID_TOKEN"
```

Expected Response:

```json
{
  "timestamp": "2024-01-20T10:30:00",
  "path": "/api/auth/me",
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is invalid"
}
```

### Test 8: Rate Limiting

```bash
# Send 25 rapid requests (exceeds burst capacity of 20)
for i in {1..25}; do
  echo "Request $i:"
  curl -s -w "\nHTTP Status: %{http_code}\n" http://localhost:8080/api/auth/health
  echo "---"
done
```

Expected: First 20 requests succeed (200), next 5 are rate limited (429)

### Test 9: Circuit Breaker (Service Down)

```bash
# 1. Stop the auth service
# 2. Make a request that should fail
curl http://localhost:8080/api/auth/health
```

Expected Response (after 5 failures):

```json
{
  "service": "Auth Service",
  "message": "The authentication service is currently unavailable. Please try again later.",
  "timestamp": "2024-01-20T10:30:00",
  "status": "SERVICE_UNAVAILABLE"
}
```

### Test 10: CORS Preflight

```bash
curl -X OPTIONS http://localhost:8080/api/auth/login \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -v
```

Expected Headers:

```
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
Access-Control-Allow-Headers: *
Access-Control-Allow-Credentials: true
```

### Test 11: User Context Propagation

With gateway logging enabled, verify that downstream services receive user headers:

```bash
# Make authenticated request
curl http://localhost:8080/api/students \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -v
```

Check downstream service logs for headers:

- `X-User-Username: testuser`
- `X-User-Role: STUDENT`

## Actuator Endpoints Testing

### View All Routes

```bash
curl http://localhost:8080/actuator/gateway/routes | jq
```

### Gateway Metrics

```bash
curl http://localhost:8080/actuator/metrics/gateway.requests | jq
```

### Circuit Breaker Status

```bash
curl http://localhost:8080/actuator/circuitbreakers | jq
```

### Health Check

```bash
curl http://localhost:8080/actuator/health | jq
```

## Load Testing with Apache Bench

### Test Rate Limiting

```bash
# Send 100 requests with 10 concurrent connections
ab -n 100 -c 10 http://localhost:8080/api/auth/health
```

### Test Authentication Flow

```bash
# Create a file with login payload
echo '{"username":"testuser","password":"password123"}' > login.json

# Load test login endpoint
ab -n 50 -c 5 -p login.json -T application/json http://localhost:8080/api/auth/login
```

## Using Postman

1. **Import Collection**: Create a Postman collection with all endpoints
2. **Environment Variables**:
   - `gateway_url`: `http://localhost:8080`
   - `access_token`: (set after login)
3. **Pre-request Script** for protected endpoints:
   ```javascript
   pm.request.headers.add({
     key: "Authorization",
     value: "Bearer " + pm.environment.get("access_token"),
   });
   ```

## Docker Testing

### Test with Docker Compose

```bash
# Start all services
cd docker
docker-compose up -d

# Wait for services to be healthy
docker-compose ps

# Test gateway
curl http://localhost:8080/gateway/health

# View logs
docker-compose logs -f api_gateway

# Stop services
docker-compose down
```

## Performance Testing Checklist

- [ ] Response time < 100ms for routing
- [ ] Rate limiting works correctly
- [ ] Circuit breaker opens after 5 failures
- [ ] Circuit breaker closes after successful requests in half-open state
- [ ] CORS headers present on all responses
- [ ] JWT validation works correctly
- [ ] User context propagated to downstream services
- [ ] Fallback responses returned when services down
- [ ] Gateway handles 100 concurrent requests
- [ ] No memory leaks under load

## Common Issues

### Redis Connection Failed

```
Error: Unable to connect to Redis at localhost:6379
```

**Solution**: Start Redis with `docker run -d -p 6379:6379 redis:7-alpine`

### JWT Secret Mismatch

```
Error: JWT signature does not match
```

**Solution**: Ensure `jwt.secret` is identical in auth-service and api-gateway

### Port Already in Use

```
Error: Port 8080 is already in use
```

**Solution**: Change port in `application.yml` or stop the conflicting process

### Service Not Found

```
Error: Connection refused to localhost:8081
```

**Solution**: Ensure target service is running before testing gateway

## Test Data

### Test Users

Create these users for comprehensive testing:

```json
// Admin User
{
  "username": "admin",
  "email": "admin@university.com",
  "password": "admin123",
  "fullName": "Admin User",
  "role": "ADMIN"
}

// Teacher User
{
  "username": "teacher1",
  "email": "teacher@university.com",
  "password": "teacher123",
  "fullName": "John Teacher",
  "role": "TEACHER"
}

// Student User
{
  "username": "student1",
  "email": "student@university.com",
  "password": "student123",
  "fullName": "Jane Student",
  "role": "STUDENT"
}
```

## Success Criteria

✅ All public endpoints accessible without authentication  
✅ Protected endpoints require valid JWT token  
✅ Invalid tokens are rejected with 401  
✅ Rate limiting activates after threshold  
✅ Circuit breaker opens when service fails  
✅ Fallback responses returned when circuit open  
✅ CORS headers present on all responses  
✅ User context headers propagated to services  
✅ All routes correctly map to target services  
✅ Gateway starts successfully with all dependencies

## Next Steps

After successful testing:

1. Create automated integration test suite
2. Set up CI/CD pipeline
3. Implement distributed tracing
4. Add API documentation (Swagger/OpenAPI)
5. Configure production-ready security
