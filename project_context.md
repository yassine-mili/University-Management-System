# 🎓 University Management System - SOA Project Context

## 📋 Project Overview

This is an academic Service-Oriented Architecture (SOA) project demonstrating microservices design patterns with multiple technology stacks. The system manages university operations including student records, courses, grades, billing, and authentication.

**Academic Context:**
- Course: Architecture SOA et Services Web
- Institution: 3ème année Licence Génie Logiciel et Système d'information
- Instructors: Ghada Feki, Amel Mdimagh, Dorra Kechrid
- Evaluation Period: Week of December 15, 2024
- Team Size: 1-3 students

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client Layer                            │
│              (Web/Mobile Applications, API Clients)             │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    API Gateway (Port 8080)                      │
│                    [Spring Cloud Gateway]                       │
│  • Centralized Routing                                          │
│  • JWT Authentication Validation                                │
│  • Load Balancing                                               │
│  • Request/Response Logging                                     │
└──┬────────┬────────┬────────┬────────┬────────────────────────┘
   │        │        │        │        │
   ▼        ▼        ▼        ▼        ▼
┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────────┐
│Auth │ │Stud │ │Cours│ │Grade│ │Billing  │
│8081 │ │8082 │ │8083 │ │8084 │ │5000     │
└─────┘ └─────┘ └─────┘ └─────┘ └─────────┘
   │        │        │        │        │
   ▼        ▼        ▼        ▼        ▼
┌──────────────────────────────────────────┐
│         Database Layer                   │
│  PostgreSQL (Students, Auth, Courses)    │
│  SQL Server (Billing)                    │
└──────────────────────────────────────────┘
```

---

## 🎯 Five Microservices

### 1. ✅ **Student Service** (COMPLETE)
- **Type:** REST API
- **Technology:** Node.js + Express.js
- **Database:** PostgreSQL (Prisma ORM)
- **Port:** 8082
- **Status:** ✅ 100% Complete

**Features:**
- Full CRUD operations for student records
- Pagination and filtering
- Unique constraints (email, numero_etudiant)
- Swagger/OpenAPI documentation at `/api-docs`
- Docker containerized

**Endpoints:**
```
POST   /api/v1/students         - Create student
GET    /api/v1/students         - List all (paginated)
GET    /api/v1/students/:id     - Get single student
PUT    /api/v1/students/:id     - Update student
DELETE /api/v1/students/:id     - Delete student
```

**Database Schema:**
```sql
students (
  id SERIAL PRIMARY KEY,
  numero_etudiant TEXT UNIQUE NOT NULL,
  email TEXT UNIQUE NOT NULL,
  nom TEXT NOT NULL,
  prenom TEXT NOT NULL,
  niveau TEXT NOT NULL,
  date_naissance DATE,
  filiere TEXT,
  date_inscription TIMESTAMP DEFAULT NOW(),
  est_actif BOOLEAN DEFAULT true,
  createdAt TIMESTAMP DEFAULT NOW(),
  updatedAt TIMESTAMP NOT NULL
)
```

---

### 2. ✅ **Billing Service** (COMPLETE)
- **Type:** SOAP Web Service + REST API
- **Technology:** .NET Core 7.0 + SoapCore
- **Database:** SQL Server
- **Port:** 5000
- **Status:** ✅ 100% Complete

**Features:**
- Dual protocol support (SOAP + REST)
- Invoice management
- Student billing records
- WSDL at `/InvoiceService.svc?wsdl`
- Docker containerized

**SOAP Operations:**
- `CreateInvoice` - Generate new invoice
- `GetInvoiceByNumber` - Retrieve invoice

**REST Endpoints:**
```
POST /api/invoices              - Create invoice
GET  /api/invoices/{number}     - Get invoice
```

**Database Schema:**
```sql
Invoices (
  Id INT PRIMARY KEY,
  InvoiceNumber NVARCHAR UNIQUE,
  Amount DECIMAL,
  Currency NVARCHAR DEFAULT 'TND',
  CreatedAt DATETIME,
  StudentId INT FOREIGN KEY
)

