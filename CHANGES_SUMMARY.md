# Database Integration - Changes Summary

## Overview

Updated Grades Service and Billing Service to use PostgreSQL database instead of in-memory or SQL Server storage.

---

## Changes Made

### 1. Grades Service (Python/FastAPI)

**File: `services/grades-service/app/database.py`**
- Updated default database URL to use PostgreSQL
- Changed from: `postgresql://user:password@localhost:5435/grades_db`
- Changed to: `postgresql://user:password@localhost:5432/grades_db`
- Port changed from 5435 to 5432 (standard PostgreSQL port)

**Status:** ✅ Ready to use with PostgreSQL

---

### 2. Billing Service (.NET)

**File: `services/billing-service/appsettings.json`**
- Changed database provider from SQL Server to PostgreSQL
- Old: `Server=billing-sql,1433;Database=BillingDb;User Id=sa;Password=Your_password123;TrustServerCertificate=True`
- New: `Host=localhost;Port=5432;Database=billing_db;Username=user;Password=password`

**File: `services/billing-service/Program.cs`**
- Changed from `UseSqlServer()` to `UseNpgsql()`
- Updated Entity Framework provider

**File: `services/billing-service/BillingService.csproj`**
- Removed: `Microsoft.EntityFrameworkCore.SqlServer` v7.0.0
- Added: `Npgsql.EntityFrameworkCore.PostgreSQL` v7.0.0

**Status:** ✅ Ready to use with PostgreSQL

---

### 3. New Setup Scripts

**File: `setup_databases.py`**
- Automated database creation script
- Creates all required databases and users
- Sets up proper permissions
- Usage: `python setup_databases.py`

**File: `POSTGRESQL_SETUP.md`**
- Step-by-step PostgreSQL installation guide
- Multiple installation options (installer, Scoop, Chocolatey)
- Troubleshooting section

**File: `DATABASE_SETUP_GUIDE.md`**
- Comprehensive database setup guide
- Database creation instructions
- Service configuration details
- Connection strings reference
- Troubleshooting and backup procedures

---

## Database Configuration

### Grades Service
- **Database:** grades_db
- **User:** user
- **Password:** password
- **Host:** localhost
- **Port:** 5432
- **Connection String:** `postgresql://user:password@localhost:5432/grades_db`

### Billing Service
- **Database:** billing_db
- **User:** user
- **Password:** password
- **Host:** localhost
- **Port:** 5432
- **Connection String:** `Host=localhost;Port=5432;Database=billing_db;Username=user;Password=password`

### Students Service (Existing)
- **Database:** students_db
- **User:** user
- **Password:** password
- **Host:** localhost
- **Port:** 5432

### Auth Service (Existing)
- **Database:** auth_db
- **User:** postgres
- **Password:** postgres
- **Host:** localhost
- **Port:** 5432

---

## Installation Steps

### 1. Install PostgreSQL
```powershell
# Download from: https://www.postgresql.org/download/windows/
# Or use Scoop:
scoop install postgresql
```

### 2. Create Databases
```powershell
cd c:\Users\Lenovo\Desktop\University-Management-System
pip install psycopg2-binary
python setup_databases.py
```

### 3. Run Grades Service
```powershell
cd services/grades-service
pip install -r requirements.txt
python -m uvicorn app.main:app --host 0.0.0.0 --port 8084 --reload
```

### 4. Run Billing Service
```powershell
cd services/billing-service
dotnet restore
dotnet run
```

---

## Verification

### Test Grades Service
```
GET http://localhost:8084/health
GET http://localhost:8084/docs (Swagger UI)
```

### Test Billing Service
```
GET http://localhost:5000/
POST http://localhost:5000/api/invoices
```

### Verify Database
```powershell
psql -U user -h localhost -d grades_db
\dt  # List tables
\q   # Exit
```

---

## Files Modified

1. ✅ `services/grades-service/app/database.py`
2. ✅ `services/billing-service/appsettings.json`
3. ✅ `services/billing-service/Program.cs`
4. ✅ `services/billing-service/BillingService.csproj`

## Files Created

1. ✅ `setup_databases.py`
2. ✅ `POSTGRESQL_SETUP.md`
3. ✅ `DATABASE_SETUP_GUIDE.md`
4. ✅ `CHANGES_SUMMARY.md` (this file)

---

## Next Steps

1. **Install PostgreSQL** - Follow POSTGRESQL_SETUP.md
2. **Create Databases** - Run setup_databases.py
3. **Start Services** - Run Grades and Billing services
4. **Test APIs** - Use Swagger UI or Postman
5. **Integrate with Gateway** - Connect to API Gateway

---

## Rollback

If you need to revert to the original configuration:

1. Restore `services/grades-service/app/database.py` from git
2. Restore `services/billing-service/` files from git
3. Update connection strings back to original values

---

## Support

For issues, refer to:
- `DATABASE_SETUP_GUIDE.md` - Troubleshooting section
- `POSTGRESQL_SETUP.md` - Installation help
- Service logs for detailed error messages
