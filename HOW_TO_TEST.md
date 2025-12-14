# How to Test & View Database - Quick Guide

## 🚀 Quick Start (5 minutes)

### 1. Start Both Services

**Terminal 1 - Grades Service:**
```powershell
cd c:\Users\Lenovo\Desktop\University-Management-System\services\grades-service
python -m uvicorn app.main:app --port 8084
```

**Terminal 2 - Billing Service:**
```powershell
cd c:\Users\Lenovo\Desktop\University-Management-System\services\billing-service
dotnet run
```

### 2. Run Automated Test Script

```powershell
cd c:\Users\Lenovo\Desktop\University-Management-System
.\test_api.ps1
```

This will:
- ✅ Check if services are running
- ✅ Add test grades
- ✅ Add test invoices
- ✅ Retrieve data
- ✅ Display results

### 3. View Database Data

```powershell
python test_data.py
```

This shows:
- 📊 All grades in a table
- 💰 All invoices in a table
- 👥 All students
- 📈 Statistics by student

---

## 🌐 View in Web Browser

### Option 1: Swagger UI (Grades Service)
```
http://localhost:8084/docs
```
- Interactive API testing
- See all endpoints
- Try requests directly

### Option 2: pgAdmin (Database GUI)
```
http://localhost:5050
```
- Login: pgadmin4@pgadmin.org / admin
- Browse databases visually
- Run SQL queries
- View tables and data

---

## 💻 Command Line Tools

### View Grades Database

```powershell
# Connect
psql -U user -h localhost -d grades_db

# View all grades
SELECT * FROM grades;

# View by student
SELECT * FROM grades WHERE student_id = 'STU001';

# View statistics
SELECT student_id, COUNT(*), AVG(grade) FROM grades GROUP BY student_id;

# Exit
\q
```

### View Billing Database

```powershell
# Connect
psql -U user -h localhost -d billing_db

# View all invoices
SELECT * FROM invoices;

# View all students
SELECT * FROM students;

# View with student names
SELECT i.*, s.first_name, s.last_name FROM invoices i JOIN students s ON i.student_id = s.id;

# Exit
\q
```

---

## 📝 Manual Testing with curl

### Add a Grade

```powershell
curl -X POST http://localhost:8084/grades/ `
  -H "Content-Type: application/json" `
  -d '{
    "student_id": "STU001",
    "course_id": "MATH101",
    "grade": 15.5,
    "weight": 1.0,
    "exam_date": "2024-12-09"
  }'
```

### Get Grades for Student

```powershell
curl http://localhost:8084/grades/student/STU001
```

### Get Transcript

```powershell
curl http://localhost:8084/grades/student/STU001/transcript
```

### Get Statistics

```powershell
curl http://localhost:8084/grades/student/STU001/statistics
```

### Add Invoice

```powershell
curl -X POST http://localhost:5000/api/invoices `
  -H "Content-Type: application/json" `
  -d '{
    "invoiceNumber": "INV-001",
    "amount": 500.00,
    "currency": "TND",
    "universityId": "UNI001",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### Get Invoice

```powershell
curl http://localhost:5000/api/invoices/INV-001
```

---

## 🛠️ Using Postman (Recommended)

1. **Download Postman:** https://www.postman.com/downloads/
2. **Create a new collection**
3. **Add requests:**

### Create Grade Request
```
POST http://localhost:8084/grades/
Content-Type: application/json

{
  "student_id": "STU001",
  "course_id": "MATH101",
  "grade": 15.5,
  "weight": 1.0,
  "exam_date": "2024-12-09"
}
```

### Get Grades Request
```
GET http://localhost:8084/grades/student/STU001
```

### Get Transcript Request
```
GET http://localhost:8084/grades/student/STU001/transcript
```

### Create Invoice Request
```
POST http://localhost:5000/api/invoices
Content-Type: application/json

{
  "invoiceNumber": "INV-001",
  "amount": 500.00,
  "currency": "TND",
  "universityId": "UNI001",
  "firstName": "John",
  "lastName": "Doe"
}
```

---

## 📊 Complete Test Workflow

### Step 1: Start Services (2 terminals)
```powershell
# Terminal 1
cd services/grades-service
python -m uvicorn app.main:app --port 8084

# Terminal 2
cd services/billing-service
dotnet run
```

### Step 2: Run Automated Tests
```powershell
.\test_api.ps1
```

### Step 3: View Results
```powershell
# Option A: Python script
python test_data.py

# Option B: Web browser
# Swagger: http://localhost:8084/docs
# pgAdmin: http://localhost:5050

# Option C: Command line
psql -U user -h localhost -d grades_db
SELECT * FROM grades;
\q
```

### Step 4: Verify Data
- Check grades in database
- Check invoices in database
- Verify calculations (GPA, totals)
- Test API responses

---

## 🔍 Troubleshooting

### Services Won't Start

**Grades Service:**
```powershell
# Check Python
python --version

# Check dependencies
pip install -r requirements.txt

# Check port
netstat -ano | findstr :8084
```

**Billing Service:**
```powershell
# Check .NET
dotnet --version

# Restore packages
dotnet restore

# Check port
netstat -ano | findstr :5000
```

### Cannot Connect to Database

```powershell
# Check PostgreSQL
Get-Service postgresql-x64-14

# Check port
netstat -ano | findstr :5432

# Test connection
psql -U postgres -h localhost
```

### No Data in Database

1. Ensure services are running
2. Make API calls to create data
3. Wait a moment for data to be written
4. Refresh database view

---

## 📚 Files Reference

| File | Purpose |
|------|---------|
| `test_api.ps1` | PowerShell script to test both APIs |
| `test_data.py` | Python script to view database data |
| `TEST_DATABASE.md` | Detailed testing guide |
| `DATABASE_SETUP_GUIDE.md` | Database setup instructions |

---

## ✅ Checklist

- [ ] PostgreSQL installed and running
- [ ] Databases created (python setup_databases.py)
- [ ] Grades Service started (port 8084)
- [ ] Billing Service started (port 5000)
- [ ] Test script ran successfully (.\test_api.ps1)
- [ ] Data visible in database (python test_data.py)
- [ ] Swagger UI accessible (http://localhost:8084/docs)
- [ ] pgAdmin accessible (http://localhost:5050)

---

## 🎯 Next Steps

1. ✅ Test both services
2. ✅ View data in multiple ways
3. ✅ Verify database integrity
4. ✅ Test API endpoints
5. ✅ Integrate with API Gateway
6. ✅ Set up authentication
7. ✅ Deploy to production

---

## 📞 Quick Commands

```powershell
# View all grades
psql -U user -h localhost -d grades_db -c "SELECT * FROM grades;"

# View all invoices
psql -U user -h localhost -d billing_db -c "SELECT * FROM invoices;"

# Run test script
.\test_api.ps1

# View data
python test_data.py

# Access Swagger
start http://localhost:8084/docs

# Access pgAdmin
start http://localhost:5050
```

