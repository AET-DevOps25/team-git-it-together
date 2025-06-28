# ────────────────────────────────
# Stage 1: Install Dependencies
# ────────────────────────────────
FROM node:22-alpine AS deps
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
ARG VITE_PUBLIC_API_URL
ARG VITE_API_INTERNAL_HOST
ARG VITE_API_INTERNAL_PORT
ARG BUILD_MODE
ARG VITE_PORT

# Expose them as environment variables (for npm run build)
ENV VITE_APP_NAME=$VITE_APP_NAME
ENV VITE_APP_VERSION=$VITE_APP_VERSION
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
ENV VITE_API_VERSION=$VITE_API_VERSION
ENV VITE_PUBLIC_API_URL=$VITE_PUBLIC_API_URL
ENV VITE_API_INTERNAL_HOST=$VITE_API_INTERNAL_HOST
ENV VITE_API_INTERNAL_PORT=$VITE_API_INTERNAL_PORT
ENV BUILD_MODE=$BUILD_MODE
ENV VITE_PORT=$VITE_PORT

# Build with explicit mode
RUN npm run build -- --mode=$BUILD_MODE
# Remove unnecessary files to reduce image size
RUN npm prune --production

# ────────────────────────────────
# Stage 3: Serve with Nginx
# ────────────────────────────────
FROM nginx:stable-alpine AS runner
WORKDIR /usr/share/nginx/html

# Copy Nginx template – docker-entrypoint will envsubst it
COPY nginx.conf.template /etc/nginx/templates/default.conf.template

# Copy static build from previous stage
COPY --from=builder /app/dist .

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]
