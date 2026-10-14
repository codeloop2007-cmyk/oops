import { create } from "zustand";
import type { DoctorResponse } from "@/types/api";

export type AgentChatMessage = {
  role: "user" | "assistant";
  content: string;
  doctors?: DoctorResponse[];
};

type AgentChatState = {
  messages: AgentChatMessage[];
  appendMessage: (message: AgentChatMessage) => void;
  clear: () => void;
};

export const useAgentChatStore = create<AgentChatState>((set) => ({
  messages: [],
  appendMessage: (message) =>
    set((state) => ({ messages: [...state.messages, message] })),
  clear: () => set({ messages: [] }),
}));
