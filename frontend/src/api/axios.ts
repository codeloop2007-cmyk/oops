import { env } from "@/config/env.config";
import axios, { AxiosError, AxiosRequestConfig } from "axios";
import { Platform } from "react-native";

let currentClientIdentityId: string | null = null;

export const setClientIdentityId = (id: string | null) => {
  currentClientIdentityId = id;
};

export const getClientIdentityId = (): string | null => {
  return currentClientIdentityId;
};

// On web, use relative URL ("") in development when targeting localhost so requests
// pass through the Metro dev proxy, eliminating browser CORS issues.
export const resolveBaseUrl = (): string => {
  if (Platform.OS === "web" && typeof window !== "undefined") {
    const target = env.EXPO_PUBLIC_BASE_URL || "";
    if (target.includes("localhost") || target.includes("127.0.0.1")) {
      return "";
    }
  }
  return env.EXPO_PUBLIC_BASE_URL;
};

export const axiosInstance = axios.create({
  baseURL: resolveBaseUrl(),
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 15000,
});

axiosInstance.interceptors.request.use((config) => {
  if (currentClientIdentityId) {
    config.headers["X-Client-User-Id"] = currentClientIdentityId;
  }
  return config;
});

export class ApiError extends Error {
  status: number;
  data: unknown;

  constructor(status: number, message: string, data?: unknown) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.data = data;
  }
}

export const extractErrorMessage = (error: unknown): string => {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data;
    if (typeof data === "string" && data.trim()) return data;
    if (data && typeof data === "object") {
      const record = data as Record<string, unknown>;
      if (typeof record.message === "string" && record.message.trim()) {
        return record.message;
      }
      if (typeof record.error === "string" && record.error.trim()) {
        return record.error;
      }
      if (record.details && typeof record.details === "object") {
        if (Array.isArray(record.details)) {
          return record.details.join(", ");
        }
        return Object.entries(record.details)
          .map(([k, v]) => `${k}: ${v}`)
          .join(", ");
      }
    }
    if (error.response?.status === 404) return "Resource not found.";
    if (error.response?.status === 409) return "Conflict: Resource already exists or is in use.";
    if (error.response?.status === 400) return "Invalid request or validation error.";
    if (error.message) return error.message;
  }
  if (error instanceof Error) return error.message;
  return "An unexpected error occurred.";
};

export const customAxios = async <T>(
  config: AxiosRequestConfig,
): Promise<T> => {
  try {
    const response = await axiosInstance.request<T>(config);
    return response.data;
  } catch (error) {
    const message = extractErrorMessage(error);
    const status = (error as AxiosError)?.response?.status ?? 500;
    const data = (error as AxiosError)?.response?.data;
    throw new ApiError(status, message, data);
  }
};
