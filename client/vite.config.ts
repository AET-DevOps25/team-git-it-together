import { defineConfig, loadEnv } from 'vite'
import { resolve } from 'path'
import react from '@vitejs/plugin-react'
import tailwindcss from "@tailwindcss/vite"

export default defineConfig(({ mode }) => {
  // Load env vars for current mode (.env, .env.development, etc.)
  const env = loadEnv(mode, process.cwd())

  const port = parseInt(env.VITE_PORT || '3000', 10)

  return {
    base: '/',
    plugins: [react(), tailwindcss()],
    preview: {
      port,
      strictPort: true,
    },
    server: {
      port,
      strictPort: true,
      open: true,
      host: true,
      origin: `http://0.0.0.0:${port}`,
    },
    define: {
      __APP_NAME__: JSON.stringify(env.VITE_APP_NAME),
      __APP_VERSION__: JSON.stringify(env.VITE_APP_VERSION),
      __API_VERSION__: JSON.stringify(env.VITE_API_VERSION),
    },
    build: {
      rollupOptions: {
        input: {
          main: resolve(__dirname, 'index.html'),
        },
      },
    },
  }
})