Students (
  Id INT PRIMARY KEY,
  UniversityId NVARCHAR,
  FirstName NVARCHAR,
  LastName NVARCHAR
)
```

---

### 3. ⏳ **Authentication Service** (TO DO)
- **Type:** REST API
- **Technology:** Spring Boot + Spring Security
- **Database:** PostgreSQL
- **Port:** 8081
- **Status:** ⏳ Not Started

**Required Features:**
- User registration and login
- JWT token generation and validation
- Token refresh mechanism
- Role-based access (ADMIN, TEACHER, STUDENT)
- Password hashing (BCrypt)
- Session management

**Required Endpoints:**
```
POST /api/v1/auth/register      - User registration
POST /api/v1/auth/login         - Login (returns JWT)
POST /api/v1/auth/refresh       - Refresh token
GET  /api/v1/auth/me            - Get current user
POST /api/v1/auth/logout        - Logout
```

**Database Schema Needed:**
```sql
users (
  id SERIAL PRIMARY KEY,
  username VARCHAR UNIQUE NOT NULL,
  email VARCHAR UNIQUE NOT NULL,
  password_hash VARCHAR NOT NULL,
  role VARCHAR NOT NULL,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP,
  last_login TIMESTAMP
)
```

**JWT Token Structure:**
```json
{
  "user_id": 1,
  "username": "john.doe",
  "email": "john@example.com",
  "role": "STUDENT",
  "exp": 1234567890
}
```

---

### 4. ⏳ **Courses Service** (TO DO)
- **Type:** SOAP Web Service
- **Technology:** Java + JAX-WS
- **Database:** PostgreSQL
- **Port:** 8083
- **Status:** ⏳ Not Started

**Required Features:**
- Course management (CRUD)
- Schedule/timetable management
- Student enrollment
- Teacher assignment
- WSDL generation

**Required SOAP Operations:**
```
createCourse        - Add new course
getCourse           - Get course details
updateCourse        - Modify course
deleteCourse        - Remove course
listCourses         - Get all courses
addSchedule         - Add course schedule
getScheduleByCourse - Get course timetable
enrollStudent       - Enroll student in course
getStudentCourses   - Get student's courses
```

**Database Schema Needed:**
```sql
courses (
  id SERIAL PRIMARY KEY,
  code VARCHAR UNIQUE NOT NULL,
  name VARCHAR NOT NULL,
  description TEXT,
  credits INT,
  semester VARCHAR,
  created_at TIMESTAMP
)

schedules (
  id SERIAL PRIMARY KEY,
  course_id INT REFERENCES courses(id),
  day VARCHAR,
  start_time TIME,
  end_time TIME,
  room VARCHAR
)

