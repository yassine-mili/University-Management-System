# 📚 Student Service

A comprehensive RESTful API microservice for managing student records using Node.js, Express, PostgreSQL, and Prisma ORM.

## ✅ Implementation Status

All three core functionalities are **FULLY IMPLEMENTED** and **TESTED**:

- ✅ **CRUD Operations** - Complete Create, Read (all & single), Update, Delete functionality
- ✅ **RESTful API** - Proper HTTP methods, status codes, JSON responses, and API versioning
- ✅ **PostgreSQL Persistence** - Full data persistence with Prisma ORM and migrations

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Node.js 18+ (if running locally)

### Start the Service

```bash
cd docker/
docker compose up -d student_service student_db
```

Service will be available at: `http://localhost:3000`

### Stop the Service

```bash
docker compose down
```

## 📖 API Documentation

Full API documentation is available in [`API_DOCUMENTATION.md`](./API_DOCUMENTATION.md)

### Quick API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/students` | Create a new student |
| GET | `/api/v1/students` | List all students (with pagination) |
| GET | `/api/v1/students/:id` | Get single student by ID, numero_etudiant, or email |
| PUT | `/api/v1/students/:id` | Update student information |
| DELETE | `/api/v1/students/:id` | Delete a student |

## 📝 Example Usage

### Create a Student
```bash
curl -X POST http://localhost:3000/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "numero_etudiant": "STU001",
    "email": "alice@university.edu",
    "nom": "Martin",
    "prenom": "Alice",
    "niveau": "L1",
    "filiere": "Informatique"
  }'
```

### List All Students
```bash
curl http://localhost:3000/api/v1/students
```

### Get Single Student
```bash
curl http://localhost:3000/api/v1/students/STU001
```

### Update Student
```bash
curl -X PUT http://localhost:3000/api/v1/students/1 \
  -H "Content-Type: application/json" \
  -d '{"niveau": "L2"}'
```

### Delete Student
```bash
curl -X DELETE http://localhost:3000/api/v1/students/1
```

## 🧪 Testing

### Run Full Test Suite
```bash
bash test-api.sh
```

The test suite includes:
- ✓ Creating multiple students
- ✓ Reading all and individual students
- ✓ Filtering and pagination
- ✓ Updating students
- ✓ Deleting students
- ✓ Error handling (validation, duplicates, not found)
- ✓ Status code verification

## 📊 Database

### PostgreSQL Connection
- **Host:** student_db (Docker service name)
- **Port:** 5432
- **Database:** students_db
- **User:** user
- **Password:** password

### View Database with Prisma Studio
```bash
cd docker/
docker compose run --rm -p 5555:5555 student_service npx prisma studio --browser none
```

Then open: `http://localhost:5555`

## 🏗️ Architecture

```
Student Service (Node.js)
    ↓
Express Routes (/api/v1/students)
    ↓
Student Controller (Business Logic)
    ↓
Prisma ORM (Data Access)
    ↓
PostgreSQL Database
```

## 📁 Project Structure

```
student-service/
├── src/
│   ├── server.js                    # Express app & routes
│   ├── config/
│   │   └── prisma.config.js        # Database configuration
│   ├── routes/
│   │   └── student.routes.js       # API endpoints
│   └── controllers/
│       └── student.controller.js   # Business logic
├── prisma/
│   ├── schema.prisma               # Data model
│   └── migrations/                 # Database migrations
├── .env                            # Environment variables
├── package.json                    # Dependencies
├── Dockerfile                      # Container config
├── API_DOCUMENTATION.md            # Detailed API docs
├── IMPLEMENTATION_SUMMARY.md       # Implementation details
└── test-api.sh                     # API test suite
```

## 🔧 Configuration

### Environment Variables (`.env`)
```env
DATABASE_URL="postgresql://user:password@student_db:5432/students_db"
PORT=3000
NODE_ENV=development
```

