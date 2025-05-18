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
SERVER_PORT=8080

# MongoDB
MONGO_PORT=27017
MONGO_INITDB_ROOT_USERNAME=root
MONGO_INITDB_ROOT_PASSWORD=root
MONGODB_URI=mongodb://root:root@localhost:27017/skill_forge_dev?authSource=admin
MONGODB_DATABASE=skill_forge_dev
```

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

To run the Spring Boot app locally, there are two options:
1. **Using IntelliJ IDEA**:
   1. Open the project in your IDE.
   2. (Optional) Make sure the profile is set to `dev` in your IDE run configuration.
   3. Add the same configuration variables as in `.env.dev` to your IDE run configuration.
   4Run the application.

2. **Using Command Line**:
    #### macOS / Linux
    ```bash
    # ensure you are in the server directory
    cd server
    # run the app
   ./scripts/macos-linux/run-dev.sh
    ````
    #### Windows
    ```cmd
    cd server
    scripts\windows\run-dev.bat
    ```

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

You can connect via a Mongo GUI like [MongoDB Compass](https://www.mongodb.com/try/download/compass) or [Studio 3T](https://studio3t.com/download/) to:
```
mongodb://root:root@localhost:27017/skill_forge_dev?authSource=admin
```
This will allow you to view the database and collections.

## 🧪 Testing the Spring Boot Server

You can use [Postman](https://www.postman.com/downloads/) or [Insomnia](https://insomnia.rest/download) to test the API endpoints. Or directly in IntelliJ IDEA using the built-in HTTP client.

### ✅ Expected Response (when MongoDB is connected)
```http
HTTP/1.1 200 OK
Content-Type: text/plain

Server is up — MongoDB ping OK: { "ok" : 1.0 }
```
### ❌ Expected Response (when MongoDB is down or unreachable)
```http
HTTP/1.1 503 Service Unavailable
Content-Type: text/plain

Server is up — but MongoDB ping failed: <ERROR MESSAGE>
```