enrollments (
  id SERIAL PRIMARY KEY,
  student_id INT,
  course_id INT REFERENCES courses(id),
  enrollment_date TIMESTAMP,
  status VARCHAR
)
```

---

### 5. ⏳ **Grades Service** (TO DO)
- **Type:** REST API
- **Technology:** Python + FastAPI
- **Database:** PostgreSQL
- **Port:** 8084
- **Status:** ⏳ Not Started

**Required Features:**
- Grade entry and management
- GPA calculation
- Academic transcript generation
- Grade statistics
- Automatic Swagger docs at `/docs`

**Required Endpoints:**
```
POST /api/v1/grades                     - Create grade
GET  /api/v1/grades                     - List grades
GET  /api/v1/grades/{id}                - Get single grade
PUT  /api/v1/grades/{id}                - Update grade
DELETE /api/v1/grades/{id}              - Delete grade
GET  /api/v1/grades/student/{id}        - Get student grades
GET  /api/v1/grades/student/{id}/gpa    - Calculate GPA
GET  /api/v1/grades/student/{id}/transcript - Generate transcript
```

**Database Schema Needed:**
```sql
grades (
  id SERIAL PRIMARY KEY,
  student_id INT NOT NULL,
  course_id INT NOT NULL,
  grade_value DECIMAL CHECK (grade_value BETWEEN 0 AND 20),
  grade_type VARCHAR, -- EXAM, HOMEWORK, PROJECT, MIDTERM, FINAL
  date DATE,
  created_at TIMESTAMP
)
```

**GPA Calculation Formula:**
```
GPA = SUM(grade_value * course_credits) / SUM(course_credits)
```

---

### 6. ⏳ **API Gateway** (TO DO)
- **Type:** Gateway Service
- **Technology:** Spring Cloud Gateway
- **Port:** 8080
- **Status:** ⏳ Not Started

**Required Features:**
- Centralized routing to all services
- JWT token validation
- Request/response logging
- CORS configuration
- Load balancing
- Rate limiting

**Routing Configuration:**
```yaml
/api/auth/**     → Auth Service (8081)
/api/students/** → Student Service (8082)
/api/courses/**  → Courses Service (8083)
/api/grades/**   → Grades Service (8084)
/api/billing/**  → Billing Service (5000)
```

**Security Flow:**
1. Client sends request with JWT in header
2. Gateway validates JWT with Auth Service
3. If valid, forward request to target service
4. If invalid, return 401 Unauthorized

---

## 🛠️ Technology Stack

### **Languages:**
- Java (Spring Boot, JAX-WS)
- JavaScript/Node.js (Express.js)
- Python (FastAPI)
- C# (.NET Core)

### **Frameworks:**
- **Spring Boot** - Auth Service, API Gateway
- **Spring Cloud Gateway** - API Gateway routing
- **Express.js** - Student Service
- **FastAPI** - Grades Service
- **JAX-WS** - SOAP Web Services (Courses)
- **SoapCore** - SOAP in .NET (Billing)

### **Databases:**
- **PostgreSQL** - Student, Auth, Courses, Grades
- **SQL Server** - Billing

### **ORMs:**
- **Prisma** (Node.js) - Student Service
- **JPA/Hibernate** (Java) - Auth, Courses
- **SQLAlchemy** (Python) - Grades
- **Entity Framework Core** (.NET) - Billing

### **API Documentation:**
- **Swagger/OpenAPI 3.0** - REST APIs
- **WSDL** - SOAP Services

### **Security:**
- **JWT (JSON Web Tokens)** - Authentication
- **Spring Security** - Auth Service
- **BCrypt** - Password hashing

### **DevOps:**
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration

---

## 📁 Project Structure

```
University-Management-System/
├── README.md                          # Project overview
├── PROJECT_CONTEXT.md                 # This file
├── QUICK_START.md                     # Fast startup guide
├── SETUP_GUIDE.md                     # Complete setup
├── DOCUMENTATION_INDEX.md             # Docs navigation
├── .gitignore                         # Git ignore rules
│
├── docker/
│   └── docker-compose.yml             # All services orchestration
│
├── services/
│   ├── auth-service/                  # ⏳ TO DO
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   └── resources/
│   │   │   └── test/
│   │   ├── pom.xml                    # Maven config
│   │   ├── Dockerfile
│   │   └── README.md
│   │
│   ├── student-service/               # ✅ COMPLETE
│   │   ├── src/
│   │   │   ├── config/                # Prisma, Swagger
│   │   │   ├── controllers/           # Business logic
│   │   │   ├── routes/                # API routes
│   │   │   └── server.js              # Express app
│   │   ├── prisma/
│   │   │   ├── schema.prisma          # DB schema
│   │   │   └── migrations/
│   │   ├── package.json
│   │   ├── Dockerfile
│   │   ├── .env
│   │   └── README.md
│   │
│   ├── courses-service/               # ⏳ TO DO
│   │   ├── src/
│   │   │   └── main/
│   │   │       ├── java/
│   │   │       └── resources/
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── README.md
│   │
│   ├── grades-service/                # ⏳ TO DO
│   │   ├── app/
│   │   │   ├── main.py                # FastAPI app
│   │   │   ├── models.py              # SQLAlchemy models
│   │   │   ├── schemas.py             # Pydantic schemas
│   │   │   ├── crud.py                # Database operations
│   │   │   └── routers/
│   │   ├── requirements.txt
│   │   ├── Dockerfile
│   │   └── README.md
│   │
│   ├── billing-service/               # ✅ COMPLETE
│   │   ├── Data/
│   │   │   └── BillingDbContext.cs
│   │   ├── Models/
│   │   ├── Services/
│   │   ├── Program.cs
│   │   ├── BillingService.csproj
│   │   ├── Dockerfile
│   │   ├── docker-compose.yml
│   │   └── README.md
│   │
│   └── api-gateway/                   # ⏳ TO DO
│       ├── src/
│       │   └── main/
│       │       ├── java/
│       │       └── resources/
│       │           └── application.yml
│       ├── pom.xml
│       ├── Dockerfile
│       └── README.md
│
└── documentation/
    ├── architecture-diagram.png
    ├── api-reference.pdf
    └── presentation.pptx
```

---

## 🔐 Security Architecture

### **Authentication Flow:**
```
1. User registers → Auth Service stores hashed password
2. User logs in → Auth Service validates credentials
3. Auth Service generates JWT token (1h expiry)
4. Client includes JWT in Authorization header
5. API Gateway validates JWT on every request
6. Gateway forwards authenticated request to service
7. Service processes request with user context
```

### **JWT Token Example:**
```
Header:
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Token Payload:
{
  "sub": "user@example.com",
  "userId": 123,
  "role": "STUDENT",
  "iat": 1702000000,
  "exp": 1702003600
}
```

### **Security Requirements:**
- All passwords must be hashed with BCrypt
- JWT tokens expire after 1 hour
- Refresh tokens valid for 7 days
- HTTPS in production
- CORS properly configured
- Rate limiting on Auth Service (5 requests/minute for login)

---

## 🔄 Inter-Service Communication

### **Service Dependencies:**
```
API Gateway
  ├─→ Auth Service (JWT validation)
  ├─→ Student Service
  ├─→ Courses Service
  ├─→ Grades Service
  └─→ Billing Service

Grades Service
  ├─→ Student Service (student info)
  └─→ Courses Service (course info)

Courses Service
  └─→ Student Service (enrollment validation)

Billing Service
  └─→ Student Service (student billing info)
```

### **Communication Protocols:**
- **REST to REST:** HTTP/JSON (Student ↔ Grades)
- **REST to SOAP:** SOAP client (Grades → Courses)
- **SOAP to REST:** REST client (Courses → Student)
- **Gateway to All:** HTTP routing with JWT propagation

---

## 🗄️ Database Architecture

### **PostgreSQL Databases:**
```
students_db (Port 5432)
  └─ students table (Student Service)

auth_db (Port 5433)
  └─ users table (Auth Service)

courses_db (Port 5434)
  ├─ courses table
  ├─ schedules table
  └─ enrollments table (Courses Service)

grades_db (Port 5435)
  └─ grades table (Grades Service)
```

### **SQL Server Database:**
```
BillingDb (Port 1433)
  ├─ Invoices table
  └─ Students table (Billing Service)
```

---

## 🐳 Docker Configuration

### **Current docker-compose.yml Structure:**
```yaml
services:
  # Databases
  student_db: (PostgreSQL - Port 5432) ✅
  billing_sql: (SQL Server - Port 1433) ✅
  
  # Services
  student_service: (Port 8082) ✅
  billing_service: (Port 5000) ✅
  
  # TO ADD:
  auth_db: (PostgreSQL - Port 5433)
  courses_db: (PostgreSQL - Port 5434)
  grades_db: (PostgreSQL - Port 5435)
  auth_service: (Port 8081)
  courses_service: (Port 8083)
  grades_service: (Port 8084)
  api_gateway: (Port 8080)
```

### **Environment Variables Pattern:**
```bash
# Service Configuration
PORT=8082
NODE_ENV=development

# Database Connection
DATABASE_URL=postgresql://user:password@host:5432/dbname

# Authentication (for services using JWT)
JWT_SECRET=your-secret-key-here
JWT_EXPIRY=3600

# Service URLs (for inter-service communication)
AUTH_SERVICE_URL=http://auth-service:8081
STUDENT_SERVICE_URL=http://student-service:8082
```

---

## 📊 Evaluation Criteria

### **Technical Competencies (20 points total):**

1. **SOA Architecture (3 points)**
   - Proper service decomposition
   - Clear separation of concerns
   - Microservices independence

2. **RESTful & SOAP Development (5 points)**
   - REST APIs properly implemented
   - SOAP services with valid WSDL
   - Correct HTTP methods and status codes

3. **Security Implementation (Bonus)**
   - JWT authentication
   - Password hashing
   - Authorization rules

4. **Interoperability (2 points)**
   - Services communicate successfully
   - REST-SOAP integration
   - Data consistency

5. **Deployment & Containerization (2 points)**
   - Docker configuration
   - Services start automatically
   - Environment management

### **Methodological Competencies (8 points total):**

1. **Teamwork (2 points)**
   - Clear role distribution
   - Git collaboration
   - Code reviews

2. **Agile Management (Bonus)**
   - Sprints/iterations
   - Task board (Trello/Jira)
   - Daily standups

3. **Documentation (3 points)**
   - Technical specifications
   - API documentation
   - User manual

4. **Presentation (3 points)**
   - Clear explanation
   - Live demo
   - Q&A handling

---

## 🎯 Project Requirements Checklist

### **Mandatory Features:**
- [ ] 5 microservices operational
- [ ] REST APIs (3 services)
- [ ] SOAP Web Services (2 services)
- [ ] PostgreSQL persistence
- [ ] SQL Server persistence (Billing)
- [ ] API Gateway routing
- [ ] JWT authentication
- [ ] Docker containerization
- [ ] Docker Compose orchestration
- [ ] API documentation (Swagger/WSDL)

### **Service-Specific Requirements:**

**Auth Service:**
- [ ] User registration
- [ ] User login with JWT
- [ ] Token validation endpoint
- [ ] Password hashing

**Student Service:** ✅
- [x] CRUD operations
- [x] Pagination
- [x] Data validation
- [x] Swagger documentation

**Courses Service:**
- [ ] SOAP operations
- [ ] WSDL accessible
- [ ] Schedule management
- [ ] Student enrollment

**Grades Service:**
- [ ] Grade management
- [ ] GPA calculation
- [ ] Transcript generation
- [ ] FastAPI automatic docs

**Billing Service:** ✅
- [x] SOAP interface
- [x] REST interface (bonus)
- [x] Invoice management
- [x] WSDL accessible

**API Gateway:**
- [ ] Routes to all services
- [ ] JWT validation
- [ ] CORS configuration
- [ ] Error handling

---

## 🚀 Getting Started (For New Developers)

### **1. Environment Setup:**
```bash
# Required Software
- Docker Desktop
- Node.js 18+
- Java JDK 17+
- Python 3.10+
- .NET Core SDK 7.0+
- Git
- Postman (for API testing)
```

### **2. Clone and Run Existing Services:**
```bash
# Clone repository
git clone [repository-url]
cd University-Management-System

# Start existing services
cd docker
docker compose up -d

# Verify services
curl http://localhost:8082/              # Student Service
curl http://localhost:5000/              # Billing Service
```

### **3. Access Documentation:**
```
Student Service Swagger: http://localhost:8082/api-docs
Billing WSDL:            http://localhost:5000/InvoiceService.svc?wsdl
```

### **4. Development Workflow:**
```bash
# Create feature branch
git checkout -b feature/auth-service

# Make changes
# ...

# Test locally
docker compose build [service-name]
docker compose up [service-name]

# Commit and push
git add .
git commit -m "feat: implement auth service"
git push origin feature/auth-service
```

---

## 📝 Coding Standards

### **General Principles:**
- Follow RESTful conventions
- Use meaningful variable/function names
- Comment complex logic
- Handle errors gracefully
- Log important operations
- Write clean, readable code

### **REST API Standards:**
```
✅ Good:
GET    /api/v1/students
POST   /api/v1/students
GET    /api/v1/students/{id}
PUT    /api/v1/students/{id}
DELETE /api/v1/students/{id}

❌ Bad:
GET    /getStudents
POST   /createStudent
GET    /student?id=1
```

### **Response Format:**
```json
// Success Response
{
  "message": "Operation successful",
  "data": { /* result */ },
  "timestamp": "2024-12-07T10:30:00Z"
}

