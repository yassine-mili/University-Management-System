# Student Service - Swagger/OpenAPI Documentation

## Overview

The Student Service now includes comprehensive **Swagger/OpenAPI 3.0** documentation that is accessible through an interactive UI at `http://localhost:8082/api-docs`.

## Running the Service

The student service is configured to run on **port 8082** as specified in the API Gateway architecture.

```bash
# Navigate to the docker directory
cd docker/

# Start all services (database + student service)
docker compose up -d

# Service will be available at:
# - API: http://localhost:8082/api/v1/
# - Swagger UI: http://localhost:8082/api-docs
# - Health Check: http://localhost:8082/
```

## API Endpoints (Fully Documented in Swagger)

All endpoints are documented with JSDoc comments and automatically converted to OpenAPI 3.0 specification:

### Base URL
```
http://localhost:8082/api/v1
```

### 1. Create Student (POST)
```
POST /api/v1/students
Content-Type: application/json

{
  "nom": "Dupont",
  "prenom": "Jean",
  "email": "jean.dupont@example.com",
  "numero_etudiant": "STU001",
  "niveau": "L1",
  "actif": true
}
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
    "createdAt": "2025-12-06T17:12:17.348Z",
    "updatedAt": "2025-12-06T17:12:17.348Z"
  }
}
```

### 2. Get All Students (GET)
```
GET /api/v1/students?page=1&limit=10&niveau=L1&actif=true
```

**Query Parameters:**
- `page` (integer, default: 1) - Page number for pagination
- `limit` (integer, default: 10) - Records per page
- `niveau` (string) - Filter by academic level (L1, L2, L3, M1, M2)
- `actif` (boolean) - Filter by active status

**Response (200 OK):**
```json
{
  "message": "Étudiants récupérés avec succès",
  "data": [
    {
      "id": 1,
      "numero_etudiant": "STU001",
      "email": "jean.dupont@example.com",
      "nom": "Dupont",
      "prenom": "Jean",
      "niveau": "L1",
      "est_actif": true,
      "createdAt": "2025-12-06T17:12:17.348Z",
      "updatedAt": "2025-12-06T17:12:17.348Z"
    }
  ],
  "pagination": {
    "total": 5,
    "page": 1,
    "limit": 10,
    "pages": 1
  }
}
```

### 3. Get Single Student (GET)
```
GET /api/v1/students/{id}
```

Supports lookup by:
- ID: `/api/v1/students/1`
- numero_etudiant: `/api/v1/students/STU001`
- email: `/api/v1/students/jean.dupont@example.com`

**Response (200 OK):**
```json
{
  "message": "Étudiant récupéré avec succès",
  "data": {
    "id": 1,
    "numero_etudiant": "STU001",
    "email": "jean.dupont@example.com",
    "nom": "Dupont",
    "prenom": "Jean",
    "niveau": "L1",
    "est_actif": true,
    "createdAt": "2025-12-06T17:12:17.348Z",
    "updatedAt": "2025-12-06T17:12:17.348Z"
  }
}
```

### 4. Update Student (PUT)
```
PUT /api/v1/students/{id}
Content-Type: application/json

{
  "nom": "Dupont",
  "prenom": "Jean",
  "niveau": "L2"
}
```

**Response (200 OK):**
```json
{
  "message": "Étudiant modifié avec succès",
  "data": {
    "id": 1,
    "numero_etudiant": "STU001",
    "email": "jean.dupont@example.com",
    "nom": "Dupont",
    "prenom": "Jean",
    "niveau": "L2",
    "est_actif": true,
    "updatedAt": "2025-12-06T17:13:45.123Z"
  }
}
```

### 5. Delete Student (DELETE)
```
DELETE /api/v1/students/{id}
```

**Response (200 OK):**
```json
{
  "message": "Étudiant supprimé avec succès",
  "data": {
    "id": 1,
    "numero_etudiant": "STU001",
    "email": "jean.dupont@example.com"
  }
}
```

## Error Responses

### Validation Error (400)
```json
{
  "message": "Données invalides",
  "errors": [
    "Email est requis",
    "niveau doit être l'une des valeurs: L1, L2, L3, M1, M2"
  ]
}
```

### Duplicate Record (409)
```json
{
  "message": "Le numero_etudiant existe déjà.",
  "error": "Conflict"
}
```

### Not Found (404)
```json
{
  "message": "Étudiant non trouvé",
  "error": "NotFound"
}
```

### Server Error (500)
```json
{
  "message": "Une erreur est survenue lors du traitement",
  "error": "InternalError"
}
```

## Swagger UI Features

The Swagger documentation interface at `http://localhost:8082/api-docs` provides:

