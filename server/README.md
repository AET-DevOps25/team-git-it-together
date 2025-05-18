# 🛠 Local Development Setup Guide

This guide walks you through setting up MongoDB for local development, with cross-platform scripts for Windows, macOS, and Linux.

---

## 📦 Prerequisites

- [Docker](https://www.docker.com/get-started) installed and running
- Java 21 (for Spring Boot app)
- (Optional) IntelliJ IDEA or VS Code for development
- (Optional) MongoDB Compass or Studio 3T for database management
- (Optional) Postman or Insomnia for API testing
- (Optional) gradle installed (if you want to run the app without the wrapper)
---

## 📁 Project Structure (Relevant Parts)

```

├── server/
│   ├── start-mongo.sh / start-mongo.bat
│   ├── stop-mongo.sh  / stop-mongo.bat
│   └── .env.dev

````

---

## ⚙️ Environment Variables

Create a file named `.env.dev` in the `server/` directory:

```env
APP_NAME=skill-forge-server
SPRING_PROFILES_ACTIVE=dev
SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/skillforge_dev
````

---

## 🚀 Starting MongoDB for Local Dev

You can start MongoDB with the provided scripts:

### 🐧 macOS / Linux

```bash
chmod +x scripts/*.sh
./start-mongo.sh
```

### 🪟 Windows (Command Prompt)

```cmd
start-mongo.bat
```

This will:

* Pull and run the official `mongo:7.0` image
* Expose it on `localhost:27017`
* Use default database: `skill_forge_dev`
* Mount a volume `"mongo-dev-data` to persist data
* Set the container name to `mongo-dev`
* Set the environment variable `MONGO_INITDB_ROOT_USERNAME` to `root`
* Set the environment variable `MONGO_INITDB_ROOT_PASSWORD` to `root`

### ▶️ Running the Spring Boot App

To run the Spring Boot app locally, ensure the correct Spring profile is set using the `SPRING_PROFILES_ACTIVE` environment variable (which is already in the `.env.dev` file).

If you want to run the app with the command line, you can do so as follows:

#### macOS / Linux
```bash
SPRING_PROFILES_ACTIVE=dev
./gradlew bootRun
````

#### Windows

```cmd
set SPRING_PROFILES_ACTIVE=dev
.\gradlew.bat bootRun
```

> __Note__: If `SPRING_PROFILES_ACTIVE` is not set, the app defaults to `no-mongo` and skips database configuration.

---
## 🛑 Stopping MongoDB
### macOS / Linux
```bash
./stop-mongo.sh
```

### Windows

```cmd
stop-mongo.bat
```

---

## 🧼 Resetting MongoDB

To fully remove the container and its data:

```bash
docker rm -f mongo-dev
docker volume prune
```

---

## 🧪 Verifying MongoDB Connection

You can check if Mongo is running with:

```bash
docker ps
```

Or connect via a Mongo GUI like [MongoDB Compass](https://www.mongodb.com/try/download/compass) or [Studio 3T](https://studio3t.com/download/) to:
```
mongodb://localhost:27017/skillforge_dev
```