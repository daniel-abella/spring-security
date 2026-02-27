# Spring Boot OWASP Top 10 - Enhanced Security

This Spring Boot project demonstrates best practices for addressing OWASP Top 10 vulnerabilities with comprehensive security features.

## 🚀 Features Implemented

### High Priority Security Enhancements

✅ **JWT Authentication** - Stateless token-based authentication replacing HTTP Basic  
✅ **Input Validation** - DTOs with comprehensive validation constraints  
✅ **Role-Based Access Control (RBAC)** - Granular permissions for users and admins  
✅ **Audit Logging** - Automatic tracking of who created/modified entities  
✅ **Password Policies** - Strong password requirements enforced  
✅ **Spring Boot Actuator** - Health monitoring and metrics endpoints  
✅ **CORS Configuration** - Explicit origins whitelist  
✅ **Security Headers** - XSS, HSTS, Content-Type protection  
✅ **Environment Profiles** - Separate dev/prod configurations  
✅ **OWASP Dependency Check** - Automated CVE scanning  

## 📋 Prerequisites

- Java 17+
- Maven 3.6+

## 🏃 Quick Start

### 1. Clone and Build

```bash
mvn clean install
```

### 2. Run Application (Development Profile)

```bash
mvn spring-boot:run
```

Or specify production profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### 3. Default Credentials

- **Admin User**: `admin` / `Admin@123`
- **Regular User**: `user` / `User@123`

### 4. Get JWT Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "username": "admin"
}
```

### 5. Access Protected Endpoints

```bash
curl http://localhost:8080/api/products \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## 🔐 Security Features

### Password Requirements

Passwords must contain:
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one number
- At least one special character (@$!%*?&)

### Input Validation

All DTOs enforce:
- Product names: 2-100 characters
- Prices: positive decimal values
- Usernames: alphanumeric with hyphens/underscores
- Descriptions: max 500 characters

### Audit Fields

Entities automatically track:
- `createdAt` - when entity was created
- `updatedAt` - when entity was last modified
- `createdBy` - username who created
- `updatedBy` - username who last modified

## 🛠️ Available Endpoints

### Authentication
- `POST /api/auth/login` - Get JWT token (public)

### Products
- `GET /api/products` - List all products (public)
- `GET /api/products/{id}` - Get product by ID (conditional)
- `POST /api/products` - Create product (ADMIN only)
- `PUT /api/products/{id}` - Update product (ADMIN only)
- `DELETE /api/products/{id}` - Delete product (ADMIN only)

### Users
- `GET /api/users/me` - Get current user info (authenticated)
- `GET /api/users` - List all users (ADMIN only)
- `POST /api/users` - Create user (ADMIN only)

### Monitoring
- `GET /actuator/health` - Health check (public)
- `GET /actuator/metrics` - Metrics (ADMIN only)
- `GET /actuator/info` - App info (ADMIN only)

## 📦 Postman Collection

Import the included `spring-boot-authentication-authorization.postman_collection.json` file into Postman.

The collection includes:
- Pre-configured requests for all endpoints
- Automatic JWT token extraction and usage
- Examples of validation errors
- Test cases for different roles

## 🗄️ Database Access

H2 Console (Development only): http://localhost:8080/h2-console

- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

⚠️ **Note**: H2 console is disabled in production profile for security.

## 🔍 OWASP Dependency Check

Run vulnerability scan:

```bash
mvn dependency-check:check
```

Report will be generated in `target/dependency-check-report.html`

## 📊 Project Structure

```
src/main/java/com/example/demo/
├── config/           # Security, CORS, Auditing configuration
├── controller/       # REST endpoints
├── dto/             # Data Transfer Objects with validation
├── exception/       # Global exception handlers
├── mapper/          # Entity ↔ DTO converters
├── model/           # JPA entities
├── repository/      # Data access layer
├── security/        # JWT utilities and filters
└── service/         # Business logic
```

## 🌍 Environment Profiles

### Development (`application-dev.properties`)
- H2 console enabled
- Verbose logging (DEBUG level)
- SQL query logging enabled
- All actuator endpoints exposed

### Production (`application-prod.properties`)
- H2 console disabled
- Minimal logging (INFO/WARN)
- Restricted actuator endpoints
- JWT secret from environment variable
- Error details hidden

## 🛡️ OWASP Top 10 Coverage

See [OWASP-TOP10-COVERAGE.md](OWASP-TOP10-COVERAGE.md) for detailed analysis of how this project addresses each OWASP Top 10 vulnerability.

## 📝 License

This is a demonstration project for educational purposes.

## 🤝 Contributing

This project is for learning purposes. Feel free to fork and experiment!

---

**⚠️ Important**: This project uses development-friendly configurations. For production deployment:
1. Move secrets to environment variables or vault
2. Use a production-grade database (PostgreSQL, MySQL)
3. Enable HTTPS/TLS
4. Review and harden all security configurations
5. Set up proper logging aggregation
6. Configure rate limiting and DDoS protection
```