# JSF-Student-Managemnet-Maven

## Introduction

A Student and Class Management System built with JSF for managing student records and class assignments.

**Features:**
- Student CRUD operations (name, email, phone, birthday)
- Class management with student enrollment
- Class monitor assignment
- Pagination support
- Auto-save on field edit
- Multi-language support (EN/VN)
- AJAX-based real-time updates

## Technology
- Java 1.8 - Java EE 8
- Wildfly 20.0.1
- JavaServer Faces 2.3 - Ajax
- Docker, Docker Compose
- PostgreSQL
- Maven

## Steps

### 1. Install these
- Docker, Docker Compose
- Maven
- Java 8 JDK
- PostgreSQL

### 2. Setup database
```bash
brew services start postgresql@15

psql -U postgres -c "CREATE DATABASE jsfstudent;"

psql -U postgres -d jsfstudent -f src/main/resources/com/ptit/sql/schema.sql
```

### 3. Run deployment
```bash
./docker-deploy.sh
```

### 4. Test URLs
- Application: http://localhost:8080/studentManagement/

### 5. Shutdown & Restart
**Shutdown:**
```bash
docker-compose down
```

**Rebuild after code change:**
```bash
docker-compose build wildfly
```

**Restart:**
```bash
docker-compose up -d
```