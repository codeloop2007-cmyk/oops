import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  bookSlot,
  createSlot,
  deleteSlot,
  getAvailableSlotsByDoctor,
  getSlotById,
  getSlots,
  getSlotsByDoctor,
  updateSlot,
} from "@/lib/api";
import { QUERY_STALE_TIME } from "@/lib/constants";
import type { BookSlotRequest, SlotRequest } from "@/types/api";

export const slotKeys = {
  all: ["slots"] as const,
  detail: (id: number) => ["slots", id] as const,
  byDoctor: (doctorId: number) => ["slots", "doctor", doctorId] as const,
  available: (doctorId: number) => ["slots", "doctor", doctorId, "available"] as const,
};

function invalidateSlotQueries(queryClient: ReturnType<typeof useQueryClient>, doctorId?: number) {
  void queryClient.invalidateQueries({ queryKey: slotKeys.all });
  if (doctorId) {
    void queryClient.invalidateQueries({ queryKey: slotKeys.byDoctor(doctorId) });
    void queryClient.invalidateQueries({ queryKey: slotKeys.available(doctorId) });
  } else {
    void queryClient.invalidateQueries({ queryKey: ["slots"] });
  }
}

export function useSlots() {
  return useQuery({
    queryKey: slotKeys.all,
    queryFn: getSlots,
    staleTime: QUERY_STALE_TIME,
  });
}

export function useSlot(id: number | undefined) {
  return useQuery({
    queryKey: slotKeys.detail(id ?? 0),
    queryFn: () => getSlotById(id as number),
    enabled: typeof id === "number" && id > 0,
    staleTime: QUERY_STALE_TIME,
  });
}

export function useDoctorSlots(doctorId: number | undefined) {
  return useQuery({
    queryKey: slotKeys.byDoctor(doctorId ?? 0),
    queryFn: () => getSlotsByDoctor(doctorId as number),
    enabled: typeof doctorId === "number" && doctorId > 0,
    staleTime: QUERY_STALE_TIME,
  });
}

export function useAvailableSlots(doctorId: number | undefined) {
  return useQuery({
    queryKey: slotKeys.available(doctorId ?? 0),
    queryFn: () => getAvailableSlotsByDoctor(doctorId as number),
    enabled: typeof doctorId === "number" && doctorId > 0,
    staleTime: QUERY_STALE_TIME,
  });
}

export function useCreateSlot() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (body: SlotRequest) => createSlot(body),
    onSuccess: (data) => invalidateSlotQueries(queryClient, data.doctorId),
  });
}

export function useUpdateSlot() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, body }: { id: number; body: SlotRequest }) => updateSlot(id, body),
    onSuccess: (data) => invalidateSlotQueries(queryClient, data.doctorId),
  });
}

export function useDeleteSlot() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => deleteSlot(id),
    onSuccess: () => invalidateSlotQueries(queryClient),
  });
}

export function useBookSlot() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ slotId, body }: { slotId: number; body: BookSlotRequest }) =>
      bookSlot(slotId, body),
    onSuccess: (data) => {
      invalidateSlotQueries(queryClient, data.doctorId);
      void queryClient.invalidateQueries({ queryKey: slotKeys.detail(data.slotId) });
    },
  });
}
