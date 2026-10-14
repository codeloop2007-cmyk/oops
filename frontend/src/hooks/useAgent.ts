import { useMutation } from "@tanstack/react-query";
import { askAgent } from "@/lib/api";

export function useAskAgent() {
  return useMutation({
    mutationFn: (query: string) => askAgent(query),
  });
}