// Error Response
{
  "message": "Error description",
  "error": "ErrorType",
  "statusCode": 400,
  "timestamp": "2024-12-07T10:30:00Z"
}
```

### **Git Commit Messages:**
```
feat: add user registration endpoint
fix: resolve JWT validation issue
docs: update API documentation
refactor: improve error handling
test: add unit tests for auth service
```

---

## 🔧 Common Development Tasks

### **Add New Endpoint to Existing Service:**
1. Define route in routes file
2. Implement controller logic
3. Update Swagger/WSDL documentation
4. Add validation
5. Test with Postman
6. Update service README

### **Create New Microservice:**
1. Create service directory structure
2. Set up database connection
3. Implement core functionality
4. Add API documentation
5. Create Dockerfile
6. Update docker-compose.yml
7. Test independently
8. Test through API Gateway

### **Integrate Services:**
1. Identify communication pattern (REST/SOAP)
2. Implement client in consuming service
3. Add error handling
4. Test integration
5. Document integration flow

---

## 🐛 Troubleshooting Guide

### **Service Won't Start:**
```bash
# Check logs
docker compose logs [service-name]

# Rebuild container
docker compose build --no-cache [service-name]
docker compose up [service-name]

# Check port conflicts
netstat -an | grep [port-number]
```

### **Database Connection Issues:**
```bash
# Check database is running
docker compose ps

