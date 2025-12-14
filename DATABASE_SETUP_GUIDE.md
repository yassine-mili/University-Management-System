# Database Setup Guide - University Management System

## Overview

This guide explains how to set up PostgreSQL databases for the Grades Service and Billing Service.

---

## Prerequisites

- PostgreSQL 14+ installed on your system
- Python 3.8+ (for running setup scripts)
- .NET 7 SDK (for Billing Service)

---

## Step 1: Install PostgreSQL

### Option A: Download Installer (Recommended for Windows)

1. Visit: https://www.postgresql.org/download/windows/
2. Download PostgreSQL 14.x or later
3. Run the installer:
   - Set superuser password (default user: `postgres`)
   - Default port: 5432
   - Install pgAdmin (optional)

### Option B: Using Package Manager

**Windows (Scoop):**
```powershell
scoop install postgresql
```

**Windows (Chocolatey):**
```powershell
choco install postgresql
```

### Verify Installation

```powershell
psql --version
```

---

## Step 2: Start PostgreSQL Service

### Windows

```powershell
# Check if service is running
Get-Service postgresql-x64-14

# Start service
Start-Service postgresql-x64-14

# Or restart
Restart-Service postgresql-x64-14
```

### Verify Connection

```powershell
psql -U postgres -h localhost
# Enter password when prompted
# Type \q to exit
```

---

## Step 3: Create Databases

### Option A: Using Python Setup Script (Recommended)

```powershell
cd c:\Users\Lenovo\Desktop\University-Management-System

# Install psycopg2
pip install psycopg2-binary

# Run setup script
python setup_databases.py
```

**Note:** Update the `POSTGRES_PASSWORD` in the script if you used a different password during PostgreSQL installation.

### Option B: Manual Setup with psql

```powershell
# Connect to PostgreSQL
psql -U postgres -h localhost

# Create users
CREATE USER user WITH PASSWORD 'password';

# Create databases
CREATE DATABASE grades_db OWNER user;
CREATE DATABASE students_db OWNER user;
CREATE DATABASE billing_db OWNER user;

# Grant privileges
GRANT ALL PRIVILEGES ON DATABASE grades_db TO user;
GRANT ALL PRIVILEGES ON DATABASE students_db TO user;
GRANT ALL PRIVILEGES ON DATABASE billing_db TO user;

# Verify
\l

# Exit
\q
```

---

## Step 4: Verify Database Setup

```powershell
# Connect to grades_db
psql -U user -h localhost -d grades_db

# List tables (should be empty initially)
\dt

# Exit
\q
```

---

## Step 5: Update Service Configuration

### Grades Service

The connection string is already configured in `app/database.py`:
```python
DATABASE_URL: str = os.getenv(
    "DATABASE_URL",
    "postgresql://user:password@localhost:5432/grades_db"
)
```

### Billing Service

Updated in `appsettings.json`:
```json
{
  "ConnectionStrings": {
    "BillingDb": "Host=localhost;Port=5432;Database=billing_db;Username=user;Password=password"
  }
}
```

---

## Step 6: Run Services

### Grades Service (Python/FastAPI)

```powershell
cd c:\Users\Lenovo\Desktop\University-Management-System\services\grades-service

# Install dependencies
pip install -r requirements.txt

# Run service
python -m uvicorn app.main:app --host 0.0.0.0 --port 8084 --reload
```

Access Swagger UI: http://localhost:8084/docs

### Billing Service (.NET)

```powershell
cd c:\Users\Lenovo\Desktop\University-Management-System\services\billing-service

# Restore packages
dotnet restore

# Run service
dotnet run
```

Access API: http://localhost:5000

---

## Connection Strings Reference

| Service | Database | Connection String |
|---------|----------|-------------------|
| Grades | grades_db | `postgresql://user:password@localhost:5432/grades_db` |
| Students | students_db | `postgresql://user:password@localhost:5432/students_db` |
| Billing | billing_db | `postgresql://user:password@localhost:5432/billing_db` |
| Auth | auth_db | `postgresql://postgres:postgres@localhost:5432/auth_db` |

---

## Troubleshooting

### PostgreSQL Service Won't Start

```powershell
# Check service status
Get-Service postgresql-x64-14 | Select-Object Status

# Check if port is in use
netstat -ano | findstr :5432

# Kill process using port (replace PID)
taskkill /PID <PID> /F
```

### Cannot Connect to Database

1. **Verify PostgreSQL is running:**
   ```powershell
   Get-Service postgresql-x64-14
   ```

2. **Check credentials:**
   - Default user: `postgres`
   - Default password: (what you set during installation)

3. **Verify port:**
   ```powershell
   netstat -ano | findstr :5432
   ```

4. **Test connection:**
   ```powershell
   psql -U postgres -h localhost -d postgres
   ```

### psql Command Not Found

Add PostgreSQL bin directory to PATH:
```powershell
$env:Path += ";C:\Program Files\PostgreSQL\14\bin"
```

Or permanently add to system PATH environment variable.

### Database Already Exists Error

The setup script checks for existing databases. To reset:

```powershell
psql -U postgres -h localhost

# Drop and recreate
DROP DATABASE IF EXISTS grades_db;
CREATE DATABASE grades_db OWNER user;

\q
```

---

## Database Initialization

When services start, they automatically:
1. Create tables if they don't exist
2. Initialize default data
3. Run migrations (if applicable)

### Manual Table Creation (if needed)

For Grades Service, tables are created by SQLAlchemy on first run.

For Billing Service, tables are created by Entity Framework on first run.

---

## Backup and Restore

### Backup Database

```powershell
pg_dump -U user -h localhost grades_db > grades_db_backup.sql
```

### Restore Database

```powershell
psql -U user -h localhost grades_db < grades_db_backup.sql
```

---

## Environment Variables

Create a `.env` file in each service directory:

**Grades Service (.env):**
```
DATABASE_URL=postgresql://user:password@localhost:5432/grades_db
PORT=8084
ENVIRONMENT=development
```

**Billing Service (.env):**
```
ConnectionStrings__BillingDb=Host=localhost;Port=5432;Database=billing_db;Username=user;Password=password
```

---

## Next Steps

1. ✅ Install PostgreSQL
2. ✅ Create databases using setup script
3. ✅ Run Grades Service
4. ✅ Run Billing Service
5. ✅ Test APIs with Swagger UI or Postman
6. ✅ Integrate with API Gateway

---

## Support

For issues:
1. Check PostgreSQL is running: `Get-Service postgresql-x64-14`
2. Verify connection: `psql -U postgres -h localhost`
3. Check service logs for error messages
4. Review connection strings in configuration files