### Docker Configuration
- **Image:** node:18-alpine
- **Port:** 3000
- **Database Port:** 5432
- **Volume:** student_db_data (persistent)

## 🎯 Features

### CRUD Operations
- ✅ **Create** - Add new student records with validation
- ✅ **Read** - Retrieve all students (with pagination) or single student
- ✅ **Update** - Modify student information (partial updates)
- ✅ **Delete** - Remove student records

### API Features
- ✅ RESTful endpoints with proper HTTP methods
- ✅ Pagination support (page, limit)
- ✅ Filtering (by nivel, actif status)
- ✅ Sorting (by creation date, descending)
- ✅ CORS support
- ✅ Input validation
- ✅ Error handling
- ✅ Health check endpoint

### Data Management
- ✅ Unique constraints (numero_etudiant, email)
- ✅ Automatic timestamps (createdAt, updatedAt)
- ✅ Optional fields (filiere, date_naissance)
- ✅ Boolean status tracking (est_actif)
- ✅ Data persistence with PostgreSQL

## 🐛 Error Handling

| Status | Scenario |
|--------|----------|
| 201 | Student successfully created |
| 200 | Operation successful |
| 400 | Validation error (missing/invalid fields) |
| 404 | Student not found |
| 409 | Conflict (duplicate email/numero_etudiant) |
| 500 | Server error |

## 📦 Dependencies

```json
{
  "@prisma/client": "^6.19.0",
  "cors": "^2.8.5",
  "express": "^4.18.2"
}
```

### Dev Dependencies
```json
{
  "prisma": "^6.19.0"
}
```

## 🔐 Data Model

### Student Table
```sql
CREATE TABLE "students" (
    id              SERIAL PRIMARY KEY,
    numero_etudiant TEXT UNIQUE NOT NULL,
    email           TEXT UNIQUE NOT NULL,
    nom             TEXT NOT NULL,
    prenom          TEXT NOT NULL,
    niveau          TEXT NOT NULL,
    date_naissance  DATE,
    filiere         TEXT,
    date_inscription TIMESTAMP DEFAULT NOW(),
    est_actif       BOOLEAN DEFAULT true,
    createdAt       TIMESTAMP DEFAULT NOW(),
    updatedAt       TIMESTAMP NOT NULL
);
```

## 📚 Documentation

- **Full API Docs:** [`API_DOCUMENTATION.md`](./API_DOCUMENTATION.md)
- **Implementation Details:** [`IMPLEMENTATION_SUMMARY.md`](./IMPLEMENTATION_SUMMARY.md)
- **This README:** [`README.md`](./README.md)

## 🚀 Deployment

### Using Docker Compose
```bash
docker compose up -d
```

### Manual Deployment
```bash
# Install dependencies
npm install

# Run migrations
npx prisma migrate deploy

# Start server
npm start
```

## 📞 Support

For issues or questions about the API, refer to:
1. `API_DOCUMENTATION.md` - Complete endpoint documentation
2. `IMPLEMENTATION_SUMMARY.md` - Architecture and implementation details
3. `test-api.sh` - Usage examples

## ✨ Quality Metrics

- ✅ Error handling: Comprehensive
- ✅ Input validation: Complete
- ✅ API documentation: Detailed
- ✅ Code organization: Modular
- ✅ Testing: Full test suite
- ✅ Database: Persistent storage
- ✅ Docker ready: Container optimized

## 📝 Notes

- The service uses PostgreSQL for data persistence
- Prisma ORM handles all database operations
- All endpoints return JSON responses
- API versioning: `/api/v1`
- CORS enabled for cross-origin requests
- Health check available at `GET /`

## 🔗 Related Services

This service is part of the University Management System SOA architecture:
- API Gateway
- Authentication Service
- Grades Service
- Billing Service

---

**Version:** 1.0.0  
**Last Updated:** December 6, 2025  
**Status:** ✅ Production Ready
