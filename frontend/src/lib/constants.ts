export const API_BASE =
  import.meta.env.VITE_API_BASE_URL?.replace(/\/+$/, "") || "http://localhost:8080";

export const AI_SERVICE_URL =
  import.meta.env.VITE_AI_SERVICE_URL?.replace(/\/+$/, "") || "http://localhost:8000";

export const LIVEKIT_URL = import.meta.env.VITE_LIVEKIT_URL || "";

export const SESSION_STORAGE_KEY = "mediconnect.session";

export const QUERY_STALE_TIME = 30_000;
