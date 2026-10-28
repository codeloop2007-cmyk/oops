import { z } from "zod";

const envSchema = z.object({
  EXPO_PUBLIC_BASE_URL: z.string().url().default("http://localhost:8080"),
  EXPO_PUBLIC_AI_SERVICE_URL: z.string().url().default("http://localhost:8000"),
});

export const env = envSchema.parse({
  EXPO_PUBLIC_BASE_URL: process.env.EXPO_PUBLIC_BASE_URL || "http://localhost:8080",
  EXPO_PUBLIC_AI_SERVICE_URL: process.env.EXPO_PUBLIC_AI_SERVICE_URL || "http://localhost:8000",
});
