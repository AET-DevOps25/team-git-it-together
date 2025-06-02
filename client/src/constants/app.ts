const appName = import.meta.env.VITE_APP_NAME;
const appVersion = import.meta.env.VITE_APP_VERSION;
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL + import.meta.env.VITE_API_VERSION;

// On server startup, these environment variables should be logged to the console
console.info(`🚀 Application Name: ${appName} started - Version: ${appVersion}`);
console.info(`📡 API Base URL: ${apiBaseUrl}`);


export const APP_NAME = appName || "My App";
export const APP_VERSION = appVersion || "1.0.0";
export const API_BASE_URL = apiBaseUrl || 'http://localhost:9000/api/v1';

