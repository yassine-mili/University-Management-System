# PostgreSQL Setup Guide for Windows

## Option 1: Download PostgreSQL Installer (Recommended)

1. **Download PostgreSQL 14 for Windows**
   - Visit: https://www.postgresql.org/download/windows/
   - Download PostgreSQL 14.x installer

2. **Run the Installer**
   - Run the .exe file
   - Set password for `postgres` user (remember this!)
   - Default port: 5432
   - Install pgAdmin (optional but helpful)

3. **Verify Installation**
   ```powershell
   psql --version
   ```

## Option 2: Using Windows Package Manager (winget)

```powershell
winget install PostgreSQL.PostgreSQL
```

## Option 3: Using Scoop

```powershell
scoop install postgresql
```

---

## After Installation

### 1. Create Databases

```powershell
# Connect to PostgreSQL
psql -U postgres

# In psql prompt, create databases:
CREATE DATABASE grades_db;
CREATE DATABASE students_db;
CREATE DATABASE auth_db;

# List databases
\l

# Exit
\q
```

### 2. Update Connection Strings

The services will connect to:
- **Grades DB**: `postgresql://user:password@localhost:5435/grades_db`
- **Students DB**: `postgresql://user:password@localhost:5432/students_db`
- **Auth DB**: `postgresql://postgres:postgres@localhost:5433/auth_db`

### 3. Verify PostgreSQL is Running

```powershell
# Check if PostgreSQL service is running
Get-Service postgresql-x64-14

# Start service if needed
Start-Service postgresql-x64-14
```

---

## Environment Variables

Set these in your system or .env files:

```
DATABASE_URL=postgresql://user:password@localhost:5435/grades_db
POSTGRES_USER=user
POSTGRES_PASSWORD=password
POSTGRES_DB=grades_db
```

---

## Troubleshooting

**Port Already in Use:**
```powershell
# Find process using port 5432
netstat -ano | findstr :5432

# Kill process (replace PID)
taskkill /PID <PID> /F
```

**Can't Connect:**
- Ensure PostgreSQL service is running
- Check firewall settings
- Verify credentials in connection string

**psql not found:**
- Add PostgreSQL bin directory to PATH
- Default: `C:\Program Files\PostgreSQL\14\bin`
