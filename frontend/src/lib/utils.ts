import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function formatFee(fee: number): string {
  return `₹${Number(fee).toLocaleString("en-IN", {
    maximumFractionDigits: 2,
    minimumFractionDigits: Number(fee) % 1 === 0 ? 0 : 2,
  })}`;
}

export function initials(name: string): string {
  const parts = name.trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) return "?";
  return parts
    .slice(0, 2)
    .map((p) => p.charAt(0).toUpperCase())
    .join("");
}

const AVATAR_COLORS = [
  "bg-teal-600",
  "bg-cyan-700",
  "bg-emerald-600",
  "bg-sky-700",
  "bg-indigo-600",
  "bg-violet-600",
  "bg-rose-600",
  "bg-amber-600",
] as const;

export function avatarColor(id: number): string {
  const index = Math.abs(id) % AVATAR_COLORS.length;
  return AVATAR_COLORS[index] ?? "bg-teal-600";
}

export function isValidEmail(value: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
}

export function roomNameFromLiveKitData(liveKitData: string | null | undefined, slotId: number): string {
  if (liveKitData) {
    try {
      const parsed: unknown = JSON.parse(liveKitData);
      if (
        parsed &&
        typeof parsed === "object" &&
        "room" in parsed &&
        typeof (parsed as { room: unknown }).room === "string"
      ) {
        return (parsed as { room: string }).room;
      }
    } catch {
      if (liveKitData.trim()) return liveKitData.trim();
    }
  }
  return `slot-${slotId}`;
}

export function toLocalDateTimePayload(date: Date): string {
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
}
