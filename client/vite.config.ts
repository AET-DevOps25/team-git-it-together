import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';
import * as path from 'node:path';
import { resolve } from 'path';

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd());
  const port = parseInt(env.VITE_PORT || '3000', 10);
  const serverGatewayHost = process.env.VITE_API_INTERNAL_HOST || 'localhost';
  const serverGatewayPort = process.env.VITE_API_INTERNAL_PORT || '8081';

  // If running inside Docker Compose, use "server-gateway", else "localhost"
  const devApiTarget = `http://${serverGatewayHost}:${serverGatewayPort}`;
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
      // In Production (docker), The nginx server will handle the API requests and proxy them to the backend services.
      // In Development, we use Vite's proxy to forward requests to the backend API.
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
