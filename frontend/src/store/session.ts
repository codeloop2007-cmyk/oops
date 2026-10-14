import { create } from "zustand";
import { persist } from "zustand/middleware";
import { SESSION_STORAGE_KEY } from "@/lib/constants";

export type Session =
  | { role: "patient"; userId: number; userName: string; userPublicId: string }
  | { role: "doctor"; doctorId: number; doctorName: string }
  | null;

type SessionState = {
  session: Session;
  setSession: (session: Session) => void;
  clearSession: () => void;
};

export const useSessionStore = create<SessionState>()(
  persist(
    (set) => ({
      session: null,
      setSession: (session) => set({ session }),
      clearSession: () => set({ session: null }),
    }),
    {
      name: SESSION_STORAGE_KEY,
      storage: {
        getItem: (name) => {
          const raw = localStorage.getItem(name);
          if (!raw) return null;
          try {
            const parsed: unknown = JSON.parse(raw);
            if (parsed && typeof parsed === "object" && "state" in parsed) {
              return parsed as { state: SessionState; version: number };
            }
            return {
              state: { session: parsed as Session } as SessionState,
              version: 0,
            };
          } catch {
            return null;
          }
        },
        setItem: (name, value) => {
          localStorage.setItem(name, JSON.stringify(value.state.session));
        },
        removeItem: (name) => {
          localStorage.removeItem(name);
        },
      },
    },
  ),
);
