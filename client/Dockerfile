FROM node:20-alpine AS deps
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

# Build app
FROM deps AS builder
WORKDIR /app

COPY . .
RUN npm run build

# Serve with Nginx
FROM nginx:stable-alpine AS runner

# Set working dir to match nginx's root
WORKDIR /usr/share/nginx/html

# Replace default Nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy build output from builder
COPY --from=builder /app/dist .

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]
