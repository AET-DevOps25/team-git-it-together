# 🛠 Local Development Setup Guide

This is the frontend for the **Skill Forge** learning platform, built using:

- ⚡ [Vite](https://vitejs.dev/) for blazing-fast dev and build tooling  
- ⚛️ [React](https://reactjs.org/) with TypeScript  
- 🎨 [Tailwind CSS](https://tailwindcss.com/) for modern utility-first styling  
- 🐳 Docker-ready for production deployment

---

## 🚀 Getting Started (Development)

1. **Install dependencies:**

```bash
npm install
````

2. **Run in development mode with hot reload:**

```bash
npm run dev
```

* App runs at: `http://localhost:3000`
* Will auto-open in your browser

---

## 🏗️ Build for Production

To create an optimized production build:

```bash
npm run build
```

The build output will be placed in the `dist/` directory.

---

## 🔍 Preview Production Build

To locally test your production build:

```bash
npm run preview
```

By default, it runs at: `http://localhost:3000`

---

## 🐳 Docker Usage

### 🧱 Build the Docker Image

```bash
docker build -t skill-forge-frontend .
```

### ▶️ Run the Container

```bash
docker run --name skill-forge-client -p 3000:3000 skill-forge-frontend
```

Then open `http://localhost:3000` in your browser.

### 📦 Image Details

* Built using multi-stage Docker build
* Final image uses `nginx:alpine` and serves the app from `/usr/share/nginx/html`
* Configured for multi-page apps (`/dashboard`, `/profile`, etc.)
* See `nginx.conf` for custom routing behavior

---

## 🧰 Scripts

```json
"scripts": {
  "dev": "vite",
  "build": "tsc -noEmit && vite build",
  "preview": "vite preview",
  "lint": "eslint . --ext .ts,.tsx",
  "format": "prettier --write .",
  "check": "tsc --noEmit && eslint . --ext .ts,.tsx && prettier --check ."
}
```
