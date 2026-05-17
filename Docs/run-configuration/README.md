# 🚀 Run Configuration Guide

> **A complete, spoon-feeding walkthrough of how to start the Online Banking Application — whether you want to run it with Docker (easiest) or manually on your local machine (for development).**

---

## 📑 Table of Contents

1. [What You Need Before Starting](#-prerequisites)
2. [Option A — Run with Docker Compose (Recommended ⭐)](#-option-a--run-with-docker-compose-recommended)
3. [Option B — Run Manually (Without Docker)](#-option-b--run-manually-without-docker)
4. [Verify Everything Is Working](#-verify-everything-is-working)
5. [Environment Variables & Configuration](#-environment-variables--configuration)
6. [Common Problems & Fixes](#-common-problems--fixes)
7. [Stopping the Application](#-stopping-the-application)

---

## 🧰 Prerequisites

Make sure the following are installed on your machine **before** you start.

### For Docker Compose (Option A)

| Tool | Version | How to Check | Download |
|------|---------|-------------|---------|
| 🐳 Docker Desktop | 24+ | `docker --version` | https://www.docker.com/products/docker-desktop |
| 🐳 Docker Compose | v2+ | `docker compose version` | Included in Docker Desktop |

> 💡 **That's it for Option A.** Docker handles everything else — Java, Node.js, MySQL are all bundled inside the containers.

---

### For Manual Run (Option B)

| Tool | Version | How to Check | Download |
|------|---------|-------------|---------|
| ☕ Java (JDK) | 17 | `java -version` | https://adoptium.net |
| 📦 Maven | 3.8+ | `mvn -version` | https://maven.apache.org/download.cgi |
| ⚛️ Node.js | 18+ | `node -v` | https://nodejs.org |
| 📦 npm | 8+ | `npm -v` | Included with Node.js |
| 🗄️ MySQL | 8.0 | `mysql --version` | https://dev.mysql.com/downloads |

---

## 🐳 Option A — Run with Docker Compose (Recommended ⭐)

This is the **easiest way**. One command starts everything — MySQL, the backend, and the frontend.

### Step 1 — Open a terminal in the project root

```bash
cd Documents-main
```

Make sure you can see `docker-compose.yml` here:

```
Documents-main/
  docker-compose.yml   ← this file must exist
  team3di-springbootbackendtest_v2/
  team3di-reactfrontendtest_v2/
```

---

### Step 2 — Build and start all containers

```bash
docker compose up --build
```

> ⏳ **First run takes 3–5 minutes** — Docker downloads base images and compiles the code.
> Subsequent runs are much faster because Docker caches the layers.

You will see output like this (this is good! ✅):

```
[+] Building backend...
[+] Building frontend...
[+] Running 3/3
 ✔ Container banking_mysql     Healthy
 ✔ Container banking_backend   Started
 ✔ Container banking_frontend  Started
```

---

### Step 3 — Open the application

Once all containers are running, open your browser and go to:

```
http://localhost:3000
```

---

### Step 4 — Check all containers are healthy (optional)

```bash
docker ps --filter name=banking --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

Expected output:

```
NAMES               STATUS                   PORTS
banking_frontend    Up X minutes             0.0.0.0:3000->80/tcp
banking_backend     Up X minutes             0.0.0.0:8080->8080/tcp
banking_mysql       Up X minutes (healthy)   3306/tcp
```

---

### To restart without rebuilding

```bash
docker compose up
```

### To rebuild only one service (e.g. after a code change)

```bash
docker compose up --build backend
docker compose up --build frontend
```

---

## 💻 Option B — Run Manually (Without Docker)

Use this option if you want to develop and make code changes without rebuilding Docker images every time.

---

### Step 1 — Start MySQL

If MySQL is already installed locally, make sure it is running on port **3306**.

Then create the database and load the schema:

```sql
-- In MySQL Workbench or mysql CLI:
CREATE DATABASE online_bank;
USE online_bank;

-- Then run the schema file:
SOURCE path/to/team3di-springbootbackendtest_v2/team3di-springbootbackendtest_v2/src/main/resources/schema.sql;

-- Optionally load sample data:
SOURCE path/to/team3di-springbootbackendtest_v2/team3di-springbootbackendtest_v2/src/main/resources/data.sql;
```

Or using the MySQL CLI:

```bash
mysql -u root -p online_bank < src/main/resources/schema.sql
mysql -u root -p online_bank < src/main/resources/data.sql
```

---

### Step 2 — Start the Spring Boot Backend

```bash
cd team3di-springbootbackendtest_v2/team3di-springbootbackendtest_v2

mvn spring-boot:run
```

The backend will start on **port 8080**. You should see:

```
Started ThreeDiTestingApplication in X.XXX seconds
```

> ⚙️ **Check `src/main/resources/application.properties`** to confirm the MySQL connection settings match your local setup (host, port, username, password).

---

### Step 3 — Start the React Frontend

Open a **second terminal window** and run:

```bash
cd team3di-reactfrontendtest_v2/team3di-reactfrontendtest_v2

npm install --legacy-peer-deps

npm start
```

The React development server will start and automatically open:

```
http://localhost:3000
```

> 💡 In development mode, the React app proxies API calls to `http://localhost:8080`. This is configured in `package.json` under the `"proxy"` field.

---

## ✅ Verify Everything Is Working

### Check the backend is responding

Open this URL in your browser or use curl:

```
http://localhost:8080/api/account
```

You should get a JSON response (not a connection error).

---

### Test with sample data

Use these credentials that come pre-loaded in `data.sql`:

| Field | Value |
|-------|-------|
| 🔢 Sort Code | `53-68-92` |
| 🏦 Account Number | `73084635` |

Expected result:
- 💰 **Current Balance: £1,071.78**
- 📋 **4 transactions** listed (April – July 2019)

---

### Test the date filter

Enter:

| Field | Value |
|-------|-------|
| 📅 Start Date | `2019-05-01` |
| 📅 End Date | `2019-06-30` |

Expected result: Only transactions between May 1 and June 30, 2019 appear.

---

## ⚙️ Environment Variables & Configuration

### Backend — `application.properties`

Located at:
```
team3di-springbootbackendtest_v2/.../src/main/resources/application.properties
```

Key settings:

```properties
# Database connection
spring.datasource.url=jdbc:mysql://mysql:3306/online_bank
spring.datasource.username=root
spring.datasource.password=rootpassword

# Schema management
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
spring.sql.init.data-locations=classpath:data.sql

# Server port
server.port=8080
```

> 🐳 **When running in Docker:** the hostname `mysql` refers to the MySQL container on the Docker network.
> 💻 **When running locally:** change `mysql` to `localhost`.

---

### Docker Compose — `docker-compose.yml`

Located at the project root. Key environment variables passed to each container:

```yaml
# MySQL container
MYSQL_ROOT_PASSWORD: rootpassword
MYSQL_DATABASE: online_bank

# Backend container
SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/online_bank
SPRING_DATASOURCE_USERNAME: root
SPRING_DATASOURCE_PASSWORD: rootpassword
```

---

## 🔧 Common Problems & Fixes

### 🔴 Problem: Port 3000 already in use

```
Error: listen EADDRINUSE: address already in use :::3000
```

**Fix:** Kill whatever is using port 3000:

```bash
# Windows
netstat -ano | findstr :3000
taskkill /PID <PID> /F

# Mac / Linux
lsof -ti:3000 | xargs kill
```

---

### 🔴 Problem: Port 8080 already in use

**Fix:** Stop any other Spring Boot or Tomcat process using port 8080.

```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

---

### 🔴 Problem: MySQL connection refused

```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
```

**Fix checklist:**
1. ✅ Is MySQL running? (`docker ps` or check MySQL service)
2. ✅ Is the hostname correct? (`mysql` for Docker, `localhost` for local)
3. ✅ Is the password correct in `application.properties`?
4. ✅ Does the `online_bank` database exist?

---

### 🔴 Problem: `npm ci` fails with lockfile error

```
npm error `npm ci` can only install packages when package-lock.json is in sync
```

**Fix:** Use `npm install` instead:

```bash
npm install --legacy-peer-deps
```

---

### 🔴 Problem: "no main manifest attribute in app.jar"

```
Error: Unable to access jarfile app.jar
```

**Fix:** Make sure `pom.xml` has the `spring-boot-maven-plugin` with a `repackage` goal. Then rebuild:

```bash
mvn clean package -DskipTests
```

---

### 🔴 Problem: Docker image not found (openjdk deprecated)

```
docker: Error response from daemon: pull access denied for openjdk:17-jdk-slim
```

**Fix:** The backend `Dockerfile` uses `eclipse-temurin:17-jre-jammy` which is the current official replacement. This should already be fixed. If not, check the backend `Dockerfile`.

---

### 🔴 Problem: Backend takes too long and MySQL health check fails

**Fix:** Increase the `start_period` in `docker-compose.yml` under the MySQL healthcheck, or simply re-run:

```bash
docker compose down
docker compose up --build
```

---

## 🛑 Stopping the Application

### Docker Compose

```bash
# Stop containers (data is preserved)
docker compose down

# Stop AND remove all data volumes (fresh start next time)
docker compose down -v
```

### Manual / Local

- **Backend:** Press `Ctrl + C` in the terminal running `mvn spring-boot:run`
- **Frontend:** Press `Ctrl + C` in the terminal running `npm start`
- **MySQL:** Stop the MySQL service via your OS or MySQL Workbench

---

## 📋 Quick Reference Cheatsheet

| Task | Command |
|------|---------|
| ▶️ Start everything (Docker) | `docker compose up --build` |
| 🔄 Restart without rebuilding | `docker compose up` |
| ⏹️ Stop everything | `docker compose down` |
| 📋 Check container status | `docker ps --filter name=banking` |
| 📜 View backend logs | `docker compose logs backend` |
| 📜 View frontend logs | `docker compose logs frontend` |
| 📜 View MySQL logs | `docker compose logs mysql` |
| 🔨 Rebuild one service | `docker compose up --build backend` |
| 🗑️ Full clean restart | `docker compose down -v && docker compose up --build` |

---

## 🌐 Service URLs

| Service | URL | Description |
|---------|-----|-------------|
| 🖥️ Frontend | http://localhost:3000 | React web app |
| ☕ Backend API | http://localhost:8080/api | Spring Boot REST API |
| 🗄️ MySQL | `localhost:3306` (local only) | Database (internal to Docker network) |

---

*📄 Run Configuration Guide · 3Di Assessment · Online Banking Application · 2026*
