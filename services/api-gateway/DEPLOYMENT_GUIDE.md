# API Gateway Deployment Guide

## Prerequisites

### Software Requirements

- Java 17 or higher
- Maven 3.9.5 or higher
- Docker 24.0+ (for containerized deployment)
- Docker Compose 2.0+
- Redis 7.0+

### Service Dependencies

The API Gateway requires the following services to be running:

1. **Redis** - For rate limiting (port 6379)
2. **Auth Service** - For user authentication (port 8081)
3. **Student Service** - For student management (port 8082)
4. **Courses Service** - For course management (port 8083) [Optional]
5. **Grades Service** - For grades management (port 8084) [Optional]
6. **Billing Service** - For billing operations (port 5000) [Optional]

## Configuration

### 1. Environment Variables

Create a `.env` file in the project root:

```env
# Gateway Configuration
SPRING_PROFILES_ACTIVE=default
SERVER_PORT=8080

# JWT Configuration (Must match auth-service)
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=3600000

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379

# Service URLs (for local development)
AUTH_SERVICE_URL=http://localhost:8081
STUDENT_SERVICE_URL=http://localhost:8082
COURSES_SERVICE_URL=http://localhost:8083
GRADES_SERVICE_URL=http://localhost:8084
BILLING_SERVICE_URL=http://localhost:5000
```

### 2. Application Profiles

#### Default Profile (`application.yml`)

Used for local development with localhost service URLs.

#### Docker Profile (`application-docker.yml`)

Used for Docker deployment with container service names.

## Deployment Options

### Option 1: Local Development (Standalone)

#### Step 1: Install Dependencies

```bash
# Install Redis
docker run -d -p 6379:6379 --name redis redis:7-alpine

# Or on Windows with Chocolatey
choco install redis

# Or on macOS with Homebrew
brew install redis
```

#### Step 2: Build the Project

```bash
cd services/api-gateway
mvn clean package -DskipTests
```

#### Step 3: Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Or using the JAR file
java -jar target/api-gateway-1.0.0.jar

# Or using the run script
# Windows
run.bat

# Linux/macOS
chmod +x run.sh
./run.sh
```

#### Step 4: Verify Deployment

```bash
# Check gateway health
curl http://localhost:8080/gateway/health

# Check gateway info
curl http://localhost:8080/gateway/info

# Check actuator health
curl http://localhost:8080/actuator/health
```

### Option 2: Docker (Single Container)

#### Step 1: Build Docker Image

```bash
cd services/api-gateway
docker build -t api-gateway:1.0.0 .
```

#### Step 2: Create Docker Network

```bash
docker network create university-network
```

#### Step 3: Run Redis

```bash
docker run -d \
  --name redis \
  --network university-network \
  -p 6379:6379 \
  redis:7-alpine
```

#### Step 4: Run API Gateway

```bash
docker run -d \
  --name api-gateway \
  --network university-network \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970 \
  -e REDIS_HOST=redis \
  -e REDIS_PORT=6379 \
  api-gateway:1.0.0
```

#### Step 5: Verify Deployment

```bash
# Check container status
docker ps

# Check logs
docker logs -f api-gateway

# Test gateway
curl http://localhost:8080/gateway/health
```

### Option 3: Docker Compose (Recommended)

#### Step 1: Navigate to Docker Directory

```bash
cd docker
```

#### Step 2: Start All Services

```bash
# Start in detached mode
docker-compose up -d

# Or view logs while starting
docker-compose up
```

#### Step 3: Check Service Status

```bash
# Check all containers
docker-compose ps

# Check specific service logs
docker-compose logs -f api_gateway

# Check all logs
docker-compose logs -f
```

#### Step 4: Verify Deployment

```bash
# Health check
curl http://localhost:8080/gateway/health

# Gateway info
curl http://localhost:8080/gateway/info

# Test authentication flow
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

#### Step 5: Stop Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Stop and remove images
docker-compose down --rmi all
```

## Production Deployment

### 1. Security Hardening

#### Update JWT Secret

Generate a strong random secret:

```bash
# Generate 256-bit secret
openssl rand -base64 64
```

Update in both `application.yml` and environment variables.

#### Enable HTTPS

Add SSL certificate configuration:

```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: gateway
```

#### Restrict CORS Origins

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        corsConfigurations:
          "[/**]":
            allowedOrigins:
              - "https://university.example.com"
              - "https://app.university.example.com"
```

### 2. Performance Tuning

#### Increase Connection Pool

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        pool:
          max-connections: 500
          max-idle-time: 30s
