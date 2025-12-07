# API Gateway - University Management System

## Overview

The API Gateway serves as the single entry point for all client requests in the University Management System. Built with Spring Cloud Gateway, it provides centralized authentication, routing, rate limiting, and circuit breaking capabilities.

## Architecture

```
Client → API Gateway (8080) → Microservices
                              ├─ Auth Service (8081)
                              ├─ Student Service (8082)
                              ├─ Courses Service (8083)
                              ├─ Grades Service (8084)
                              └─ Billing Service (5000)
```

## Key Features

### 1. **Centralized Routing**

- Routes all requests to appropriate microservices
- Path-based routing with `/api/{service}/**` pattern
- Automatic service discovery and load balancing

### 2. **Authentication & Authorization**

- JWT token validation at gateway level
- Extracts user context from tokens
- Propagates user info to downstream services via headers:
  - `X-User-Username`: Authenticated username
  - `X-User-Role`: User role (ADMIN, TEACHER, STUDENT)

### 3. **Rate Limiting**

- Redis-based distributed rate limiting
- IP-based request throttling
- Configurable limits per service:
  - Auth/Student/Courses/Grades: 10 req/sec (burst: 20)
  - Billing: 5 req/sec (burst: 10)

### 4. **Circuit Breaking**

- Resilience4j circuit breaker implementation
- Automatic fallback responses when services are down
- Configurable failure thresholds and timeout periods

### 5. **CORS Configuration**

- Pre-configured CORS for web clients
- Supports multiple origins (localhost:3000, 4200, 8080)
- All HTTP methods allowed

### 6. **Logging & Monitoring**

- Request/response logging with duration tracking
- Actuator endpoints for health checks
- Circuit breaker and rate limiter metrics

## Technology Stack

- **Spring Boot 3.2.0**
- **Spring Cloud Gateway 2023.0.0**
- **Resilience4j** (Circuit Breaker)
- **Redis** (Rate Limiting)
- **JWT** (Authentication)
- **Docker** (Containerization)

## Project Structure

```
api-gateway/
├── src/main/java/com/universite/gateway/
│   ├── ApiGatewayApplication.java          # Main application
│   ├── config/
│   │   ├── CorsConfig.java                 # CORS configuration
│   │   ├── GatewayConfig.java              # Route configuration
│   │   ├── CircuitBreakerConfiguration.java # Circuit breaker setup
│   │   └── RateLimiterConfiguration.java   # Rate limiter setup
│   ├── filter/
│   │   ├── AuthenticationFilter.java       # JWT validation filter
│   │   └── LoggingFilter.java              # Request/response logging
│   ├── controller/
│   │   ├── GatewayController.java          # Health & info endpoints
│   │   └── FallbackController.java         # Circuit breaker fallbacks
│   ├── util/
│   │   └── JwtUtil.java                    # JWT utility methods
│   └── exception/
│       └── GlobalErrorAttributes.java      # Error handling
├── src/main/resources/
│   ├── application.yml                     # Main configuration
│   └── application-docker.yml              # Docker configuration
├── Dockerfile
└── pom.xml
```

## Routes Configuration

### Authentication Service

- **Path**: `/api/auth/**`
- **Target**: `http://localhost:8081` (or `auth_service:8081` in Docker)
- **Public Endpoints**: `/login`, `/register`, `/health`
- **Protected Endpoints**: All others require JWT

### Student Service

- **Path**: `/api/students/**`
- **Target**: `http://localhost:8082`
- **Authentication**: Required

### Courses Service

- **Path**: `/api/courses/**`
- **Target**: `http://localhost:8083`
- **Authentication**: Required

### Grades Service

- **Path**: `/api/grades/**`
- **Target**: `http://localhost:8084`
- **Authentication**: Required

### Billing Service

- **Path**: `/api/billing/**`
- **Target**: `http://localhost:5000`
- **Authentication**: Required

## Configuration

### JWT Settings

Must match the auth-service configuration:

```yaml
jwt:
  secret: 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
  expiration: 3600000 # 1 hour
```

### Circuit Breaker Settings

```yaml
resilience4j:
  circuitbreaker:
    instances:
      authService:
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 10000
        permittedNumberOfCallsInHalfOpenState: 3
```

### Rate Limiting