✅ **Interactive API Testing**
- Try-it-out feature to execute requests directly from the browser
- Automatic request/response formatting
- Real-time response visualization

✅ **Complete Endpoint Documentation**
- All CRUD operations documented with detailed descriptions
- Request and response schemas with examples
- Query parameter documentation with constraints

✅ **Schema Definitions**
- `Student` - Complete student data model
- `CreateStudentRequest` - Required/optional fields for creation
- `UpdateStudentRequest` - Updateable fields
- Error response schemas with status codes

✅ **Multiple Server Configurations**
- Development server (http://localhost:8082/api/v1)
- Alternative port configuration support

## Implementation Details

### Technology Stack
- **Framework:** Express.js 4.18.2
- **ORM:** Prisma 6.19.0
- **Database:** PostgreSQL 14
- **API Documentation:** Swagger-UI-Express 5.0.0 + Swagger-JSDoc 6.2.8
- **Node.js Version:** 18-alpine (Docker)

### File Structure
```
student-service/
├── src/
│   ├── server.js                    # Express app with Swagger integration
│   ├── config/
│   │   ├── swagger.config.js       # OpenAPI 3.0 specification generator
│   │   └── prisma.config.js        # Database connection
│   ├── controllers/
│   │   └── student.controller.js   # CRUD business logic
│   └── routes/
│       └── student.routes.js       # API endpoints with JSDoc comments
├── prisma/
│   ├── schema.prisma               # Database schema
│   └── migrations/                 # Database migration history
├── Dockerfile
└── docker-compose.yml
```

### Docker Configuration
- **Port Mapping:** 8082:8082 (host:container)
- **Database Service:** PostgreSQL on port 5432
- **Health Check:** Active with retry logic
- **Volumes:**
  - Bind mount for code development
  - Named volume for node_modules (prevents override issues)
  - PostgreSQL data persistence volume

### Key Features Implemented
✅ Full CRUD operations (Create, Read, Update, Delete)
✅ RESTful API design with proper HTTP methods and status codes
✅ PostgreSQL persistence with Prisma ORM
✅ Comprehensive input validation
✅ Error handling with appropriate status codes
✅ Pagination and filtering support
✅ API versioning (/api/v1)
✅ Swagger/OpenAPI 3.0 documentation
✅ Interactive API documentation UI
✅ Unique constraint validation (email, numero_etudiant)
✅ Database migration support
✅ Health check endpoint
✅ CORS enabled
✅ Development-friendly bind volume mounting

## API Integration with API Gateway

The student service is configured to integrate with the API Gateway architecture:

```
API Gateway (Port 8080)
    └─ Student Service (Port 8082)
    └─ Auth Service
    └─ Billing Service
    └─ Grades Service
    └─ Courses Service
```

The service provides:
- Comprehensive OpenAPI specification for gateway routing
- Consistent RESTful endpoints with versioning
- Health check endpoint for service discovery
- Full error response standards

## Testing the API

### Using cURL
```bash
# Create a student
curl -X POST http://localhost:8082/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{"nom":"Dupont","prenom":"Jean","email":"jean@example.com","numero_etudiant":"STU001","niveau":"L1","actif":true}'

# Get all students
curl http://localhost:8082/api/v1/students?page=1&limit=10

# Get specific student
curl http://localhost:8082/api/v1/students/1

# Update student
curl -X PUT http://localhost:8082/api/v1/students/1 \
  -H "Content-Type: application/json" \
  -d '{"niveau":"L2"}'

# Delete student
curl -X DELETE http://localhost:8082/api/v1/students/1
```

### Using Swagger UI
1. Navigate to http://localhost:8082/api-docs
2. Click on any endpoint to expand it
3. Click "Try it out" button
4. Fill in the request body/parameters
5. Click "Execute" to test
6. View the response

## Troubleshooting

### Service not starting
```bash
# Check logs
docker compose logs student_service

# Verify database is running
docker compose ps
```

### Swagger UI not showing endpoints
```bash
# Ensure swagger-jsdoc dependencies are installed
npm install

# Restart the service
docker compose restart student_service
```

### Database connection issues
```bash
# Check DATABASE_URL in .env
# Verify PostgreSQL container is healthy
docker compose ps
```

## Notes

- The service runs in development mode with hot-reload enabled
- All timestamps are in UTC (ISO 8601 format)
- Pagination defaults to page 1, limit 10
- All required fields must be provided during creation
- Email and numero_etudiant must be globally unique
- The API uses French messages for user feedback

## Next Steps

To integrate this service with other services:
1. Configure API Gateway to route requests to port 8082
2. Update service discovery configuration
3. Implement authentication/authorization if needed
4. Add rate limiting middleware
5. Configure monitoring and logging
