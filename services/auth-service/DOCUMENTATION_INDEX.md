# 📚 Authentication Service - Documentation Index

Welcome to the Authentication Service documentation! This index will help you find the information you need.

## 🎯 Quick Navigation

### 🚀 Getting Started (Start Here!)

1. **[QUICK_REFERENCE.md](QUICK_REFERENCE.md)** ⭐ **START HERE**

   - Essential commands and examples
   - Quick test commands
   - Common workflows
   - Perfect for: Quick reference, testing

2. **[DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)**

   - Complete deployment instructions
   - Local and Docker setup
   - Troubleshooting guide
   - Testing scenarios
   - Perfect for: First-time setup, deployment

3. **[README.md](README.md)**
   - Project overview
   - Technology stack
   - API endpoints documentation
   - Database schema
   - Perfect for: Understanding the service

### 📊 Implementation Details

4. **[IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)**
   - Complete implementation checklist
   - File structure overview
   - Features implemented
   - Technical specifications
   - Perfect for: Code review, academic evaluation

### 🧪 Testing Resources

5. **[postman_collection.json](postman_collection.json)**

   - Postman API collection
   - Automated token management
   - All endpoints covered
   - Perfect for: API testing

6. **[src/test/](src/test/)**
   - Integration tests
   - Test configuration
   - Perfect for: Automated testing

### 🔧 Configuration Files

7. **[pom.xml](pom.xml)**

   - Maven dependencies
   - Build configuration
   - Plugins setup

8. **[src/main/resources/application.yml](src/main/resources/application.yml)**

   - Main configuration
   - Database settings
   - JWT settings
   - Logging configuration

9. **[Dockerfile](Dockerfile)**

   - Docker build configuration
   - Multi-stage build
   - Production-ready

10. **[docker-compose.yml](../../docker/docker-compose.yml)**
    - Service orchestration
    - Database configuration
    - Network setup

## 📖 Documentation by Use Case

### 🎓 For Academic Evaluation

**Read in this order:**

1. [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) - See what's been built
2. [README.md](README.md) - Understand the architecture
3. [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) - Deploy and test
4. Source code in `src/main/java/` - Review implementation

### 👨‍💻 For Developers

**Read in this order:**

1. [QUICK_REFERENCE.md](QUICK_REFERENCE.md) - Get started quickly
2. [README.md](README.md) - Learn the API
3. Source code documentation - Understand implementation
4. [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) - Set up environment

### 🚀 For Deployment

**Read in this order:**

1. [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) - Follow step-by-step
2. [QUICK_REFERENCE.md](QUICK_REFERENCE.md) - Quick commands
3. [README.md](README.md) - Understand the service

### 🧪 For Testing

**Read in this order:**

1. [QUICK_REFERENCE.md](QUICK_REFERENCE.md) - Quick test commands
2. [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) - Testing scenarios
3. Import [postman_collection.json](postman_collection.json)
4. Open Swagger UI: http://localhost:8081/swagger-ui.html

## 🗂️ Source Code Structure

```
src/main/java/com/universite/auth/
├── config/                      # Configuration classes
│   ├── OpenApiConfig.java       # Swagger/OpenAPI setup
│   └── SecurityConfig.java      # Spring Security config
│
├── controller/                  # REST Controllers
│   └── AuthController.java      # All auth endpoints
│
├── dto/                         # Data Transfer Objects
│   ├── ApiResponse.java         # Generic response wrapper
│   ├── AuthResponse.java        # Login/register response
│   ├── LoginRequest.java        # Login payload
│   ├── RegisterRequest.java     # Registration payload
│   ├── TokenRefreshRequest.java # Refresh token payload
│   └── UserResponse.java        # User info response
│
├── exception/                   # Exception handling
│   ├── ErrorResponse.java       # Error response format
│   ├── GlobalExceptionHandler.java  # @ControllerAdvice
│   ├── InvalidTokenException.java
│   ├── UserAlreadyExistsException.java
│   └── UserNotFoundException.java
│
├── model/                       # JPA Entities
│   ├── RefreshToken.java        # Refresh token entity
│   ├── Role.java                # User roles enum
│   └── User.java                # User entity
│
├── repository/                  # Data Access Layer
│   ├── RefreshTokenRepository.java
│   └── UserRepository.java
│
├── security/                    # Security components
│   ├── JwtAuthenticationFilter.java  # JWT filter
│   └── JwtUtil.java             # JWT utility class
│
├── service/                     # Business Logic
│   ├── AuthService.java         # Main auth service
│   └── CustomUserDetailsService.java
│
└── AuthServiceApplication.java # Main application class
```

