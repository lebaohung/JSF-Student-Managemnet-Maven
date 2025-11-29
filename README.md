# JSF-Student-Managemnet-Maven

## Technology
- Java 1.8 - Java EE 8
- Wildfly 20.0.1
- JavaServer Faces 2.3
- Postgres 12.4
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

psql -U postgres -d jsfstudent -f schema.sql
```

### 3. Run deployment
```bash
./docker-deploy.sh
```

### 4. Test URLs
- Application: http://localhost:8080/studentManagement/

