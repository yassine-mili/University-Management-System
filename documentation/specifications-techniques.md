# 📐 Spécifications Techniques - Système de Gestion Universitaire

## Table des Matières

1. [Vue d'ensemble](#1-vue-densemble)
2. [Architecture des Services](#2-architecture-des-services)
3. [Spécifications API](#3-spécifications-api)
4. [Modèle de Données](#4-modèle-de-données)
5. [Sécurité](#5-sécurité)
6. [Communication Inter-Services](#6-communication-inter-services)
7. [Configuration et Déploiement](#7-configuration-et-déploiement)
8. [Gestion des Erreurs](#8-gestion-des-erreurs)
9. [Performance et Optimisation](#9-performance-et-optimisation)

---

## 1. Vue d'ensemble

### 1.1 Stack Technologique Complète

```
Frontend Layer:
├── React 18.2.0
├── TypeScript 4.9.5
├── Material-UI 5.14.0
├── Axios pour les appels API
└── React Router v6

API Gateway Layer:
├── Spring Cloud Gateway 4.0.7
├── Spring Boot 3.1.5
├── Redis 7.0 (rate limiting & cache)
└── JWT Validation

Microservices Layer:
├── Auth Service
│   ├── Spring Boot 3.1.5
│   ├── Spring Security 6.1.4
│   ├── JWT (jjwt 0.11.5)
│   └── PostgreSQL 14
│
├── Student Service
│   ├── Node.js 18.x
│   ├── Express.js 4.18.2
│   ├── Prisma ORM 5.0.0
│   └── PostgreSQL 14
│
├── Course Service
│   ├── Spring Boot 3.1.5
│   ├── Apache CXF (SOAP)
│   ├── Spring Data JPA
│   └── PostgreSQL 14
│
├── Grade Service
│   ├── Python 3.10
│   ├── FastAPI 0.104.0
│   ├── SQLAlchemy 2.0
│   └── PostgreSQL 14
│
└── Billing Service
    ├── .NET 7.0
    ├── ASP.NET Core
    ├── Entity Framework Core
    └── PostgreSQL 14

Infrastructure:
├── Docker 24.0+
├── Docker Compose 2.20+
├── PostgreSQL 14-alpine
└── Nginx (frontend serving)
```

### 1.2 Ports et URL

| Service         | Port | URL de Base            | Protocole      |
| --------------- | ---- | ---------------------- | -------------- |
| Frontend        | 3000 | http://localhost:3000  | HTTP           |
| API Gateway     | 8080 | http://localhost:8080  | HTTP           |
| Auth Service    | 8081 | http://localhost:8081  | REST           |
| Student Service | 8082 | http://localhost:8082  | REST           |
| Course Service  | 8083 | http://localhost:8083  | REST + SOAP    |
| Grade Service   | 8084 | http://localhost:8084  | REST           |
| Billing Service | 5000 | http://localhost:5000  | REST + SOAP    |
| Redis           | 6379 | redis://localhost:6379 | Redis Protocol |

### 1.3 Bases de Données

| Base de Données | Port | Service         | Schéma                    |
| --------------- | ---- | --------------- | ------------------------- |
| auth_db         | 5433 | Auth Service    | users, roles, permissions |
| students_db     | 5432 | Student Service | students                  |
| courses_db      | 5434 | Course Service  | courses, student_courses  |
| grades_db       | 5435 | Grade Service   | grades                    |
| billing_db      | 5436 | Billing Service | invoices, payments        |

---

## 2. Architecture des Services

### 2.1 Service d'Authentification (Spring Boot)

#### 2.1.1 Structure du Projet

```
auth-service/
├── src/main/java/com/universite/auth/
│   ├── controller/
│   │   └── AuthController.java
│   ├── service/
│   │   ├── AuthService.java
│   │   └── JwtService.java
│   ├── repository/
│   │   └── UserRepository.java
│   ├── model/
│   │   ├── User.java
│   │   └── Role.java
│   ├── dto/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   └── AuthResponse.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   └── JwtConfig.java
│   └── exception/
│       └── AuthException.java
├── src/main/resources/
│   ├── application.yml
│   └── application-docker.yml
└── pom.xml
```

#### 2.1.2 Dépendances Maven Principales

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
</dependencies>
```

#### 2.1.3 Configuration JWT

```yaml
jwt:
  secret: UniversityManagementSystemSecretKey2024VeryLongSecretForHS512Algorithm
  expiration: 86400000 # 24 heures en millisecondes
  issuer: university-management-system
```

#### 2.1.4 Algorithme de Génération de Token

```
1. Réception des credentials (email, password)
2. Validation du mot de passe (BCrypt compare)
3. Récupération des informations utilisateur
4. Construction du JWT:
   - Header: {"alg": "HS512", "typ": "JWT"}
   - Payload: {
       "sub": "email@example.com",
       "userId": "123",
       "studentId": "STU000123",
       "role": "STUDENT",
       "iat": timestamp,
       "exp": timestamp + 24h
     }
   - Signature: HMACSHA512(base64(header) + "." + base64(payload), secret)
5. Retour du token au format: header.payload.signature
```

### 2.2 Service Étudiants (Node.js)

#### 2.2.1 Structure du Projet

```
student-service/
├── src/
│   ├── controllers/
│   │   └── studentController.js
│   ├── services/
│   │   └── studentService.js
│   ├── middleware/
│   │   ├── authMiddleware.js
│   │   └── errorHandler.js
│   ├── routes/
│   │   └── studentRoutes.js
│   ├── config/
│   │   └── database.js
│   └── index.js
├── prisma/
│   ├── schema.prisma
│   └── migrations/
├── package.json
└── Dockerfile
```

#### 2.2.2 Schéma Prisma

```prisma
datasource db {
  provider = "postgresql"
  url      = env("DATABASE_URL")
}

generator client {
  provider = "prisma-client-js"
}

model Student {
  id              Int       @id @default(autoincrement())
  numeroEtudiant  String    @unique @map("numero_etudiant")
  email           String    @unique
  nom             String
  prenom          String
  dateNaissance   DateTime? @map("date_naissance") @db.Date
  adresse         String?
  telephone       String?
  createdAt       DateTime  @default(now()) @map("created_at")
  updatedAt       DateTime  @updatedAt @map("updated_at")

  @@map("students")
}
```

#### 2.2.3 Configuration Express

```javascript
const express = require("express");
const cors = require("cors");
const helmet = require("helmet");
const rateLimit = require("express-rate-limit");

const app = express();

// Middleware de sécurité
app.use(helmet());
app.use(
  cors({
    origin: process.env.ALLOWED_ORIGINS?.split(",") || "*",
    credentials: true,
  })
);

// Rate limiting
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // 100 requêtes par IP
});
app.use("/api/", limiter);

// Body parsing
app.use(express.json({ limit: "10mb" }));
app.use(express.urlencoded({ extended: true }));
```

### 2.3 Service Cours (Spring Boot + SOAP)

#### 2.3.1 Configuration SOAP avec Apache CXF

```xml
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-spring-boot-starter-jaxws</artifactId>
    <version>4.0.0</version>
</dependency>
```

#### 2.3.2 Interface SOAP

```java
@WebService(serviceName = "CourseService")
public interface CourseWebService {

    @WebMethod
    @WebResult(name = "course")
    CourseDTO getCourseDetails(
        @WebParam(name = "courseId") Long courseId
    );

    @WebMethod
    @WebResult(name = "courses")
    List<CourseDTO> getAllCourses();

    @WebMethod
    @WebResult(name = "success")
    boolean createCourse(
        @WebParam(name = "course") CourseDTO course
    );

    @WebMethod
    @WebResult(name = "success")
    boolean updateCourse(
        @WebParam(name = "courseId") Long courseId,
        @WebParam(name = "course") CourseDTO course
    );

    @WebMethod
    @WebResult(name = "success")
    boolean deleteCourse(
        @WebParam(name = "courseId") Long courseId
    );
}
```

#### 2.3.3 Configuration WSDL

```xml
<!-- WSDL généré automatiquement accessible à: -->
<!-- http://localhost:8083/soap/courses?wsdl -->

<definitions targetNamespace="http://courses.universite.com/">
  <types>
    <xsd:schema>
      <xsd:complexType name="CourseDTO">
        <xsd:sequence>
          <xsd:element name="id" type="xsd:long"/>
          <xsd:element name="code" type="xsd:string"/>
          <xsd:element name="titre" type="xsd:string"/>
          <xsd:element name="description" type="xsd:string"/>
          <xsd:element name="credits" type="xsd:int"/>
          <xsd:element name="teacherId" type="xsd:string"/>
          <xsd:element name="capacity" type="xsd:int"/>
        </xsd:sequence>
      </xsd:complexType>
    </xsd:schema>
  </types>
  <!-- Operations et bindings... -->
</definitions>
```

#### 2.3.4 Endpoints REST Additionnels

```java
@RestController
@RequestMapping("/api/v1/courses")
public class CourseRestController {

    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<EnrollmentResponse> enrollInCourse(
        @PathVariable Long courseId,
        @RequestBody EnrollmentRequest request
    ) {
        // Extraction du numéro d'étudiant: "STU000025" -> 25
        String studentId = request.getStudentId();
        Long numericId = extractNumericId(studentId);

        // Vérification de la capacité
        // Création de l'enrollment dans student_courses
        // Retour de la confirmation
    }

    @GetMapping("/student/{studentId}/enrollments")
    public ResponseEntity<List<CourseDTO>> getStudentEnrollments(
        @PathVariable String studentId
    ) {
        // Récupération des cours inscrits pour l'étudiant
    }

    @DeleteMapping("/{courseId}/enroll/{studentId}")
    public ResponseEntity<Void> unenrollFromCourse(
        @PathVariable Long courseId,
        @PathVariable String studentId
    ) {
        // Désinscription du cours
    }
}
```

### 2.4 Service Notes (Python FastAPI)

#### 2.4.1 Structure du Projet

```
grade-service/
├── app/
│   ├── main.py
│   ├── config.py
│   ├── models/
│   │   └── grade.py
│   ├── schemas/
│   │   └── grade_schema.py
│   ├── routers/
│   │   └── grade_router.py
│   ├── services/
│   │   └── grade_service.py
│   └── database.py
├── requirements.txt
└── Dockerfile
```

#### 2.4.2 Modèle SQLAlchemy

```python
from sqlalchemy import Column, Integer, String, Numeric, Date, DateTime
from sqlalchemy.sql import func
from app.database import Base

class Grade(Base):
    __tablename__ = "grades"

    id = Column(Integer, primary_key=True, index=True)
    student_id = Column(String(20), nullable=False, index=True)
    course_code = Column(String(20), nullable=False, index=True)
    grade = Column(Numeric(5, 2), nullable=False)
    coefficient = Column(Integer, default=1)
    exam_date = Column(Date, nullable=True)
    comments = Column(String, nullable=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())
```

#### 2.4.3 Configuration FastAPI

```python
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.gzip import GZipMiddleware

app = FastAPI(
    title="Grade Service API",
    description="Service de gestion des notes",
    version="1.0.0",
    docs_url="/api-docs",
    redoc_url="/redoc"
)

# CORS Configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Compression
app.add_middleware(GZipMiddleware, minimum_size=1000)
```

### 2.5 Service Facturation (.NET Core)

#### 2.5.1 Structure du Projet

```
billing-service/
├── Controllers/
│   ├── InvoiceController.cs
│   └── InvoiceSoapController.cs
├── Services/
│   ├── InvoiceService.cs
│   └── PaymentService.cs
├── Repositories/
│   └── InvoiceRepository.cs
├── Models/
│   ├── Invoice.cs
│   └── Payment.cs
├── DTOs/
│   ├── InvoiceDto.cs
│   └── PaymentDto.cs
├── Middleware/
│   └── ErrorHandlingMiddleware.cs
├── Program.cs
├── appsettings.json
└── BillingService.csproj
```

#### 2.5.2 Modèle Entity Framework

```csharp
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

[Table("invoices")]
public class Invoice
{
    [Key]
    [Column("id")]
    public int Id { get; set; }

    [Required]
    [Column("student_id")]
    [MaxLength(20)]
    public string StudentId { get; set; }

    [Required]
    [Column("amount", TypeName = "decimal(10,2)")]
    public decimal Amount { get; set; }

    [Column("description")]
    public string Description { get; set; }

    [Required]
    [Column("status")]
    [MaxLength(20)]
    public string Status { get; set; }  // PENDING, PAID, OVERDUE, CANCELLED

    [Column("due_date")]
    public DateTime? DueDate { get; set; }

    [Column("paid_date")]
    public DateTime? PaidDate { get; set; }

    [Column("created_at")]
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    [Column("updated_at")]
    public DateTime? UpdatedAt { get; set; }
}
```

#### 2.5.3 Configuration SOAP (.NET)

```csharp
using System.ServiceModel;

[ServiceContract]
public interface IBillingService
{
    [OperationContract]
    List<InvoiceDto> GetInvoicesByStudent(string studentId);

    [OperationContract]
    InvoiceDto CreateInvoice(CreateInvoiceRequest request);

    [OperationContract]
    PaymentResponse ProcessPayment(PaymentRequest request);

    [OperationContract]
    InvoiceDto GetInvoiceDetails(int invoiceId);
}

// Configuration dans Program.cs
builder.Services.AddSoapCore();
app.UseSoapEndpoint<IBillingService>(
    "/soap/billing",
    new SoapEncoderOptions(),
    SoapSerializer.DataContractSerializer
);
```

### 2.6 API Gateway (Spring Cloud Gateway)

#### 2.6.1 Configuration des Routes

```yaml
spring:
  cloud:
    gateway:
      routes:
        # Auth Service Routes (Public)
        - id: auth-service
          uri: http://auth-service:8081
          predicates:
            - Path=/api/v1/auth/**
          filters:
            - StripPrefix=1

        # Student Service Routes (Protected)
        - id: student-service
          uri: http://student-service:8082
          predicates:
            - Path=/api/v1/students/**
          filters:
            - StripPrefix=1
            - JwtAuthenticationFilter

        # Course Service Routes (Protected)
        - id: course-service
          uri: http://courses-service:8083
          predicates:
            - Path=/api/v1/courses/**
          filters:
            - StripPrefix=1
            - JwtAuthenticationFilter

        # Grade Service Routes (Protected)
        - id: grade-service
          uri: http://grades-service:8084
          predicates:
            - Path=/api/v1/grades/**
          filters:
            - StripPrefix=1
            - JwtAuthenticationFilter

        # Billing Service Routes (Protected)
        - id: billing-service
          uri: http://billing-service:5000
          predicates:
            - Path=/api/v1/invoices/**
          filters:
            - StripPrefix=1
            - JwtAuthenticationFilter
```

#### 2.6.2 Filtre JWT Personnalisé

```java
@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Extraction du token
        String authHeader = request.getHeaders()
            .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Missing authorization header",
                          HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try {
            // Validation du token
            if (!jwtService.validateToken(token)) {
                return onError(exchange, "Invalid token",
                              HttpStatus.UNAUTHORIZED);
            }

            // Extraction des claims
            Claims claims = jwtService.extractClaims(token);

            // Ajout des headers pour les services en aval
            ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Id", claims.get("userId", String.class))
                .header("X-User-Email", claims.getSubject())
                .header("X-User-Role", claims.get("role", String.class))
                .build();

            return chain.filter(exchange.mutate()
                .request(modifiedRequest)
                .build());

        } catch (Exception e) {
            return onError(exchange, "Token validation failed",
                          HttpStatus.UNAUTHORIZED);
        }
    }
}
```

#### 2.6.3 Configuration Rate Limiting (Redis)

```yaml
spring:
  redis:
    host: redis
    port: 6379
  cloud:
    gateway:
      routes:
        - id: student-service
          # ... autres configs
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10 # 10 requêtes/seconde
                redis-rate-limiter.burstCapacity: 20 # Max 20 requêtes burst
                redis-rate-limiter.requestedTokens: 1
```

---

## 3. Spécifications API

### 3.1 Format des Réponses Standard

#### 3.1.1 Réponse Succès

```json
{
  "success": true,
  "data": {
    // Données de la réponse
  },
  "message": "Operation completed successfully",
  "timestamp": "2024-12-16T10:30:00Z",
  "traceId": "abc-123-def-456"
}
```

#### 3.1.2 Réponse Erreur

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input data",
    "details": [
      {
        "field": "email",
        "message": "Email is required"
      }
    ]
  },
  "timestamp": "2024-12-16T10:30:00Z",
  "traceId": "abc-123-def-456",
  "path": "/api/v1/students"
}
```

### 3.2 Codes d'Erreur Standards

| Code                  | Description                  | HTTP Status |
| --------------------- | ---------------------------- | ----------- |
| `AUTH_TOKEN_MISSING`  | Token JWT manquant           | 401         |
| `AUTH_TOKEN_INVALID`  | Token JWT invalide           | 401         |
| `AUTH_TOKEN_EXPIRED`  | Token JWT expiré             | 401         |
| `INVALID_CREDENTIALS` | Email/mot de passe incorrect | 401         |
| `FORBIDDEN`           | Accès interdit               | 403         |
| `NOT_FOUND`           | Ressource non trouvée        | 404         |
| `VALIDATION_ERROR`    | Erreur de validation         | 400         |
| `DUPLICATE_ENTRY`     | Entrée en double             | 409         |
| `SERVICE_UNAVAILABLE` | Service indisponible         | 503         |
| `INTERNAL_ERROR`      | Erreur interne               | 500         |

### 3.3 API Authentication Service

#### POST /api/v1/auth/register

**Description:** Inscription d'un nouvel utilisateur

**Request:**

```json
{
  "email": "student@university.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe",
  "role": "STUDENT"
}
```

**Response (201 Created):**

```json
{
  "success": true,
  "data": {
    "user": {
      "id": 123,
      "email": "student@university.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "STUDENT",
      "studentId": "STU000123"
    },
    "token": "eyJhbGciOiJIUzUxMiJ9..."
  }
}
```

#### POST /api/v1/auth/login

**Description:** Connexion utilisateur

**Request:**

```json
{
  "email": "student@university.com",
  "password": "SecurePass123!"
}
```

**Response (200 OK):**

```json
{
  "success": true,
  "data": {
    "user": {
      "id": 123,
      "email": "student@university.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "STUDENT",
      "studentId": "STU000123"
    },
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "expiresIn": 86400
  }
}
```

### 3.4 API Student Service

#### POST /api/v1/students

**Description:** Créer un étudiant

**Headers:**

```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request:**

```json
{
  "numeroEtudiant": "STU000001",
  "email": "student@example.com",
  "nom": "Dupont",
  "prenom": "Marie",
  "dateNaissance": "2000-05-15",
  "adresse": "123 Rue de la Paix, Paris",
  "telephone": "+33612345678"
}
```

**Response (201 Created):**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "numeroEtudiant": "STU000001",
    "email": "student@example.com",
    "nom": "Dupont",
    "prenom": "Marie",
    "dateNaissance": "2000-05-15",
    "adresse": "123 Rue de la Paix, Paris",
    "telephone": "+33612345678",
    "createdAt": "2024-12-16T10:00:00Z",
    "updatedAt": "2024-12-16T10:00:00Z"
  }
}
```

#### GET /api/v1/students

**Description:** Liste des étudiants (paginée)

**Query Parameters:**

- `page` (optional, default: 1)
- `limit` (optional, default: 10, max: 100)
- `search` (optional, search in nom/prenom/email)

**Request:**

```
GET /api/v1/students?page=1&limit=10&search=Dupont
```

**Response (200 OK):**

```json
{
  "success": true,
  "data": {
    "students": [
      {
        "id": 1,
        "numeroEtudiant": "STU000001",
        "email": "student@example.com",
        "nom": "Dupont",
        "prenom": "Marie"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 5,
      "totalItems": 50,
      "itemsPerPage": 10
    }
  }
}
```

### 3.5 API Course Service (REST)

#### POST /api/v1/courses/{courseId}/enroll

**Description:** Inscription d'un étudiant à un cours

**Request:**

```json
{
  "studentId": "STU000025"
}
```

**Response (200 OK):**

```json
{
  "success": true,
  "data": {
    "enrollmentId": 123,
    "studentId": "STU000025",
    "courseId": 1,
    "courseCode": "CS101",
    "enrolledAt": "2024-12-16T10:00:00Z"
  }
}
```

#### GET /api/v1/student/{studentId}/enrollments

**Description:** Liste des cours inscrits pour un étudiant

**Response (200 OK):**

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "code": "CS101",
      "titre": "Introduction à la Programmation",
      "credits": 3,
      "teacherId": "TEA000001",
      "enrolledAt": "2024-12-16T10:00:00Z"
    }
  ]
}
```

### 3.6 API Course Service (SOAP)

**WSDL URL:** http://localhost:8083/soap/courses?wsdl

**Exemple Request SOAP - getCourseDetails:**

```xml
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:cou="http://courses.universite.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <cou:getCourseDetails>
      <courseId>1</courseId>
    </cou:getCourseDetails>
  </soapenv:Body>
</soapenv:Envelope>
```

**Response SOAP:**

```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:getCourseDetailsResponse xmlns:ns2="http://courses.universite.com/">
      <course>
        <id>1</id>
        <code>CS101</code>
        <titre>Introduction à la Programmation</titre>
        <description>Cours d'introduction</description>
        <credits>3</credits>
        <teacherId>TEA000001</teacherId>
        <capacity>30</capacity>
      </course>
    </ns2:getCourseDetailsResponse>
  </soap:Body>
</soap:Envelope>
```

### 3.7 API Grade Service

#### POST /api/v1/grades

**Description:** Créer une note

**Request:**

```json
{
  "studentId": "STU000001",
  "courseCode": "CS101",
  "grade": 15.5,
  "coefficient": 2,
  "examDate": "2024-12-15",
  "comments": "Examen final"
}
```

**Response (201 Created):**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "studentId": "STU000001",
    "courseCode": "CS101",
    "grade": 15.5,
    "coefficient": 2,
    "examDate": "2024-12-15",
    "comments": "Examen final",
    "createdAt": "2024-12-16T10:00:00Z"
  }
}
```

#### GET /api/v1/grades/student/{studentId}

**Description:** Notes d'un étudiant

**Response (200 OK):**

```json
{
  "success": true,
  "data": {
    "studentId": "STU000001",
    "grades": [
      {
        "id": 1,
        "courseCode": "CS101",
        "grade": 15.5,
        "coefficient": 2,
        "examDate": "2024-12-15"
      }
    ],
    "average": 15.5,
    "totalCredits": 6
  }
}
```

### 3.8 API Billing Service (REST)

#### GET /api/v1/invoices/student/{studentId}

**Description:** Factures d'un étudiant

**Response (200 OK):**

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "studentId": "STU000001",
      "amount": 5000.0,
      "description": "Frais de scolarité - Semestre 1",
      "status": "PENDING",
      "dueDate": "2024-12-31",
      "createdAt": "2024-12-16T10:00:00Z"
    }
  ]
}
```

#### POST /api/v1/invoices/{id}/pay

**Description:** Enregistrer un paiement

**Request:**

```json
{
  "paymentMethod": "CREDIT_CARD",
  "transactionId": "TXN-123456",
  "paidAmount": 5000.0
}
```

**Response (200 OK):**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "status": "PAID",
    "paidDate": "2024-12-16T10:00:00Z",
    "paidAmount": 5000.0
  }
}
```

---

## 4. Modèle de Données

### 4.1 Schéma Relationnel Complet

```sql
-- Auth Database (auth_db)
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(20) CHECK (role IN ('STUDENT', 'TEACHER', 'ADMIN')),
    student_id VARCHAR(20) UNIQUE,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_student_id ON users(student_id);

-- Students Database (students_db)
CREATE TABLE students (
    id SERIAL PRIMARY KEY,
    numero_etudiant VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    date_naissance DATE,
    adresse TEXT,
    telephone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_students_numero ON students(numero_etudiant);
CREATE INDEX idx_students_email ON students(email);

-- Courses Database (courses_db)
CREATE TABLE courses (
    id SERIAL PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    credits INTEGER DEFAULT 3 CHECK (credits > 0),
    teacher_id VARCHAR(20),
    capacity INTEGER DEFAULT 30 CHECK (capacity > 0),
    enrolled_count INTEGER DEFAULT 0 CHECK (enrolled_count >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE student_courses (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL,
    course_id INTEGER NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(student_id, course_id)
);

CREATE INDEX idx_courses_code ON courses(code);
CREATE INDEX idx_student_courses_student ON student_courses(student_id);
CREATE INDEX idx_student_courses_course ON student_courses(course_id);

-- Grades Database (grades_db)
CREATE TABLE grades (
    id SERIAL PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    course_code VARCHAR(20) NOT NULL,
    grade NUMERIC(5,2) CHECK (grade >= 0 AND grade <= 20),
    coefficient INTEGER DEFAULT 1 CHECK (coefficient > 0),
    exam_date DATE,
    comments TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_grades_student ON grades(student_id);
CREATE INDEX idx_grades_course ON grades(course_code);
CREATE INDEX idx_grades_composite ON grades(student_id, course_code);

-- Billing Database (billing_db)
CREATE TABLE invoices (
    id SERIAL PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    amount NUMERIC(10,2) NOT NULL CHECK (amount >= 0),
    description TEXT,
    status VARCHAR(20) CHECK (status IN ('PENDING', 'PAID', 'OVERDUE', 'CANCELLED')),
    due_date DATE,
    paid_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_invoices_student ON invoices(student_id);
CREATE INDEX idx_invoices_status ON invoices(status);
```

### 4.2 Migrations et Versioning

Chaque service gère ses migrations de manière indépendante:

**Node.js (Prisma):**

```bash
npx prisma migrate dev --name init
npx prisma migrate deploy  # Production
```

**Spring Boot (Flyway):**

```sql
-- V1__init.sql
CREATE TABLE users (...);

-- V2__add_indexes.sql
CREATE INDEX idx_users_email ON users(email);
```

**Python (Alembic):**

```bash
alembic revision --autogenerate -m "init"
alembic upgrade head
```

**.NET (Entity Framework):**

```bash
dotnet ef migrations add InitialCreate
dotnet ef database update
```

---

## 5. Sécurité

### 5.1 Authentification JWT

#### 5.1.1 Structure du Token

```
Header:
{
  "alg": "HS512",
  "typ": "JWT"
}

Payload:
{
  "sub": "student@example.com",
  "userId": "123",
  "studentId": "STU000123",
  "role": "STUDENT",
  "iat": 1702729200,
  "exp": 1702815600,
  "iss": "university-management-system"
}

Signature:
HMACSHA512(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret_key
)
```

#### 5.1.2 Validation du Token

```java
public boolean validateToken(String token) {
    try {
        Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token);
        return true;
    } catch (ExpiredJwtException e) {
        throw new AuthException("Token expired");
    } catch (MalformedJwtException e) {
        throw new AuthException("Invalid token format");
    } catch (SignatureException e) {
        throw new AuthException("Invalid token signature");
    }
}
```

### 5.2 Hachage des Mots de Passe

**Algorithme:** BCrypt avec coût 12

```java
// Encoding
String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));

// Validation
boolean isValid = BCrypt.checkpw(plainPassword, hashedPassword);
```

### 5.3 Configuration CORS

```yaml
cors:
  allowed-origins:
    - http://localhost:3000
    - http://frontend:80
  allowed-methods:
    - GET
    - POST
    - PUT
    - DELETE
    - OPTIONS
  allowed-headers:
    - Authorization
    - Content-Type
    - X-Requested-With
  exposed-headers:
    - X-Total-Count
  allow-credentials: true
  max-age: 3600
```

### 5.4 Rate Limiting

```yaml
# Configuration par service via API Gateway
rate-limit:
  auth-service:
    requests-per-minute: 20 # Login attempts
  student-service:
    requests-per-minute: 100
  course-service:
    requests-per-minute: 100
  grade-service:
    requests-per-minute: 100
  billing-service:
    requests-per-minute: 50
```

---

## 6. Communication Inter-Services

### 6.1 Appels REST Synchrones

```java
// Exemple: Course Service → Student Service
@Service
public class StudentServiceClient {

    private final RestTemplate restTemplate;

    public StudentDTO validateStudent(Long studentId, String traceId) {
        String url = "http://student-service:8082/api/v1/students/" + studentId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Trace-Id", traceId);

        try {
            ResponseEntity<StudentDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                StudentDTO.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new BusinessException("Student not found");
        } catch (Exception e) {
            throw new ServiceException("student-service is currently unavailable");
        }
    }
}
```

### 6.2 Circuit Breaker Pattern

```java
@CircuitBreaker(name = "studentService", fallbackMethod = "studentServiceFallback")
public StudentDTO getStudent(Long id) {
    return studentServiceClient.getStudent(id);
}

public StudentDTO studentServiceFallback(Long id, Exception e) {
    // Retour de données en cache ou erreur gracieuse
    return StudentDTO.builder()
        .id(id)
        .available(false)
        .build();
}
```

### 6.3 Timeout Configuration

```yaml
# API Gateway timeouts
spring:
  cloud:
    gateway:
      httpclient:
        connect-timeout: 5000 # 5 seconds
        response-timeout: 30s # 30 seconds
```

---

## 7. Configuration et Déploiement

### 7.1 Variables d'Environnement

**Auth Service (.env):**

```env
DB_HOST=auth_db
DB_PORT=5432
DB_NAME=auth_db
DB_USER=postgres
DB_PASSWORD=postgres
JWT_SECRET=UniversityManagementSystemSecretKey2024VeryLongSecretForHS512Algorithm
JWT_EXPIRATION=86400000
```

**Student Service (.env):**

```env
DATABASE_URL=postgresql://user:password@student_db:5432/students_db
PORT=8082
NODE_ENV=production
LOG_LEVEL=info
```

**Course Service (application-docker.yml):**

```yaml
spring:
  datasource:
    url: jdbc:postgresql://courses_db:5432/courses_db
    username: user
    password: password
  jpa:
    hibernate:
      ddl-auto: update
```

### 7.2 Docker Compose Configuration

```yaml
version: "3.8"

services:
  # Bases de données
  student_db:
    image: postgres:14-alpine
    environment:
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
      POSTGRES_DB: students_db
    volumes:
      - student_db_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U $$POSTGRES_USER -d $$POSTGRES_DB"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Services
  student_service:
    build: ../services/student-service
    environment:
      DATABASE_URL: "postgresql://user:password@student_db:5432/students_db"
      PORT: 8082
    depends_on:
      student_db:
        condition: service_healthy
    restart: unless-stopped

volumes:
  student_db_data:
  auth_db_data:
  courses_db_data:
  grades_db_data:
  billing_db_data:
```

### 7.3 Health Checks

**Spring Boot (Actuator):**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

**Node.js (Express):**

```javascript
app.get("/health", (req, res) => {
  res.json({
    status: "UP",
    service: "student-service",
    timestamp: new Date().toISOString(),
    database: isDatabaseConnected() ? "UP" : "DOWN",
  });
});
```

**FastAPI:**

```python
@app.get("/health")
async def health_check():
    return {
        "status": "UP",
        "service": "grade-service",
        "timestamp": datetime.utcnow().isoformat()
    }
```

**.NET:**

```csharp
app.MapGet("/health", () => new {
    Status = "UP",
    Service = "billing-service",
    Timestamp = DateTime.UtcNow
});
```

---

## 8. Gestion des Erreurs

### 8.1 Hiérarchie des Exceptions

```java
// Exception de base
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Map<String, Object> metadata;
}

// Exceptions spécifiques
public class ValidationException extends BusinessException {
    public ValidationException(String message, List<FieldError> errors) {
        super(ErrorCode.VALIDATION_ERROR, message);
        this.fieldErrors = errors;
    }
}

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resource, Object id) {
        super(ErrorCode.NOT_FOUND,
              String.format("%s with id %s not found", resource, id));
    }
}

public class AuthenticationException extends BusinessException {
    public AuthenticationException(String message) {
        super(ErrorCode.AUTH_FAILED, message);
    }
}
```

### 8.2 Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        ErrorResponse response = ErrorResponse.builder()
            .success(false)
            .error(ErrorDetail.builder()
                .code(ex.getErrorCode())
                .message(ex.getMessage())
                .details(ex.getFieldErrors())
                .build())
            .timestamp(Instant.now())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        // Similar structure
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
```

### 8.3 Logging Structuré

```json
{
  "timestamp": "2024-12-16T10:30:00.123Z",
  "level": "ERROR",
  "service": "student-service",
  "traceId": "abc-123-def-456",
  "message": "Failed to create student",
  "error": {
    "type": "ValidationException",
    "message": "Email already exists",
    "stackTrace": "..."
  },
  "context": {
    "userId": "123",
    "endpoint": "/api/v1/students",
    "method": "POST"
  }
}
```

---

## 9. Performance et Optimisation

### 9.1 Stratégies de Cache

**Redis Cache Configuration:**

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 600000 # 10 minutes
      cache-null-values: false
  redis:
    host: redis
    port: 6379
```

**Cacheable Methods:**

```java
@Cacheable(value = "courses", key = "#courseId")
public CourseDTO getCourse(Long courseId) {
    return courseRepository.findById(courseId)
        .map(courseMapper::toDTO)
        .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
}

@CacheEvict(value = "courses", key = "#courseId")
public void updateCourse(Long courseId, CourseDTO dto) {
    // Update logic
}
```

### 9.2 Pagination et Filtrage

```java
@GetMapping
public Page<StudentDTO> getStudents(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(required = false) String search
) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

    if (search != null && !search.isEmpty()) {
        return studentService.searchStudents(search, pageable);
    }
    return studentService.getAllStudents(pageable);
}
```

### 9.3 Optimisation Base de Données

**Index Composites:**

```sql
CREATE INDEX idx_grades_student_course ON grades(student_id, course_code);
CREATE INDEX idx_invoices_student_status ON invoices(student_id, status);
```

**Query Optimization (N+1 Problem):**

```java
// Éviter N+1
@EntityGraph(attributePaths = {"teacher", "enrollments"})
List<Course> findAllWithDetails();

// Au lieu de:
List<Course> courses = courseRepository.findAll();
courses.forEach(c -> c.getTeacher()); // N+1 queries
```

### 9.4 Compression et Gzip

**Spring Boot:**

```yaml
server:
  compression:
    enabled: true
    mime-types: application/json,application/xml,text/html,text/xml,text/plain
    min-response-size: 1024
```

**Node.js:**

```javascript
const compression = require("compression");
app.use(compression());
```

---

## 10. Monitoring et Observabilité

### 10.1 Logs Centralisés

Tous les services loggent au format JSON avec les champs:

- `timestamp`
- `level` (INFO, WARN, ERROR)
- `service` (nom du service)
- `traceId` (pour tracer les requêtes)
- `message`
- `context` (userId, endpoint, etc.)

### 10.2 Métriques

**Spring Boot Actuator:**

```
GET /actuator/metrics/http.server.requests
GET /actuator/metrics/jvm.memory.used
GET /actuator/metrics/system.cpu.usage
```

### 10.3 Distributed Tracing

Header `X-Trace-Id` propagé entre tous les services pour tracer une requête complète à travers le système.

---

**Version:** 1.0  
**Date:** 16 Décembre 2024  
**Statut:** Document Technique Complet
