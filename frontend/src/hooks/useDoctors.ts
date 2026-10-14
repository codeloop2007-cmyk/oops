import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  createDoctor,
  deleteDoctor,
  getDoctorById,
  getDoctors,
  searchDoctors,
  updateDoctor,
} from "@/lib/api";
import { QUERY_STALE_TIME } from "@/lib/constants";
import type { DoctorRequest } from "@/types/api";

export const doctorKeys = {
  all: ["doctors"] as const,
  detail: (id: number) => ["doctors", id] as const,
  search: (q: string, limit: number) => ["doctors", "search", q, limit] as const,
};

export function useDoctors() {
  return useQuery({
    queryKey: doctorKeys.all,
    queryFn: getDoctors,
    staleTime: QUERY_STALE_TIME,
  });
}

export function useDoctor(id: number | undefined) {
  return useQuery({
    queryKey: doctorKeys.detail(id ?? 0),
    queryFn: () => getDoctorById(id as number),
    enabled: typeof id === "number" && id > 0,
    staleTime: QUERY_STALE_TIME,
  });
}

export function useSearchDoctors(q: string, limit = 12) {
  const trimmed = q.trim();
  return useQuery({
    queryKey: doctorKeys.search(trimmed, limit),
    queryFn: () => searchDoctors(trimmed, limit),
    enabled: trimmed.length >= 2,
    staleTime: QUERY_STALE_TIME,
  });
}

export function useCreateDoctor() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (body: DoctorRequest) => createDoctor(body),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: doctorKeys.all });
    },
  });
}

export function useUpdateDoctor() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, body }: { id: number; body: DoctorRequest }) =>
      updateDoctor(id, body),
    onSuccess: (_data, vars) => {
      void queryClient.invalidateQueries({ queryKey: doctorKeys.all });
      void queryClient.invalidateQueries({ queryKey: doctorKeys.detail(vars.id) });
    },
  });
}

export function useDeleteDoctor() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => deleteDoctor(id),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: doctorKeys.all });
    },
  });
}
