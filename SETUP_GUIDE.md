# 🚀 University Management System - Setup & Execution Guide

## Project Overview

This is a **Service-Oriented Architecture (SOA)** project with 5 microservices:

```
API Gateway (Port 8080)
    ├─ Student Service (Port 8082) ✅ READY
    ├─ Auth Service (Port 8081)
    ├─ Courses Service (Port 8083)
    ├─ Grades Service (Port 8084)
    └─ Billing Service (Port 8085)
```

**Current Status:** ✅ Student Service is fully implemented and operational

---

## 📋 Prerequisites

Before running the project, ensure you have:

### Required Software
- **Docker** (v20.10+) - [Install Docker](https://docs.docker.com/get-docker/)
- **Docker Compose** (v2.0+) - Included with Docker Desktop
- **Git** - For cloning the repository
- **Port availability** - Ensure ports 8082 (student service), 5432 (PostgreSQL) are available

### Optional (for local development without Docker)
- **Node.js** (v18+) - [Install Node.js](https://nodejs.org/)
- **npm** (v9+) - Included with Node.js
- **PostgreSQL** (v14+) - [Install PostgreSQL](https://www.postgresql.org/download/)

---

## 🎯 Quick Start (Docker - Recommended)

### Step 1: Clone the Repository
```bash
# Clone the project
git clone https://github.com/yassine-mili/University-Management-System.git
cd University-Management-System
```

### Step 2: Navigate to Project Directory
```bash
cd University-Management-System
```

### Step 3: Start All Services
```bash
# Navigate to docker directory
cd docker

# Start services (database + student service)
docker compose up -d

# Verify services are running
docker compose ps
```

**Expected output:**
```
NAME                       IMAGE                    STATUS
docker-student_db-1        postgres:14-alpine       Up (healthy)
docker-student_service-1   docker-student_service   Up
```

### Step 4: Access the Service
- **API Base**: http://localhost:8082/api/v1/
- **Swagger UI**: http://localhost:8082/api-docs
- **Health Check**: http://localhost:8082/

---

## 📚 API Quick Reference

### Test the API

#### 1. Create a Student
```bash
curl -X POST http://localhost:8082/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Dupont",
    "prenom": "Jean",
    "email": "jean.dupont@example.com",
    "numero_etudiant": "STU001",
    "niveau": "L1",
    "actif": true
  }'
```

**Response (201 Created):**
```json
{
  "message": "Étudiant créé avec succès",
  "data": {
    "id": 1,
    "numero_etudiant": "STU001",
    "email": "jean.dupont@example.com",
    "nom": "Dupont",
    "prenom": "Jean",
    "niveau": "L1",
    "est_actif": true,
    "createdAt": "2025-12-06T17:12:17.348Z"
  }
}
```

#### 2. Get All Students
```bash
curl http://localhost:8082/api/v1/students?page=1&limit=10
```

#### 3. Get Single Student
```bash
# By ID
curl http://localhost:8082/api/v1/students/1

# By email
curl http://localhost:8082/api/v1/students/jean.dupont@example.com

# By numero_etudiant
curl http://localhost:8082/api/v1/students/STU001
```

#### 4. Update Student
```bash
curl -X PUT http://localhost:8082/api/v1/students/1 \
  -H "Content-Type: application/json" \
  -d '{"niveau": "L2"}'
```

#### 5. Delete Student
```bash
curl -X DELETE http://localhost:8082/api/v1/students/1
```

---

## 🎨 Using Swagger UI (Interactive Documentation)

### Access Swagger UI
1. Open your browser and navigate to: **http://localhost:8082/api-docs**
2. You'll see an interactive API documentation interface

### Test Endpoints in Swagger
1. Click on any endpoint to expand it
2. Click the **"Try it out"** button
3. Fill in the request body/parameters
4. Click **"Execute"** to make the request
5. View the response in real-time

### Features
- ✓ Interactive API testing
- ✓ Request/response examples
- ✓ Schema validation
- ✓ Error code documentation
- ✓ OpenAPI 3.0 specification

---

## 🔧 Docker Management

### View Logs
```bash
# View student service logs
docker compose logs student_service

# View database logs
docker compose logs student_db

# View real-time logs (-f flag)
docker compose logs -f student_service
```

### Stop Services
```bash
# Stop all services (but keep data)
docker compose stop

# Stop specific service
docker compose stop student_service
```

### Restart Services
```bash
# Restart all services
docker compose restart

# Restart specific service
docker compose restart student_service
```

### Remove Services
```bash
# Stop and remove containers (keep volumes/data)
docker compose down

# Stop and remove everything including volumes/data
docker compose down -v
```

---

## 📦 Local Development (Without Docker)

### Prerequisites
- Node.js 18+ installed
- PostgreSQL 14+ running and accessible
- npm or yarn package manager

### Step 1: Install Dependencies
```bash
cd services/student-service

# Install Node.js dependencies
npm install
```

### Step 2: Configure Database

Create a `.env` file in `services/student-service/`:
```
DATABASE_URL="postgresql://user:password@localhost:5432/students_db"
POSTGRES_URI="postgresql://user:password@localhost:5432/students_db"
PORT=8082
NODE_ENV=development
```

### Step 3: Create PostgreSQL Database
```bash
# Connect to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE students_db;

# Exit psql
\q
```

### Step 4: Run Database Migrations
```bash
cd services/student-service

# Generate Prisma client
npx prisma generate

# Run migrations
npx prisma migrate dev --name init_student_table
```

### Step 5: Start the Service
```bash
# Development mode with auto-reload
npm run dev

# Or production mode
npm start

# Or directly with Node
node src/server.js
```

Service will be available at: http://localhost:8082

---

## 🛠️ Configuration Files

### Key Configuration Files

#### 1. `.env` - Environment Variables
```
DATABASE_URL="postgresql://user:password@student_db:5432/students_db"
PORT=8082
NODE_ENV=development
```

#### 2. `docker-compose.yml` - Docker Services
- Student Service (Node.js)
- PostgreSQL Database
- Port mappings (8082 → 8082)
- Volume mounts
- Health checks

#### 3. `prisma/schema.prisma` - Database Schema
```prisma
model Student {
  id              Int     @id @default(autoincrement())
  numero_etudiant String  @unique
  email           String  @unique
  nom             String
  prenom          String
  niveau          String  // L1, L2, L3, M1, M2
  createdAt       DateTime @default(now())
  updatedAt       DateTime @updatedAt
}
```

#### 4. `src/server.js` - Express Application
- Swagger UI integration
- Route definitions
- Middleware configuration
- CORS setup

---

## 📊 API Endpoints

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| POST | `/api/v1/students` | Create student | 201 |
| GET | `/api/v1/students` | List all students (paginated) | 200 |
| GET | `/api/v1/students/:id` | Get single student | 200 |
| PUT | `/api/v1/students/:id` | Update student | 200 |
| DELETE | `/api/v1/students/:id` | Delete student | 200 |
| GET | `/` | Health check | 200 |
| GET | `/api-docs` | Swagger UI documentation | 200 |

---

## 🐛 Troubleshooting

### Issue: Port 8082 Already in Use
```bash
# Find process using port 8082
lsof -i :8082

# Kill the process (on macOS/Linux)
kill -9 <PID>

# On Windows
netstat -ano | findstr :8082
taskkill /PID <PID> /F
```

### Issue: Database Connection Failed
```bash
# Check if PostgreSQL container is running
docker compose ps

# View database logs
docker compose logs student_db

# Restart database
docker compose restart student_db
```

### Issue: Swagger UI Not Loading
```bash
# Rebuild containers
docker compose build --no-cache student_service

# Restart service
docker compose restart student_service
```

### Issue: npm Install Fails
```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm install
```

### Issue: Migration Errors
```bash
# Reset database (CAUTION: deletes all data)
npx prisma migrate reset

# Or check migration status
npx prisma migrate status
```

---

## 📁 Project Structure

```
University-Management-System/
├── README.md                          # Project overview
├── STUDENT_SERVICE_COMPLETE.md        # Completion status
├── docker/
│   ├── docker-compose.yml             # Docker services config
│   └── ...
└── services/
    ├── student-service/               # ✅ READY
    │   ├── Dockerfile                 # Container definition
    │   ├── package.json               # Dependencies
    │   ├── .env                       # Configuration
    │   ├── prisma/
    │   │   ├── schema.prisma          # Database schema
    │   │   └── migrations/            # Migration history
    │   ├── src/
    │   │   ├── server.js              # Main app
    │   │   ├── config/
    │   │   │   ├── swagger.config.js  # OpenAPI spec
    │   │   │   └── prisma.config.js   # DB connection
    │   │   ├── controllers/
    │   │   │   └── student.controller.js
    │   │   └── routes/
    │   │       └── student.routes.js
    │   └── docs/
    │       ├── SWAGGER_DOCUMENTATION.md
    │       ├── API_DOCUMENTATION.md
    │       └── IMPLEMENTATION_SUMMARY.md
    │
    ├── auth-service/                  # [In development]
    ├── courses-service/               # [In development]
    ├── grades-service/                # [In development]
    └── billing-service/               # [In development]
```

---

## ✅ Verification Checklist

After starting the services, verify everything is working:

- [ ] Docker containers are running: `docker compose ps`
- [ ] Health check responds: `curl http://localhost:8082/`
- [ ] API responds: `curl http://localhost:8082/api/v1/students`
- [ ] Swagger UI loads: Open http://localhost:8082/api-docs in browser
- [ ] Can create student: POST request with valid JSON payload
- [ ] Can retrieve students: GET all students with pagination
- [ ] Database connection is healthy: Check logs

---

## 🔗 Useful Resources

### Documentation Files
- **Setup Guide**: `/SETUP_GUIDE.md` (this file)
- **API Reference**: `services/student-service/SWAGGER_DOCUMENTATION.md`
- **Implementation Details**: `services/student-service/IMPLEMENTATION_SUMMARY.md`
- **Completion Status**: `STUDENT_SERVICE_COMPLETE.md`

### External Links
- [Docker Documentation](https://docs.docker.com/)
- [Express.js Guide](https://expressjs.com/)
- [Prisma ORM Docs](https://www.prisma.io/docs/)
- [OpenAPI/Swagger Spec](https://swagger.io/specification/)

---

## 🚀 Next Steps

1. **Run the Student Service** (already ready)
   ```bash
   cd docker
   docker compose up -d
   ```

2. **Test the API** using Swagger UI or cURL

3. **Develop Other Services** (Auth, Courses, Grades, Billing)

4. **Implement API Gateway** for service routing

5. **Deploy to Production** using Kubernetes or Cloud Platform

---

## 💡 Common Commands

```bash
# Start services
docker compose up -d

# Stop services
docker compose stop

# View logs
docker compose logs -f student_service

# Restart service
docker compose restart student_service

# View running containers
docker compose ps

# Execute command in container
docker compose exec student_service npm test

# Clean up
docker compose down -v
```

---

## 📞 Support

For issues or questions:
1. Check the troubleshooting section above
2. Review service logs: `docker compose logs`
3. Check API documentation: http://localhost:8082/api-docs
4. Refer to implementation documents in `services/student-service/`

---

**Last Updated:** December 6, 2025
**Status:** ✅ Student Service Ready for Production
**Version:** 1.0.0