```

#### Configure Timeouts

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        connect-timeout: 3000
        response-timeout: 10s
```

#### Tune Circuit Breaker

```yaml
resilience4j:
  circuitbreaker:
    instances:
      default:
        slidingWindowSize: 20
        minimumNumberOfCalls: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 30000
```

### 3. Redis Configuration

#### Redis Cluster (Production)

```yaml
spring:
  data:
    redis:
      cluster:
        nodes:
          - redis-1:6379
          - redis-2:6379
          - redis-3:6379
```

#### Redis Sentinel

```yaml
spring:
  data:
    redis:
      sentinel:
        master: mymaster
        nodes:
          - sentinel-1:26379
          - sentinel-2:26379
          - sentinel-3:26379
```

### 4. Logging Configuration

#### Production Logging

```yaml
logging:
  level:
    root: WARN
    com.universite.gateway: INFO
    org.springframework.cloud.gateway: INFO
  file:
    name: /var/log/gateway/application.log
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

#### Enable JSON Logging

Add dependency:

```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

### 5. Health Checks

#### Kubernetes Readiness Probe

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
```

#### Kubernetes Liveness Probe

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 60
  periodSeconds: 30
```

## Kubernetes Deployment

### 1. Create Deployment YAML

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-gateway
spec:
  replicas: 3
  selector:
    matchLabels:
      app: api-gateway
  template:
    metadata:
      labels:
        app: api-gateway
    spec:
      containers:
        - name: api-gateway
          image: api-gateway:1.0.0
          ports:
            - containerPort: 8080
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "docker"
            - name: JWT_SECRET
              valueFrom:
                secretKeyRef:
                  name: jwt-secret
                  key: secret
            - name: REDIS_HOST
              value: "redis-service"
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 10
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 30
```

### 2. Create Service YAML

```yaml
apiVersion: v1
kind: Service
metadata:
  name: api-gateway-service
spec:
  type: LoadBalancer
  ports:
    - port: 80
      targetPort: 8080
  selector:
    app: api-gateway
```

### 3. Deploy to Kubernetes

```bash
# Create secret for JWT
kubectl create secret generic jwt-secret \
  --from-literal=secret=YOUR_JWT_SECRET

# Deploy gateway
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml

# Check status
kubectl get pods
kubectl get services

# View logs
kubectl logs -f deployment/api-gateway
```

## Monitoring Setup

### 1. Prometheus Metrics

Add dependency:

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

Configure:

```yaml
management:
  metrics:
    export:
      prometheus:
        enabled: true
  endpoints:
    web:
      exposure:
        include: prometheus,health,info,metrics
```

### 2. Grafana Dashboard

Import the Spring Cloud Gateway dashboard (ID: 11506) in Grafana.

### 3. Distributed Tracing

Add Zipkin dependency:

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
```

## Troubleshooting

### Gateway Won't Start

1. Check if port 8080 is available: `netstat -ano | findstr :8080`
2. Verify Redis is running: `redis-cli ping`
3. Check JWT secret is set correctly
4. Review logs for specific errors

### Redis Connection Failed

1. Verify Redis is accessible: `redis-cli -h localhost -p 6379 ping`
2. Check firewall rules
3. Verify Redis configuration in `application.yml`

### Routes Not Working

1. Check target services are running
2. Verify service URLs in configuration
3. Check actuator routes: `curl http://localhost:8080/actuator/gateway/routes`
4. Review gateway logs for routing errors

### High Memory Usage

1. Tune JVM heap: `java -Xmx512m -Xms256m -jar gateway.jar`
2. Enable garbage collection logging: `-XX:+PrintGCDetails`
3. Check for memory leaks in custom filters

## Maintenance

### Update Configuration

```bash
# Without downtime (rolling update)
docker-compose up -d --no-deps --build api_gateway
```

### View Real-time Logs

```bash
docker-compose logs -f api_gateway
```

### Backup Configuration

```bash
# Backup application.yml
cp src/main/resources/application*.yml backup/
```

### Health Check Script

```bash
#!/bin/bash
HEALTH=$(curl -s http://localhost:8080/actuator/health | jq -r '.status')
if [ "$HEALTH" != "UP" ]; then
    echo "Gateway is down!"
    # Send alert
fi
```

## Next Steps

1. ✅ Deploy API Gateway
2. 🔄 Deploy remaining microservices
3. 📊 Set up monitoring and alerting
4. 🔒 Configure HTTPS/SSL
5. 📈 Implement distributed tracing
6. 🚀 Set up CI/CD pipeline
