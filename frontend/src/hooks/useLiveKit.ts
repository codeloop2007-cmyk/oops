import { useMutation } from "@tanstack/react-query";
import { getLiveKitToken } from "@/lib/api";
import type { LiveKitTokenRequest } from "@/types/api";

export function useLiveKitToken() {
  return useMutation({
    mutationFn: (body: LiveKitTokenRequest) => getLiveKitToken(body),
  });
}
