# 📚 Student Service API Documentation

## Overview
RESTful API for managing student records with full CRUD operations, PostgreSQL persistence, and Prisma ORM.

**Base URL:** `http://localhost:3000/api/v1`
**Database:** PostgreSQL
**ORM:** Prisma

---

## Health Check

### Endpoint
```
GET /
```

### Response (200 OK)
```json
{
  "message": "✅ Service Étudiants (Node.js/REST) est opérationnel",
  "status": "running",
  "database": "PostgreSQL + Prisma",
  "version": "1.0.0"
}
```

---

## Student Endpoints

### 1. CREATE Student
Create a new student record in the database.

**Endpoint:**
```
POST /students
```

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "numero_etudiant": "STU001",        // Required, Unique string
  "email": "alice@university.edu",    // Required, Unique email
  "nom": "Martin",                    // Required, Last name
  "prenom": "Alice",                  // Required, First name
  "niveau": "L1",                     // Required, e.g. L1, L2, L3, M1, M2
  "filiere": "Informatique",          // Optional, Field of study
  "date_naissance": "2002-05-15",     // Optional, ISO date format
  "est_actif": true                   // Optional, Default: true
}
```

**Response (201 Created):**
```json
{
  "message": "Étudiant créé avec succès",
  "data": {
    "id": 1,
    "numero_etudiant": "STU001",
    "email": "alice@university.edu",
    "nom": "Martin",
    "prenom": "Alice",
    "niveau": "L1",
    "filiere": "Informatique",
    "date_naissance": "2002-05-15T00:00:00.000Z",
    "date_inscription": "2025-12-06T16:54:25.833Z",
    "est_actif": true,
    "createdAt": "2025-12-06T16:54:25.833Z",
    "updatedAt": "2025-12-06T16:54:25.833Z"
  }
}
```

**Error Responses:**

*400 Bad Request - Validation Error:*
```json
{
  "message": "Données invalides",
  "errors": [
    "email est requis et doit être une adresse email valide",
    "niveau est requis (ex: L1, L2, L3, M1, M2)"
  ]
}
```

*409 Conflict - Duplicate Unique Field:*
```json
{
  "message": "Le email existe déjà.",
  "error": "Conflict"
}
```

**cURL Example:**
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

---

### 2. READ All Students
Retrieve a list of all students with pagination and filtering.

**Endpoint:**
```
GET /students
```

**Query Parameters:**
- `page` (optional): Page number, default: 1
- `limit` (optional): Records per page, default: 10
- `actif` (optional): Filter by status (true/false)
- `niveau` (optional): Filter by level (e.g., L1, L2, M1)

**Response (200 OK):**
```json
{
  "message": "Étudiants récupérés avec succès",
  "data": [
    {
      "id": 1,
      "numero_etudiant": "STU001",
      "email": "alice@university.edu",
      "nom": "Martin",
      "prenom": "Alice",
      "niveau": "L1",
      "filiere": "Informatique",
      "date_naissance": "2002-05-15T00:00:00.000Z",
      "date_inscription": "2025-12-06T16:54:25.833Z",
      "est_actif": true,
      "createdAt": "2025-12-06T16:54:25.833Z",
      "updatedAt": "2025-12-06T16:54:25.833Z"
    }
  ],
  "pagination": {
    "total": 1,
    "page": 1,
    "limit": 10,
    "pages": 1
  }
}
```

**cURL Examples:**
```bash
# Get all students
curl http://localhost:3000/api/v1/students

# Get students with pagination
curl http://localhost:3000/api/v1/students?page=2&limit=5

# Filter by level
curl http://localhost:3000/api/v1/students?niveau=L1

# Filter by active status
curl http://localhost:3000/api/v1/students?actif=true
```

---

### 3. READ Single Student
Retrieve a specific student by ID, numero_etudiant, or email.

**Endpoint:**
```
GET /students/:id
```

**Path Parameters:**
- `id`: Can be:
  - Student ID (numeric): `1`
  - Numero etudiant: `STU001`
  - Email: `alice@university.edu`

**Response (200 OK):**
```json
{
  "message": "Étudiant récupéré avec succès",
  "data": {
    "id": 1,
    "numero_etudiant": "STU001",
    "email": "alice@university.edu",
    "nom": "Martin",
    "prenom": "Alice",
    "niveau": "L1",
    "filiere": "Informatique",
    "date_naissance": "2002-05-15T00:00:00.000Z",
    "date_inscription": "2025-12-06T16:54:25.833Z",
    "est_actif": true,
    "createdAt": "2025-12-06T16:54:25.833Z",
    "updatedAt": "2025-12-06T16:54:25.833Z"
  }
}
```

**Error Response (404 Not Found):**
```json
{
  "message": "Étudiant non trouvé.",
  "error": "Not Found"
}
```

**cURL Examples:**
```bash
# By ID
curl http://localhost:3000/api/v1/students/1

# By numero_etudiant
curl http://localhost:3000/api/v1/students/STU001

