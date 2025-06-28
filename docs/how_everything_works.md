# 📝 How the SkillForge Stack Works

## 🌐 Architecture Overview

The application is composed of several **microservices**, all containerized and managed via **Docker Compose**.
We use **Traefik** as a dynamic reverse proxy and Nginx in the frontend client container for optimized SPA/static asset delivery and API proxying.

### **Main Components**

* **reverse-proxy (Traefik):** Entry point for all HTTP/HTTPS traffic, SSL termination, service routing, dashboard.
* **nginx:** Serves the React/Vite frontend, proxies API requests to the backend.
* **server-gateway:** Spring Boot Java backend, API Gateway for the app.
* **user-service:** Spring Boot Java backend, user management.
* **client:** React/Vite frontend, optionally served via Nginx.
* **weaviate-db:** Vector database for AI use cases.
* **genai:** Python-based GenAI microservice (LangChain/OpenAI).


## 🚏 **How Routing & Networking Works**

### **1. External Traffic (from browser or Postman)**

* **In Production:**

  * We set public DNS or use a wildcard DNS service (like nip.io or localhost for dev).
  * **Traefik** listens on ports 80 (HTTP) and 443 (HTTPS) and routes traffic based on the Host header and path rules.
  * Example:

    * `https://client.domain.com` → routed to the `client` service.
    * `https://api.domain.com/api` → routed to the `server-gateway` service.
* **In Local Development:**

  * We use hostnames like `client.localhost`, `server.localhost`, etc for local testing.

### **2. Internal Communication (between containers)**

* Docker Compose creates an **internal network** (e.g. `app-network`).
* Services communicate using **service/container names** (not FQDN/localhost).
* Example:

  * From `client` (Nginx) to backend:

    * `proxy_pass http://server-gateway:8081/api/;`
  * From `server-gateway` to `user-service`:

    * `http://user-service:8082/api/`
  * from `server-gateway` to `course-service`:

    * `http://course-service:8083/api/`


## 🛡️ **Why Use Service Names, Not Domains**

* **Docker’s internal DNS** automatically resolves service names to the correct container IP.
* Using `server-gateway` (service name) works **inside Docker network**.
* Using `server-gateway.localhost` or custom FQDN **does not work inside Docker** unless you set up custom DNS or hack /etc/hosts (not recommended).


## ⚙️ **How the Env Vars Flow**

* **External access:**

  * Browser/Postman → Traefik → Client/Server → Upstream services.
* **Env file (`.env.prod`, `.env.local`, etc.):**

  * Used to provide secrets, port numbers, API keys, and all other configuration values.
* **Service-to-service:**

  * Use Docker Compose variable substitution:

    ```yaml
    API_HOST: ${SERVER_HOST}
    ```

    with `.env`:

    ```
    SERVER_HOST=server-gateway
    ```
  * This is resolved inside containers as needed.


## 🏗️ **Production vs. Local**

* **Production:**

  * Images are pulled from a registry (e.g., GitHub Container Registry).
* **Local Development:**

  * Images are built locally from source using the `build:` key in Compose.
  * This allows us to develop and test changes before pushing to production.


## 🖥️ **Development and Debugging**

* **Service URLs:**

  * Traefik dashboards (in local mode) can be accessed on a specific port (e.g., [http://localhost:8085/dashboard/](http://localhost:8085/dashboard/)).
  * Each service is accessible via its mapped port, or via Traefik domain routing if configured.
* **Logs and Troubleshooting:**

  * Use `docker-compose logs <service>` for service logs.
  * For failed containers, check `docker ps -a` and `docker logs <container>`.


## 🚨 **Common Gotchas**

* **Host not found in upstream**
  If you see `host not found in upstream "server.localhost"` in Nginx, it means you're using a domain that Docker’s internal DNS cannot resolve. Use the **Docker service name** (e.g., `server-gateway`) inside Docker Compose networks instead of `localhost` or custom domains.

* **Certificate resolver errors**
  `Router uses a nonexistent certificate resolver certificateResolver=letsencrypt` usually means the letsencrypt resolver is not properly defined or enabled.

  * Only configure Let's Encrypt with **public domains** and with ports 80/443 exposed.
  * For local-only use, **disable** certificate resolvers (comment out the resolver lines in your Traefik labels and command).

* **Cannot connect to the Docker daemon at unix:///var/run/docker.sock**

  * Make sure Docker is running.
  * On Linux/macOS, your user needs permission to access the Docker socket.
  * On Windows, sometimes WSL integration or Docker Desktop needs a restart.

* **Containers exit immediately / no logs**

  * This usually means missing environment variables, config files, or incorrect entrypoints.
  * Also check if the service it depends on is healthy and running (some services it depends on might not be ready yet or have no health checks).
  * Run `docker-compose logs <service>` for clues.
  * Try running the image interactively: `docker run -it <image> sh` to debug.

* **Ports already in use**

  * Error like `bind: address already in use` means another process or Docker run is using the same port.
  * Run `lsof -i :PORT` or `netstat -anp | grep PORT` to see what is using it.

* **Env variables not substituted / are empty in containers**

  * Make sure `.env` files are in the correct directory and referenced in `env_file:`.
  * Use `${VAR:-default}` syntax in Compose for reliable defaults.
  * If you update the `.env` file, restart containers for changes to apply.

* **Health checks fail, containers restart forever**

  * The health check `test:` command must match what the service actually serves and the port.
  * For Spring Boot: make sure `/actuator/health` endpoint is exposed.
  * For custom services: create a `/healthz` or `/ping` endpoint.

* **Traefik dashboard 404 or unreachable**

  * Check which port you mapped it to (default is 8080, but you can set it to another).
  * You need to enable it via the right flags in `command:` section of Traefik service.
  * Dashboard should be mapped to `web` entrypoint or a dedicated entrypoint like `dashboard`.

* **CORS Issues**

  * Make sure all backend services set appropriate `Access-Control-Allow-Origin` headers, especially for local development.
  * Double-check the value of `CORS_ALLOW_ORIGINS` in your environment.

* **Differences between Local and Production**

  * In production, containers might have different hostnames, domains, and networks.
  * Always check if you are using correct service names vs. public domain names depending on the context.