# Verify connection string in .env
# Check database logs
docker compose logs [db-name]

# Reset database
docker compose down -v
docker compose up -d
```

### **Inter-Service Communication Fails:**
```bash
# Check service URLs in environment variables
# Verify services are on same Docker network
# Check JWT token is being passed
# Examine gateway logs
docker compose logs api_gateway
```

---

## 📚 Additional Resources

### **Documentation:**
- [Student Service Complete Docs](./STUDENT_SERVICE_COMPLETE.md)
- [Billing Service README](./services/billing-service/README.md)
- [Setup Guide](./SETUP_GUIDE.md)
- [Quick Start](./QUICK_START.md)

### **External References:**
- Spring Boot: https://spring.io/projects/spring-boot
- FastAPI: https://fastapi.tiangolo.com
- Express.js: https://expressjs.com
- JAX-WS: https://javaee.github.io/metro-jax-ws
- Prisma: https://www.prisma.io
- Docker: https://docs.docker.com

---

## 👥 Team Collaboration

### **Authors:**
- Mili Yassine - [yassinemili.me](https://yassinemili.me)
- Battikh Youssef - [LinkedIn](https://www.linkedin.com/in/ysf-battikh)
- Ksouri Fahmi

### **Communication Channels:**
- Daily standups (15 minutes)
- Code reviews on pull requests
- Documentation updates on Wiki
- Issue tracking on GitHub Issues

---

## ⚡ Quick Commands Reference

```bash
# Start all services
cd docker && docker compose up -d

# Stop all services
docker compose stop

# View logs
docker compose logs -f [service-name]

# Rebuild service
docker compose build [service-name]

# Remove everything
docker compose down -v

# Test Student Service
curl http://localhost:8082/api/v1/students

# Test Billing Service
curl http://localhost:5000/

# Access Swagger
open http://localhost:8082/api-docs
```

---

**Last Updated:** December 7, 2025  
**Project Status:** 40% Complete (2/5 services ready)  
**Next Priority:** Authentication Service → API Gateway → Courses/Grades Services