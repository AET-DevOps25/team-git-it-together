import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';
import * as path from 'node:path';
import { resolve } from 'path';

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd());
  const port = parseInt(env.VITE_PORT || '3000', 10);

  // Determine API base for dev
  const isDocker = process.env.DOCKERIZED === '1';
  // If running inside Docker Compose, use "server", else "localhost"
  const devApiTarget = isDocker ? 'http://server:8080' : 'http://localhost:8080';

  return {
    base: '/',
    plugins: [react()],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src'),
      },
    },
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
      // ---- DEV PROXY ----
      proxy:
        mode === 'development'
          ? {
              '/api': {
                target: devApiTarget,
                changeOrigin: true,
                rewrite: (path) => path,
              },
            }
          : undefined,
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
  };
});
