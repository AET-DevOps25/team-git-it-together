# 🎨 SkillForge Client

A modern React-based frontend for the **SkillForge** learning platform, built with TypeScript, Vite, and Tailwind CSS.

## 🚀 Features

- **Modern Tech Stack**: React 18, TypeScript, Vite, Tailwind CSS
- **UI Components**: Radix UI primitives with custom styling
- **State Management**: React Query for server state
- **Form Handling**: React Hook Form with Zod validation
- **Routing**: React Router DOM with protected routes
- **Authentication**: JWT-based auth with context provider
- **Real-time Features**: AI chat, course progress tracking
- **Responsive Design**: Mobile-first approach with Tailwind
- **Testing**: Vitest with coverage reporting
- **Docker Ready**: Multi-stage build with Nginx

## 🏗️ Architecture

```
client/
├── src/
│   ├── components/     # Reusable UI components
│   ├── pages/         # Route components
│   ├── contexts/      # React contexts (Auth, etc.)
│   ├── hooks/         # Custom React hooks
│   ├── services/      # API service layer
│   ├── types/         # TypeScript type definitions
│   ├── utils/         # Utility functions
│   └── lib/           # Third-party library configs
├── public/            # Static assets
└── dist/              # Build output
```

## 📋 Prerequisites

Before running the client, ensure you have:

- **Node.js** (v18 or higher)
- **npm** or **yarn**
- **Backend Services Running**:
  - Server Gateway (port 8081)
  - User Service (port 8082) 
  - Course Service (port 8083)
  - GenAI Service (port 8888)
- **Infrastructure Services**:
  - MongoDB (port 27017)
  - Redis (port 6379)
  - Weaviate (port 8080)

## 🔧 Environment Variables

Create a `.env` file in the client directory:

```env
# App Configuration
VITE_APP_NAME=SkillForge
VITE_APP_VERSION=1.0.0
VITE_API_VERSION=v1
VITE_PORT=3000

# API Configuration
VITE_API_INTERNAL_HOST=localhost
VITE_API_INTERNAL_PORT=8081
VITE_API_BASE_URL=http://localhost:8081/api/v1

# Development
VITE_DEV_MODE=1
```

### Environment Modes

The client supports different environment modes:

- **Development**: `npm run dev` (uses `.env.development`)
- **Test**: `npm run dev:test` (uses `.env.test`) 
- **Production**: `npm run dev:prod` (uses `.env.production`)

## 🚀 Quick Start

### 1. Install Dependencies

```bash
npm install
```

### 2. Set Up Environment

```bash
# Copy example environment file
cp .env.example .env

# Edit .env with your configuration
nano .env
```

### 3. Start Development Server

```bash
npm run dev
```

The app will be available at `http://localhost:3000`

## 🐳 Docker Development

### Using Docker Compose (Recommended)

The client is configured to work with the main project's Docker Compose setup:

```bash
# From project root
docker compose up client
```

### Standalone Docker

```bash
# Build image
docker build -t skillforge-client .

# Run container
docker run -p 3000:3000 \
  -e VITE_API_INTERNAL_HOST=host.docker.internal \
  -e VITE_API_INTERNAL_PORT=8081 \
  skillforge-client
```

## 🔗 Backend Integration

The client connects to these backend services:

| Service | Port | Purpose |
|---------|------|---------|
| Gateway | 8081 | API routing, auth, rate limiting |
| User Service | 8082 | User management, authentication |
| Course Service | 8083 | Course content, progress tracking |
| GenAI Service | 8888 | AI chat, course generation |

### API Proxy Configuration

In development mode, Vite proxies `/api/*` requests to the gateway service:

```typescript
// vite.config.ts
proxy: {
  '/api': {
    target: 'http://localhost:8081',
    changeOrigin: true,
  }
}
```

## 🧪 Testing

```bash
# Run tests
npm test

# Run tests with coverage
npm run test:coverage

# Run tests in watch mode
npm run test:watch
```

## 📦 Build & Deploy

### Development Build

```bash
npm run build:dev
```

### Production Build

```bash
npm run build
```

### Preview Build

```bash
# Preview production build
npm run preview

# Preview development build
npm run preview:dev
```

## 🛠️ Development Scripts

```bash
# Development
npm run dev              # Start dev server
npm run dev:test         # Start with test env
npm run dev:prod         # Start with prod env

# Building
npm run build            # Production build
npm run build:dev        # Development build
npm run build:test       # Test build

# Preview
npm run preview          # Preview production build
npm run preview:dev      # Preview dev build
npm run preview:test     # Preview test build

# Code Quality
npm run lint             # ESLint check
npm run format           # Prettier format
npm run check            # Type check + lint + format

# Testing
npm test                 # Run tests
npm run test:coverage    # Run tests with coverage
```

## 🔍 Troubleshooting

### Common Issues

1. **API Connection Errors**
   - Ensure backend services are running
   - Check environment variables in `.env`
   - Verify proxy configuration in `vite.config.ts`

2. **Port Conflicts**
   - Change `VITE_PORT` in `.env`
   - Kill processes using port 3000

3. **Build Errors**
   - Clear `node_modules` and reinstall: `rm -rf node_modules && npm install`
   - Check TypeScript errors: `npm run check`

4. **Docker Issues**
   - Ensure Docker Desktop is running
   - Check container logs: `docker logs <container-name>`
   - Verify network connectivity between containers

### Debug Mode

Enable debug logging by setting in `.env`:

```env
VITE_ENABLE_DEBUG_LOGS=true
```

## 📚 API Documentation

- **User Service**: `http://localhost:8081/api/v1/users/docs`
- **Course Service**: `http://localhost:8083/api/v1/courses/docs`
- **GenAI Service**: `http://localhost:8888/docs`

## 🔐 Authentication

The client uses JWT-based authentication:

1. **Login**: POST `/api/v1/auth/login`
2. **Register**: POST `/api/v1/auth/register`
3. **Token Storage**: Local storage with automatic refresh
4. **Protected Routes**: Automatic redirect to login

## 🎯 Key Features

- **User Management**: Registration, login, profile management
- **Course System**: Browse, enroll, track progress
- **AI Integration**: Chat assistant, course generation
- **Achievements**: Gamification with badges and rewards
- **Responsive Design**: Works on desktop, tablet, and mobile
- **Real-time Updates**: Live progress tracking and notifications
