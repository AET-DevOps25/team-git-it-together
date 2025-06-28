const appName = import.meta.env.VITE_APP_NAME ?? 'My App';
const appVersion = import.meta.env.VITE_APP_VERSION ?? '1.0.0-alpha';

const rawBase = import.meta.env.VITE_API_BASE_URL ?? '/api';
const base = rawBase.replace(/\/$/, '');

const rawVersion = import.meta.env.VITE_API_VERSION ?? 'v1';
const version = rawVersion.replace(/^\//, '');

const apiBaseUrl = `${base}/${version}`;

// Public API URL (e.g., full URL for CORS, SSR, etc)
const publicApiUrl = import.meta.env.VITE_PUBLIC_API_URL ?? 'http://localhost:8081/api';

// API Host (for internal proxying or health checks)
const apiHost = import.meta.env.VITE_API_INTERNAL_HOST ?? 'localhost';

// API Port (for internal proxying or health checks)
const apiPort = import.meta.env.VITE_API_INTERNAL_PORT ?? '8081';

// Log on app start
console.info(`🚀 Application Name: ${appName} — Version: ${appVersion}`);
console.info(`📡 API Base URL: ${apiBaseUrl}`);
console.info(`🔗 PUBLIC_API_URL: ${publicApiUrl}`);
console.info(`🖥️  API_HOST: ${apiHost}`);
console.info(`🔢 API_PORT: ${apiPort}`);

export const APP_NAME = appName;
export const APP_VERSION = appVersion;
export const API_BASE_URL = apiBaseUrl;
export const PUBLIC_API_URL = publicApiUrl;
export const API_HOST = apiHost;
export const API_PORT = apiPort;
