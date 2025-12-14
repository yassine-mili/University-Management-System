# Testing & Viewing Database - Grades & Billing Services

## Overview
This guide shows how to test both services and view their database data.

---

## Part 1: View Database with pgAdmin (GUI)

### Install pgAdmin (Optional but Easy)

pgAdmin is a graphical tool to manage PostgreSQL databases.

**Option A: During PostgreSQL Installation**
- pgAdmin is installed automatically
- Access at: http://localhost:5050

**Option B: Standalone Installation**
```powershell
# Using Scoop
scoop install pgadmin4

# Or download from: https://www.pgadmin.org/download/
```

### Connect to Database in pgAdmin

1. Open http://localhost:5050
2. Login (default: pgadmin4@pgadmin.org / admin)
3. Click **Add New Server**
4. Fill in:
   - **Name:** PostgreSQL Local
   - **Host:** localhost
   - **Port:** 5432
   - **Username:** postgres
   - **Password:** (your PostgreSQL password)
5. Click **Save**
6. Expand **Databases** to see:
   - grades_db
   - billing_db
   - students_db
   - auth_db

---

## Part 2: View Database with Command Line (psql)

### Connect to Grades Database

```powershell
# Connect to grades_db
psql -U user -h localhost -d grades_db

# List tables
\dt

# View all grades
SELECT * FROM grades;

# View specific student's grades
SELECT * FROM grades WHERE student_id = 'STU001';

# View grade statistics
SELECT 
    student_id,
    COUNT(*) as total_grades,
    AVG(grade) as average_grade,
    MAX(grade) as highest_grade,
    MIN(grade) as lowest_grade
FROM grades
GROUP BY student_id;

# Exit
\q
```

### Connect to Billing Database

```powershell
# Connect to billing_db
psql -U user -h localhost -d billing_db

# List tables
\dt

# View all invoices
SELECT * FROM invoices;

# View all students
SELECT * FROM students;

# View invoices with student info
SELECT i.*, s.first_name, s.last_name 
FROM invoices i
JOIN students s ON i.student_id = s.id;

# Exit
\q
```

---

## Part 3: Test Grades Service

### Start the Service

```powershell
cd c:\Users\Lenovo\Desktop\University-Management-System\services\grades-service
python -m uvicorn app.main:app --host 0.0.0.0 --port 8084 --reload
```

### Test with Swagger UI (Easiest)

1. Open: http://localhost:8084/docs
2. You'll see all API endpoints
3. Click on any endpoint to test it

### Test with curl

```powershell
# 1. Check health
curl http://localhost:8084/health

# 2. Create a grade
curl -X POST http://localhost:8084/grades/ `
  -H "Content-Type: application/json" `
  -d '{
    "student_id": "STU001",
    "course_id": "MATH101",
    "grade": 15.5,
    "weight": 1.0,
    "exam_date": "2024-12-09"
  }'

# 3. Get all grades for a student
curl http://localhost:8084/grades/student/STU001

# 4. Get transcript
curl http://localhost:8084/grades/student/STU001/transcript

# 5. Get statistics
curl http://localhost:8084/grades/student/STU001/statistics

# 6. Delete a grade (replace ID)
curl -X DELETE http://localhost:8084/grades/1
```

### Test with Postman

1. Download Postman: https://www.postman.com/downloads/
2. Create requests:

**Create Grade:**
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

**Get Grades:**
```
GET http://localhost:8084/grades/student/STU001
```

**Get Transcript:**
```
GET http://localhost:8084/grades/student/STU001/transcript
```

**Get Statistics:**
```
GET http://localhost:8084/grades/student/STU001/statistics
```

---

## Part 4: Test Billing Service

### Start the Service

```powershell
cd c:\Users\Lenovo\Desktop\University-Management-System\services\billing-service
dotnet restore
dotnet run
```

Service runs on: http://localhost:5000

### Test with curl

```powershell
# 1. Check service
curl http://localhost:5000/

# 2. Create an invoice
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

# 3. Get invoice
curl http://localhost:5000/api/invoices/INV-001
```

### Test with Postman

**Create Invoice:**
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

**Get Invoice:**
```
GET http://localhost:5000/api/invoices/INV-001
```

---

## Part 5: Complete Test Workflow

### Step 1: Start Both Services

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

### Step 2: Create Test Data

**Add Grades:**
```powershell
# Grade 1
curl -X POST http://localhost:8084/grades/ `
  -H "Content-Type: application/json" `
  -d '{"student_id":"STU001","course_id":"MATH101","grade":18,"weight":1,"exam_date":"2024-12-01"}'

# Grade 2
curl -X POST http://localhost:8084/grades/ `
  -H "Content-Type: application/json" `
  -d '{"student_id":"STU001","course_id":"ENG102","grade":16,"weight":1,"exam_date":"2024-12-02"}'

# Grade 3
curl -X POST http://localhost:8084/grades/ `
  -H "Content-Type: application/json" `
  -d '{"student_id":"STU001","course_id":"PHY103","grade":14,"weight":1,"exam_date":"2024-12-03"}'
```

