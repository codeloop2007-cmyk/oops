import { API_BASE, AI_SERVICE_URL } from "@/lib/constants";
import type {
  AgentRequest,
  AgentResponse,
  BookSlotRequest,
  DoctorRequest,
  DoctorResponse,
  LiveKitTokenRequest,
  LiveKitTokenResponse,
  SlotRequest,
  SlotResponse,
  UserRequest,
  UserResponse,
} from "@/types/api";

export class ApiError extends Error {
  status: number;
  body: unknown;

  constructor(status: number, message: string, body: unknown) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.body = body;
  }
}

function messageFromBody(status: number, body: unknown): string {
  if (typeof body === "string" && body.trim()) return body;
  if (body && typeof body === "object") {
    const record = body as Record<string, unknown>;
    if (typeof record.message === "string" && record.message.trim()) {
      return record.message;
    }
    if (record.errors && typeof record.errors === "object") {
      const parts = Object.values(record.errors as Record<string, unknown>)
        .filter((v): v is string => typeof v === "string");
      if (parts.length) return parts.join(" ");
    }
    if (typeof record.detail === "string") return record.detail;
    if (typeof record.error === "string") return record.error;
  }
  return `Request failed (${status})`;
}

export async function apiFetch<T>(path: string, init?: RequestInit, baseUrl: string = API_BASE): Promise<T> {
  const headers = new Headers(init?.headers);
  const hasBody = init?.body !== undefined && init.body !== null;
  if (hasBody && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }
  headers.set("Accept", "application/json");

  const response = await fetch(`${baseUrl}${path}`, {
    ...init,
    headers,
  });

  const raw = await response.text();
  let parsed: unknown = null;
  if (raw) {
    try {
      parsed = JSON.parse(raw);
    } catch {
      parsed = raw;
    }
  }

  if (!response.ok) {
    throw new ApiError(response.status, messageFromBody(response.status, parsed), parsed);
  }

  if (response.status === 204 || raw === "") {
    return undefined as T;
  }

  return parsed as T;
}

export function getUsers() {
  return apiFetch<UserResponse[]>("/api/users");
}

export function getUserById(id: number) {
  return apiFetch<UserResponse>(`/api/users/${id}`);
}

export function createUser(body: UserRequest) {
  return apiFetch<UserResponse>("/api/users", {
    method: "POST",
    body: JSON.stringify(body),
  });
}

export function updateUser(id: number, body: UserRequest) {
  return apiFetch<UserResponse>(`/api/users/${id}`, {
    method: "PUT",
    body: JSON.stringify(body),
  });
}

export function deleteUser(id: number) {
  return apiFetch<void>(`/api/users/${id}`, { method: "DELETE" });
}

export function getDoctors() {
  return apiFetch<DoctorResponse[]>("/api/doctors");
}

export function getDoctorById(id: number) {
  return apiFetch<DoctorResponse>(`/api/doctors/${id}`);
}

export function createDoctor(body: DoctorRequest) {
  return apiFetch<DoctorResponse>("/api/doctors", {
    method: "POST",
    body: JSON.stringify(body),
  });
}

export function updateDoctor(id: number, body: DoctorRequest) {
  return apiFetch<DoctorResponse>(`/api/doctors/${id}`, {
    method: "PUT",
    body: JSON.stringify(body),
  });
}

export function deleteDoctor(id: number) {
  return apiFetch<void>(`/api/doctors/${id}`, { method: "DELETE" });
}

export function searchDoctors(q: string, limit = 10) {
  const params = new URLSearchParams({ q, limit: String(limit) });
  return apiFetch<DoctorResponse[]>(`/api/doctors/search?${params.toString()}`);
}

export function getSlots() {
  return apiFetch<SlotResponse[]>("/api/slots");
}

export function createSlot(body: SlotRequest) {
  return apiFetch<SlotResponse>("/api/slots", {
    method: "POST",
    body: JSON.stringify(body),
  });
}

export function getSlotById(id: number) {
  return apiFetch<SlotResponse>(`/api/slots/${id}`);
}

export function updateSlot(id: number, body: SlotRequest) {
  return apiFetch<SlotResponse>(`/api/slots/${id}`, {
    method: "PUT",
    body: JSON.stringify(body),
  });
}

export function deleteSlot(id: number) {
  return apiFetch<void>(`/api/slots/${id}`, { method: "DELETE" });
}

export function getSlotsByDoctor(doctorId: number) {
  return apiFetch<SlotResponse[]>(`/api/slots/doctor/${doctorId}`);
}

export function getAvailableSlotsByDoctor(doctorId: number) {
  return apiFetch<SlotResponse[]>(`/api/slots/doctor/${doctorId}/available`);
}

export function bookSlot(slotId: number, body: BookSlotRequest) {
  return apiFetch<SlotResponse>(`/api/slots/${slotId}/book`, {
    method: "POST",
    body: JSON.stringify(body),
  });
}

export function askAgent(query: AgentRequest["query"]) {
  return apiFetch<AgentResponse>("/api/agent/ask", {
    method: "POST",
    body: JSON.stringify({ query }),
  });
}

export function getLiveKitToken(body: LiveKitTokenRequest) {
  return apiFetch<LiveKitTokenResponse>(
    "/livekit/token",
    {
      method: "POST",
      body: JSON.stringify(body),
    },
    AI_SERVICE_URL,
  );
}
