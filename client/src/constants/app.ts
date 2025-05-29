const appName = import.meta.env.VITE_APP_NAME;
const appVersion = import.meta.env.VITE_APP_VERSION;

export const APP_NAME = appName || "My App";
export const APP_VERSION = appVersion || "1.0.0";

