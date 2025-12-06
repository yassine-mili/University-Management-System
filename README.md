# 🎓 University Management System - SOA Architecture

A multi-technology Service-Oriented Architecture (SOA) project demonstrating 
microservices design patterns, API Gateway implementation, and inter-service 
communication.

## 🏗️ Architecture Overview

This system is composed of 5 independent microservices, each built with a 
different technology stack to showcase polyglot architecture:

```
┌─────────────┐
│   Client    │
│ Application │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────────┐
│         API Gateway                 │
│      (Spring Cloud Gateway)         │
│  - Routage                          │
│  - Authentification centralisée     │
│  - Load balancing                   │
└──────┬──────────────────────────────┘
       │
       ├─────────────┬─────────────┬─────────────┬─────────────┐
       ▼             ▼             ▼             ▼             ▼
┌─────────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────┐
│Auth Service │ │ Student  │ │ Course   │ │  Grade   │ │   Billing    │
│   (REST)    │ │ Service  │ │ Service  │ │ Service  │ │   Service    │
│ Spring Boot │ │  (REST)  │ │  (SOAP)  │ │  (REST)  │ │   (SOAP)     │
│             │ │ Node.js  │ │   Java   │ │  Python  │ │  .NET Core   │
└─────────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────────┘
       │             │             │             │             │
       └─────────────┴─────────────┴─────────────┴─────────────┘
                              │
                       ┌──────▼──────┐
                       │  Bases de   │
                       │  Données    │
                       └─────────────┘
```

## 🎯 Microservices

### 1. Authentication Service (Spring Boot)
- JWT token generation and validation
- User authentication and authorization
- Role-based access control

### 2. Student Service (Node.js + Express)
- CRUD operations for student records
- RESTful API endpoints
- PostgreSQL data persistence

### 3. Grades Service (Python + FastAPI)
- Grade management and calculations
- Student performance analytics
- RESTful API with FastAPI

### 4. Billing Service (.NET Core)
- Tuition and fee management
- SOAP web service implementation
- SQL Server database

### 5. API Gateway (Spring Cloud Gateway)
- Centralized routing and load balancing
- Request/response transformation
- Security layer (JWT validation)
- Service discovery

## 🛠️ Technologies

**Backend Frameworks:**
- Spring Boot (Java)
- Node.js + Express (JavaScript)
- FastAPI (Python)
- .NET Core (C#)

**API Protocols:**
- REST APIs
- SOAP Web Services

**Gateway & Orchestration:**
- Spring Cloud Gateway
- Service-to-service communication

**Security:**
- JWT Authentication
- Spring Security

**DevOps:**
- Docker + Docker Compose
- Multi-container deployment
- CI/CD pipeline

## 🔑 Key Features

- ✅ Multi-technology microservices architecture
- ✅ Centralized API Gateway for routing
- ✅ JWT-based authentication across all services
- ✅ Both REST and SOAP API implementations
- ✅ Docker containerization for each service
- ✅ Load balancing and service discovery
- ✅ Comprehensive technical documentation

## 🚀 Running the Project
```bash
# Using Docker Compose
docker-compose up -d

# Services will be available at:
# Gateway: http://localhost:8080
# Auth: http://localhost:8081
# Students: http://localhost:8082
# Grades: http://localhost:8083
# Billing: http://localhost:8084
```

## 📚 Learning Outcomes

- Microservices architecture design patterns
- Polyglot programming (Java, JavaScript, Python, C#)
- API Gateway implementation
- Service orchestration and communication
- Docker containerization strategies
- JWT authentication in distributed systems

## 👥 Team

Academic project completed in a team of 3 with clear role distribution:
- Architecture design
- Service development
- Integration and testing
- Documentation and presentation

## 👤 Authors

**Mili Yassine**
- Portfolio: [yassinemili.me](https://yassinemili.me)
- LinkedIn: [mili-yassine](https://linkedin.com/in/mili-yassine)
  
**Battikh Youssef**
- Portfolio:
- LinkedIn: [ysf-battikh](https://www.linkedin.com/in/ysf-battikh)
  
**Ksouri Fahmi**
- Portfolio: 
- LinkedIn:  
## 📄 License
Academic Project - MIT License