# By email
curl http://localhost:3000/api/v1/students/alice@university.edu
```

---

### 4. UPDATE Student
Update an existing student's information.

**Endpoint:**
```
PUT /students/:id
```

**Path Parameters:**
- `id`: Student ID, numero_etudiant, or email

**Request Headers:**
```
Content-Type: application/json
```

**Request Body (partial update - only include fields to update):**
```json
{
  "niveau": "L2",
  "filiere": "Génie Informatique",
  "est_actif": false
}
```

**Response (200 OK):**
```json
{
  "message": "Étudiant mis à jour avec succès",
  "data": {
    "id": 1,
    "numero_etudiant": "STU001",
    "email": "alice@university.edu",
    "nom": "Martin",
    "prenom": "Alice",
    "niveau": "L2",
    "filiere": "Génie Informatique",
    "date_naissance": "2002-05-15T00:00:00.000Z",
    "date_inscription": "2025-12-06T16:54:25.833Z",
    "est_actif": false,
    "createdAt": "2025-12-06T16:54:25.833Z",
    "updatedAt": "2025-12-06T16:54:52.319Z"
  }
}
```

**Error Responses:**

*404 Not Found:*
```json
{
  "message": "Étudiant non trouvé pour mise à jour.",
  "error": "Not Found"
}
```

*409 Conflict - Duplicate Unique Field:*
```json
{
  "message": "Le email existe déjà après mise à jour.",
  "error": "Conflict"
}
```

**cURL Example:**
```bash
curl -X PUT http://localhost:3000/api/v1/students/1 \
  -H "Content-Type: application/json" \
  -d '{
    "niveau": "L2",
    "filiere": "Génie Informatique"
  }'
```

---

### 5. DELETE Student
Delete a student record from the database.

**Endpoint:**
```
DELETE /students/:id
```

**Path Parameters:**
- `id`: Student ID, numero_etudiant, or email

**Response (200 OK):**
```json
{
  "message": "Étudiant supprimé avec succès",
  "data": {
    "id": 1
  }
}
```

**Error Response (404 Not Found):**
```json
{
  "message": "Étudiant non trouvé pour suppression.",
  "error": "Not Found"
}
```

**cURL Example:**
```bash
curl -X DELETE http://localhost:3000/api/v1/students/1
```

---

## Data Models

### Student Object
```typescript
interface Student {
  id: number;                  // Primary key, auto-generated
  numero_etudiant: string;     // Unique student number
  email: string;               // Unique email address
  nom: string;                 // Last name
  prenom: string;              // First name
  niveau: string;              // Academic level (L1-L3, M1-M2)
  date_naissance?: Date;       // Date of birth (optional)
  filiere?: string;            // Field of study (optional)
  date_inscription: Date;      // Registration date (auto-set)
  est_actif: boolean;          // Active status (default: true)
  createdAt: Date;             // Creation timestamp
  updatedAt: Date;             // Last update timestamp
}
```

---

## Status Codes

| Code | Description |
|------|-------------|
| 200  | Success (GET, PUT, DELETE) |
| 201  | Resource created (POST) |
| 400  | Bad request (validation error) |
| 404  | Resource not found |
| 409  | Conflict (duplicate unique field) |
| 500  | Internal server error |

---

## Technology Stack

- **Framework:** Node.js + Express
- **Database:** PostgreSQL
- **ORM:** Prisma
- **API Style:** REST
- **Language:** JavaScript (ES6+)

---

## Running the Service

### With Docker
```bash
docker compose up -d student_service student_db
```

### View Prisma Studio (Database GUI)
```bash
docker compose run --rm -p 5555:5555 student_service npx prisma studio --browser none
# Access at http://localhost:5555
```

---

## Troubleshooting

### Connection Errors
Ensure the PostgreSQL database is running and `DATABASE_URL` environment variable is set correctly.

### Validation Errors
Check that all required fields are provided and have correct formats:
- `numero_etudiant`: Non-empty string
- `email`: Valid email format
- `nom`: Non-empty string
- `prenom`: Non-empty string
- `niveau`: Required field

### Duplicate Key Errors
Ensure `numero_etudiant` and `email` are unique across all students.

---

## Example Workflow

```bash
# 1. Create a student
curl -X POST http://localhost:3000/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "numero_etudiant": "STU001",
    "email": "alice@university.edu",
    "nom": "Martin",
    "prenom": "Alice",
    "niveau": "L1"
  }'

# 2. List all students
curl http://localhost:3000/api/v1/students

# 3. Get one student
curl http://localhost:3000/api/v1/students/STU001

# 4. Update student level
curl -X PUT http://localhost:3000/api/v1/students/1 \
  -H "Content-Type: application/json" \
  -d '{"niveau": "L2"}'

# 5. Delete student
curl -X DELETE http://localhost:3000/api/v1/students/1
```

---

**Last Updated:** December 6, 2025
**Version:** 1.0.0