## 📋 API Endpoints Reference

| Endpoint                | Method | Auth | Document Section                          |
| ----------------------- | ------ | ---- | ----------------------------------------- |
| `/api/v1/auth/register` | POST   | ❌   | [README.md](README.md#1-register-user)    |
| `/api/v1/auth/login`    | POST   | ❌   | [README.md](README.md#2-login)            |
| `/api/v1/auth/refresh`  | POST   | ❌   | [README.md](README.md#3-refresh-token)    |
| `/api/v1/auth/me`       | GET    | ✅   | [README.md](README.md#4-get-current-user) |
| `/api/v1/auth/logout`   | POST   | ✅   | [README.md](README.md#5-logout)           |
| `/api/v1/auth/health`   | GET    | ❌   | [QUICK_REFERENCE.md](QUICK_REFERENCE.md)  |

## 🔗 External Links

- **Live Swagger UI**: http://localhost:8081/swagger-ui.html (when running)
- **Health Check**: http://localhost:8081/api/v1/auth/health
- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **JWT Documentation**: https://jwt.io/
- **PostgreSQL Docs**: https://www.postgresql.org/docs/

## 🎯 Common Tasks

### I want to...

**...start the service quickly**
→ [QUICK_REFERENCE.md](QUICK_REFERENCE.md#-quick-start)

**...understand the architecture**
→ [README.md](README.md#-overview)

**...test the API**
→ [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md#-testing-the-service)

**...troubleshoot an issue**
→ [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md#-troubleshooting)

**...see what's implemented**
→ [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md#-deliverables-summary)

**...review the code**
→ [Source Code Structure](#%EF%B8%8F-source-code-structure) (above)

**...deploy to production**
→ [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md#-deployment-to-production)

**...import into Postman**
→ [postman_collection.json](postman_collection.json)

**...connect to database**
→ [README.md](README.md#%EF%B8%8F-database-schema)

## 📞 Getting Help

### Quick Help

1. Check [QUICK_REFERENCE.md](QUICK_REFERENCE.md)
2. View logs: `docker compose logs -f auth_service`
3. Check health: `curl http://localhost:8081/api/v1/auth/health`

### Detailed Help

1. Read [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md#-troubleshooting)
2. Review [README.md](README.md)
3. Check source code comments

### Testing Help

1. Open Swagger UI: http://localhost:8081/swagger-ui.html
2. Import Postman collection
3. Follow [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md#-testing-the-service)

## ✅ Pre-Flight Checklist

Before starting, make sure you have:

- [ ] Read [QUICK_REFERENCE.md](QUICK_REFERENCE.md)
- [ ] Docker installed and running
- [ ] Port 8081 available
- [ ] Port 5433 available (for database)
- [ ] Postman installed (optional, for testing)

## 🎓 For Students/Instructors

### Evaluation Materials

1. **[IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)** - Complete overview
2. **[README.md](README.md)** - Technical documentation
3. **Source code** - Implementation details
4. **[postman_collection.json](postman_collection.json)** - Testing collection

### Demonstration Steps

1. Start service: `docker compose up auth_db auth_service -d`
2. Open Swagger: http://localhost:8081/swagger-ui.html
3. Test endpoints with Postman or Swagger
4. Show database schema
5. Review source code

## 📊 Documentation Statistics

- **Total Documents**: 5 main documents
- **Code Files**: 35+ Java files
- **Test Files**: 2 test files
- **Configuration Files**: 4 config files
- **Total Lines**: 2,500+ lines of code
- **API Endpoints**: 6 REST endpoints
- **Database Tables**: 2 tables

## 🔄 Document Update History

| Document      | Version | Last Updated     |
| ------------- | ------- | ---------------- |
| All Documents | 1.0.0   | December 7, 2025 |

---

## 🎯 Start Here

**New to this service?**  
Start with [QUICK_REFERENCE.md](QUICK_REFERENCE.md) for a quick overview and testing.

**Want to deploy?**  
Follow [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) step by step.

**Need API details?**  
Check [README.md](README.md) for complete documentation.

**Code review?**  
See [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) for what's built.

---

**Happy coding! 🚀**

For questions or issues, review the appropriate documentation above or check the source code comments.