**Add Invoices:**
```powershell
# Invoice 1
curl -X POST http://localhost:5000/api/invoices `
  -H "Content-Type: application/json" `
  -d '{"invoiceNumber":"INV-2024-001","amount":1500,"currency":"TND","universityId":"UNI001","firstName":"John","lastName":"Doe"}'

# Invoice 2
curl -X POST http://localhost:5000/api/invoices `
  -H "Content-Type: application/json" `
  -d '{"invoiceNumber":"INV-2024-002","amount":2000,"currency":"TND","universityId":"UNI002","firstName":"Jane","lastName":"Smith"}'
```

### Step 3: View Data in Database

```powershell
# View Grades
psql -U user -h localhost -d grades_db -c "SELECT * FROM grades;"

# View Invoices
psql -U user -h localhost -d billing_db -c "SELECT * FROM invoices;"
```

### Step 4: Get API Responses

```powershell
# Get student transcript
curl http://localhost:8084/grades/student/STU001/transcript

# Get student statistics
curl http://localhost:8084/grades/student/STU001/statistics

# Get invoice
curl http://localhost:5000/api/invoices/INV-2024-001
```

---

## Part 6: View Data with Python Script

Create a test script to view all data:

**File: `test_databases.py`**

```python
import psycopg2
from tabulate import tabulate

# Connect to Grades DB
grades_conn = psycopg2.connect(
    host="localhost",
    port=5432,
    database="grades_db",
    user="user",
    password="password"
)

# Connect to Billing DB
billing_conn = psycopg2.connect(
    host="localhost",
    port=5432,
    database="billing_db",
    user="user",
    password="password"
)

print("=" * 80)
print("GRADES DATABASE")
print("=" * 80)

# View Grades
grades_cursor = grades_conn.cursor()
grades_cursor.execute("SELECT id, student_id, course_id, grade, weight, exam_date FROM grades ORDER BY id")
grades_data = grades_cursor.fetchall()
print("\nGrades Table:")
print(tabulate(grades_data, headers=["ID", "Student ID", "Course", "Grade", "Weight", "Date"], tablefmt="grid"))

grades_cursor.close()
grades_conn.close()

print("\n" + "=" * 80)
print("BILLING DATABASE")
print("=" * 80)

# View Students
billing_cursor = billing_conn.cursor()
billing_cursor.execute("SELECT id, university_id, first_name, last_name FROM students ORDER BY id")
students_data = billing_cursor.fetchall()
print("\nStudents Table:")
print(tabulate(students_data, headers=["ID", "University ID", "First Name", "Last Name"], tablefmt="grid"))

# View Invoices
billing_cursor.execute("SELECT id, invoice_number, amount, currency, student_id, created_at FROM invoices ORDER BY id")
invoices_data = billing_cursor.fetchall()
print("\nInvoices Table:")
print(tabulate(invoices_data, headers=["ID", "Invoice #", "Amount", "Currency", "Student ID", "Created"], tablefmt="grid"))

billing_cursor.close()
billing_conn.close()

print("\n" + "=" * 80)
```

Run it:
```powershell
pip install psycopg2-binary tabulate
python test_databases.py
```

---

## Part 7: Quick Reference - Common Commands

### psql Commands

```powershell
# Connect to database
psql -U user -h localhost -d grades_db

# List tables
\dt

# Describe table
\d grades

# View all data
SELECT * FROM grades;

# View with formatting
SELECT * FROM grades \gx

# Count rows
SELECT COUNT(*) FROM grades;

# Export to CSV
\COPY grades TO 'grades.csv' CSV HEADER

# Exit
\q
```

### Useful Queries

```sql
-- Count grades by student
SELECT student_id, COUNT(*) FROM grades GROUP BY student_id;

-- Average grade by student
SELECT student_id, AVG(grade) FROM grades GROUP BY student_id;

-- Grades above 15
SELECT * FROM grades WHERE grade > 15;

-- Recent grades
SELECT * FROM grades ORDER BY exam_date DESC LIMIT 10;

-- Invoices by student
SELECT * FROM invoices WHERE student_id = 1;

-- Total invoice amount
SELECT SUM(amount) FROM invoices;
```

---

## Troubleshooting

### Cannot Connect to Database
```powershell
# Check PostgreSQL is running
Get-Service postgresql-x64-14

# Check port
netstat -ano | findstr :5432
```

### Service Won't Start
```powershell
# Check logs
# For Grades: Look at console output
# For Billing: Check appsettings.json connection string
```

### No Data in Database
1. Ensure service is running
2. Make API calls to create data
3. Check database connection string
4. Verify user permissions

---

## Summary

| Tool | Purpose | Command |
|------|---------|---------|
| **Swagger UI** | Test Grades API | http://localhost:8084/docs |
| **pgAdmin** | GUI Database Manager | http://localhost:5050 |
| **psql** | Command Line DB Access | `psql -U user -h localhost -d grades_db` |
| **curl** | Test APIs | `curl http://localhost:8084/health` |
| **Postman** | API Testing Tool | Download from postman.com |
| **Python Script** | View all data | `python test_databases.py` |

