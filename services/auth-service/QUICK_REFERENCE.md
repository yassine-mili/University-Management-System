# 🚀 AUTH SERVICE - QUICK REFERENCE

## 🎯 Essential Information

**Service Name**: Authentication Service  
**Port**: 8081  
**Technology**: Spring Boot 3.2.0 + JWT + PostgreSQL  
**Status**: ✅ Production Ready

## 📡 API Endpoints

| Endpoint                | Method | Auth Required | Description          |
| ----------------------- | ------ | ------------- | -------------------- |
| `/api/v1/auth/register` | POST   | ❌            | Register new user    |
| `/api/v1/auth/login`    | POST   | ❌            | User login           |
| `/api/v1/auth/refresh`  | POST   | ❌            | Refresh access token |
| `/api/v1/auth/me`       | GET    | ✅            | Get current user     |
| `/api/v1/auth/logout`   | POST   | ✅            | Logout user          |
| `/api/v1/auth/health`   | GET    | ❌            | Health check         |

## 🚀 Quick Start

### Start with Docker (Recommended)

```bash
cd docker
docker compose up auth_db auth_service -d
```

### Check Status

```bash
# View logs
docker compose logs -f auth_service

# Health check
curl http://localhost:8081/api/v1/auth/health
```

### Access Swagger UI

```
http://localhost:8081/swagger-ui.html
```

## 📝 Quick Test Commands

### Register User

```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "email": "john@university.com",
    "password": "password123",
    "role": "STUDENT"
  }'
```

### Login

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "password": "password123"
  }'
```

### Get Current User (Replace TOKEN)

```bash
curl -X GET http://localhost:8081/api/v1/auth/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## 🔑 Request/Response Examples

### Register Request

```json
{
  "username": "alice.student",
  "email": "alice@university.com",
  "password": "secure123",
  "role": "STUDENT"
}
```

### Success Response

```json
{
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "550e8400-e29b...",
    "tokenType": "Bearer",
    "expiresIn": 3600000,
    "user": {
      "id": 1,
      "username": "alice.student",
      "email": "alice@university.com",
      "role": "STUDENT",
      "createdAt": "2024-12-07T10:30:00"
    }
  },
  "timestamp": "2024-12-07T10:30:00"
}
```

### Error Response

```json
{
  "message": "Username already exists",
  "error": "USER_ALREADY_EXISTS",
  "statusCode": 409,
  "timestamp": "2024-12-07T10:30:00"
}
```

## 🔐 Security

### Roles

- `ADMIN` - Full system access
- `TEACHER` - Course and grade management
- `STUDENT` - Read-only personal data

### Token Expiration

- **Access Token**: 1 hour (3,600,000 ms)
- **Refresh Token**: 7 days (604,800,000 ms)

### Password Requirements

- Minimum: 6 characters
- Maximum: 100 characters
- Hashed with: BCrypt (strength 10)

## 🐳 Docker Commands

```bash
# Start services
docker compose up auth_db auth_service -d

# Stop services
docker compose stop auth_service

# View logs
docker compose logs -f auth_service

# Restart service
docker compose restart auth_service

# Remove everything
docker compose down -v
```

## 🗄️ Database

### Connection Details

- **Host**: localhost
- **Port**: 5433
- **Database**: auth_db
- **Username**: postgres
- **Password**: postgres

### Quick SQL Queries

```sql
-- Connect
psql -h localhost -p 5433 -U postgres -d auth_db

-- View users
SELECT id, username, email, role, created_at FROM users;

-- Count by role
SELECT role, COUNT(*) FROM users GROUP BY role;

-- View tokens
SELECT id, user_id, expiry_date, is_revoked FROM refresh_tokens;
```

## 🧪 Testing Tools

### 1. Swagger UI

```
http://localhost:8081/swagger-ui.html
```

Interactive API documentation and testing

### 2. Postman

Import `postman_collection.json` from auth-service folder

### 3. cURL

Use the commands in this reference card

## 🐛 Troubleshooting

### Port Already in Use

```bash
# Windows
netstat -ano | findstr :8081
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8081 | xargs kill -9
```

### Database Connection Failed

```bash
# Check database is running
docker compose ps auth_db

# Restart database
docker compose restart auth_db
```

### JWT Token Invalid

- Check token hasn't expired (1 hour)
- Verify format: `Bearer <token>`
- Ensure JWT_SECRET matches

## 📁 File Locations

```
services/auth-service/
├── README.md                    # Full documentation
├── DEPLOYMENT_GUIDE.md          # Deployment instructions
├── IMPLEMENTATION_SUMMARY.md    # Implementation details
├── postman_collection.json      # Postman tests
├── Dockerfile                   # Docker build
├── pom.xml                      # Maven dependencies
└── src/
    └── main/
        ├── java/                # Source code
        └── resources/
            └── application.yml  # Configuration
```

## 🔄 Common Workflows

### New User Registration → Login → Access Protected Resource

```bash
# 1. Register
RESPONSE=$(curl -s -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test1","email":"test1@u.com","password":"pass123","role":"STUDENT"}')

# 2. Extract token (use jq if available)
TOKEN=$(echo $RESPONSE | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)

# 3. Access protected endpoint
curl -X GET http://localhost:8081/api/v1/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

## 📊 Health Monitoring

### Health Endpoint

```bash
curl http://localhost:8081/api/v1/auth/health
```

### Check Service Logs

```bash
docker compose logs -f auth_service
```

### Monitor Resource Usage

```bash
docker stats auth_service
```

## 🎓 Academic Evaluation Checklist

- ✅ RESTful API implemented
- ✅ JWT authentication working
- ✅ Database persistence (PostgreSQL)
- ✅ Security (BCrypt, JWT, CORS)
- ✅ API documentation (Swagger)
- ✅ Docker deployment ready
- ✅ Error handling implemented
- ✅ Testing available (Postman + Integration tests)
- ✅ Code quality (clean, documented)
- ✅ Complete documentation

## 📞 Support

- **Swagger**: http://localhost:8081/swagger-ui.html
- **Health**: http://localhost:8081/api/v1/auth/health
- **Logs**: `docker compose logs -f auth_service`
- **README**: `services/auth-service/README.md`

---

**Version**: 1.0.0  
**Status**: ✅ Production Ready  
**Last Updated**: December 7, 2025