```yaml
redis-rate-limiter:
  replenishRate: 10 # Tokens per second
  burstCapacity: 20 # Maximum burst size
  requestedTokens: 1 # Tokens per request
```

## Running the Gateway

### Local Development

```bash
# Build the project
mvn clean package

# Run the application
java -jar target/api-gateway-1.0.0.jar

# Or use Maven
mvn spring-boot:run
```

Gateway will start on `http://localhost:8080`

### Docker

```bash
# Build Docker image
docker build -t api-gateway .

# Run with Docker Compose (from project root)
cd docker
docker-compose up -d api_gateway
```

## Testing the Gateway

### Health Check

```bash
curl http://localhost:8080/gateway/health
```

### Gateway Info

```bash
curl http://localhost:8080/gateway/info
```

### Test Authentication Flow

```bash
# 1. Register a user (via gateway)
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User",
    "role": "STUDENT"
  }'

# 2. Login to get JWT token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# 3. Access protected endpoint with token
curl http://localhost:8080/api/students \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Monitoring Endpoints

### Actuator Endpoints

- **Health**: `http://localhost:8080/actuator/health`
- **Info**: `http://localhost:8080/actuator/info`
- **Metrics**: `http://localhost:8080/actuator/metrics`
- **Gateway Routes**: `http://localhost:8080/actuator/gateway/routes`
- **Circuit Breakers**: `http://localhost:8080/actuator/circuitbreakers`

### Fallback Endpoints

When services are unavailable, the gateway returns fallback responses:

- `/fallback/auth` - Auth service unavailable
- `/fallback/student` - Student service unavailable
- `/fallback/courses` - Courses service unavailable
- `/fallback/grades` - Grades service unavailable
- `/fallback/billing` - Billing service unavailable

## Error Handling

The gateway provides standardized error responses:

```json
{
  "timestamp": "2024-01-20T10:30:00",
  "path": "/api/students",
  "service": "API Gateway",
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is missing or invalid"
}
```

## Security Features

1. **JWT Validation**: All protected routes validate JWT tokens
2. **CORS Protection**: Configured CORS policies
3. **Rate Limiting**: Prevents DDoS and abuse
4. **Circuit Breaking**: Prevents cascade failures
5. **Request Logging**: Audit trail for all requests

## Dependencies

Key dependencies in `pom.xml`:

- `spring-cloud-starter-gateway` - Gateway functionality
- `spring-cloud-starter-circuitbreaker-reactor-resilience4j` - Circuit breaking
- `spring-boot-starter-data-redis-reactive` - Rate limiting
- `jjwt` - JWT validation
- `lombok` - Code generation

## Environment Variables

### Required

- `SPRING_PROFILES_ACTIVE` - Active profile (default, docker)
- `JWT_SECRET` - Must match auth-service secret

### Optional

- `REDIS_HOST` - Redis host (default: localhost)
- `REDIS_PORT` - Redis port (default: 6379)

## Troubleshooting

### Gateway won't start

- Check if port 8080 is available
- Verify Redis is running
- Check JWT secret matches auth-service

### Routes not working

- Verify target services are running
- Check service URLs in configuration
- Review gateway logs for routing errors

### Rate limiting issues

- Ensure Redis is accessible
- Check Redis connection settings
- Verify rate limiter configuration

### Circuit breaker activated

- Check target service health
- Review failure thresholds
- Wait for circuit breaker recovery period

## Development Tips

1. **Testing Routes**: Use the `/actuator/gateway/routes` endpoint to view all configured routes
2. **Debugging**: Enable DEBUG logging for `com.universite.gateway`
3. **Local Testing**: Services can run on localhost, Docker handles networking
4. **JWT Secret**: Keep consistent across auth-service and gateway

## Performance Tuning

### Connection Pool

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        pool:
          max-connections: 100
          max-idle-time: 30s
```

### Timeouts

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        connect-timeout: 3000
        response-timeout: 5s
```

## Next Steps

1. ✅ API Gateway implemented
2. 🔄 Implement remaining microservices:
   - Courses Service (JAX-WS SOAP)
   - Grades Service (Python FastAPI)
   - Billing Service (.NET Core)
3. 📊 Add distributed tracing (Zipkin/Jaeger)
4. 📈 Implement centralized logging (ELK Stack)
5. 🔒 Add OAuth2/OpenID Connect support

## License

University Management System - Educational Project
