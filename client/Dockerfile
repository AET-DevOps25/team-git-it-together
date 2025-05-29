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
ARG VITE_SERVER_API_URL
ARG VITE_API_VERSION
ARG BUILD_MODE
ARG VITE_PORT

# Expose to Vite
ENV VITE_PORT=$VITE_PORT \
    VITE_APP_NAME=$VITE_APP_NAME \
    VITE_APP_VERSION=$VITE_APP_VERSION \
    VITE_SERVER_API_URL=$VITE_SERVER_API_URL \
    VITE_API_VERSION=$VITE_API_VERSION \
    BUILD_MODE=$BUILD_MODE

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
