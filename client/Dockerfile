# ────────────────────────────────
# Stage 1: Install Dependencies
# ────────────────────────────────
FROM node:20-alpine AS deps
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

# ────────────────────────────────
# Stage 2: Build the Application
# ────────────────────────────────
FROM deps AS builder
WORKDIR /app

COPY . .

# Accept build-time environment variables
ARG VITE_APP_NAME
ARG VITE_APP_VERSION
ARG VITE_API_BASE_URL
ARG VITE_API_VERSION
ARG BUILD_MODE
ARG VITE_PORT
ARG DOCKERIZED=0

# Expose to Vite
ENV VITE_PORT=${VITE_PORT}
ENV VITE_APP_NAME=${VITE_APP_NAME}
ENV VITE_APP_VERSION=${VITE_APP_VERSION}
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
ENV VITE_API_VERSION=${VITE_API_VERSION}
ENV BUILD_MODE=${BUILD_MODE}
ENV DOCKERIZED=${DOCKERIZED}

# Build with explicit mode
RUN npm run build -- --mode=$BUILD_MODE

# ────────────────────────────────
# Stage 3: Serve with Nginx
# ────────────────────────────────
FROM nginx:stable-alpine AS runner
WORKDIR /usr/share/nginx/html

# Replace default Nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy static build from previous stage
COPY --from=builder /app/dist .

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]
