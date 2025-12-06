# 🚀 Quick Start Cheat Sheet

## ⚡ 30-Second Startup

```bash
# 1. Navigate to docker directory
cd University-Management-System/docker

# 2. Start everything
docker compose up -d

# 3. Verify it's running
docker compose ps

# 4. Open in browser
# API: http://localhost:8082/api/v1/
# Docs: http://localhost:8082/api-docs
```

---

## 🔗 Essential URLs

| What | URL |
|------|-----|
| **API Endpoint** | http://localhost:8082/api/v1/ |
| **API Docs** | http://localhost:8082/api-docs |
| **Health Check** | http://localhost:8082/ |
| **Database** | postgresql://localhost:5432/students_db |

---

## 📋 Common cURL Commands

### Create Student
```bash
curl -X POST http://localhost:8082/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Dupont",
    "prenom": "Jean",
    "email": "jean@example.com",
    "numero_etudiant": "STU001",
    "niveau": "L1",
    "actif": true
  }'
```

### Get All Students
```bash
curl http://localhost:8082/api/v1/students
```

### Get Student by ID
```bash
curl http://localhost:8082/api/v1/students/1
```

### Update Student
```bash
curl -X PUT http://localhost:8082/api/v1/students/1 \
  -H "Content-Type: application/json" \
  -d '{"niveau": "L2"}'
```

### Delete Student
```bash
curl -X DELETE http://localhost:8082/api/v1/students/1
```

---

## 🐳 Docker Commands

```bash
# Start services
docker compose up -d

# Stop services
docker compose stop

# Restart service
docker compose restart student_service

# View logs
docker compose logs -f student_service

# View status
docker compose ps

# Remove everything
docker compose down -v
```

---

## ✅ Verification Steps

```bash
# 1. Check services are running
docker compose ps
# Should show: student_service (Up) and student_db (Up, healthy)

# 2. Test API
curl http://localhost:8082/

# 3. Test database connection
curl http://localhost:8082/api/v1/students

# 4. View Swagger docs
open http://localhost:8082/api-docs
```

---

## 🎯 API Response Codes

| Code | Meaning |
|------|---------|
| 200 | ✅ Success (GET, PUT, DELETE) |
| 201 | ✅ Created (POST) |
| 400 | ❌ Bad Request (validation error) |
| 404 | ❌ Not Found |
| 409 | ❌ Conflict (duplicate) |
| 500 | ❌ Server Error |

---

## 📊 Example Student Object

```json
{
  "id": 1,
  "numero_etudiant": "STU001",
  "email": "jean@example.com",
  "nom": "Dupont",
  "prenom": "Jean",
  "niveau": "L1",
  "date_naissance": null,
  "filiere": null,
  "date_inscription": "2025-12-06T17:12:17.348Z",
  "est_actif": true,
  "createdAt": "2025-12-06T17:12:17.348Z",
  "updatedAt": "2025-12-06T17:12:17.348Z"
}
```

---

## 🎓 Valid Levels (niveau)

```
L1 - First year Bachelor
L2 - Second year Bachelor
L3 - Third year Bachelor
M1 - First year Master
M2 - Second year Master
```

---

## 🆘 Quick Fixes

### Service won't start
```bash
docker compose down -v
docker compose build --no-cache student_service
docker compose up -d
```

### Port already in use
```bash
# Kill process on port 8082
lsof -i :8082 | grep LISTEN | awk '{print $2}' | xargs kill -9
docker compose up -d
```

### Database error
```bash
docker compose restart student_db
docker compose logs student_db
```

### Clear everything and start fresh
```bash
docker compose down -v
docker compose up -d
```

---

## 📝 Required Fields for Student Creation

- ✅ **nom** (string) - Last name
- ✅ **prenom** (string) - First name
- ✅ **email** (string) - Must be unique
- ✅ **numero_etudiant** (string) - Must be unique
- ✅ **niveau** (string) - L1, L2, L3, M1, or M2
- ⚪ **actif** (boolean) - Optional, defaults to true

---

## 📁 File Locations

```
docker-compose.yml     → ./docker/
.env                   → ./services/student-service/
Swagger UI             → http://localhost:8082/api-docs
API                    → http://localhost:8082/api/v1/
Database               → PostgreSQL on 5432
```

---

## 🎯 Next After Startup

1. ✅ Check health: `curl http://localhost:8082/`
2. ✅ Open Swagger: http://localhost:8082/api-docs
3. ✅ Try creating a student in Swagger UI
4. ✅ Test all CRUD operations
5. ✅ Review logs if issues: `docker compose logs`

---

**Start Command:**
```bash
cd docker && docker compose up -d
```

**That's it! Your Student Service is running! 🎉**
