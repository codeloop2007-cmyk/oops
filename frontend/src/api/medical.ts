/**
 * Medical & Healthcare API endpoints
 * Backed by Orval generated endpoints
 */
import {
  searchDoctors,
  getAllDoctorProfiles,
  getDoctorProfileById,
  getSlots,
  createSlot,
  getAppointments,
  getAppointmentById,
  createAppointment,
  cancelAppointment,
  getVideoSessionByAppointmentId,
  createVideoSession,
  updateVideoSession,
  getReviews,
  createReview,
  ask1,
  ask,
} from "@/api/generated/mediConnectHealthcareAPI";
import type {
  DoctorSearchResponse,
  DoctorProfileResponse,
  SlotResponse,
  CreateSlotRequest,
  AppointmentResponse,
  CreateAppointmentRequest,
  VideoSessionResponse,
  CreateVideoSessionRequest,
  UpdateVideoSessionRequest,
  ReviewResponse,
  CreateReviewRequest,
  AgentResponse,
  AgentRequest,
  SearchDoctorsParams,
} from "@/api/generated/model";

export type {
  DoctorSearchResponse as DoctorSearchItem,
  DoctorProfileResponse as DoctorProfile,
  SlotResponse as Slot,
  CreateSlotRequest,
  AppointmentResponse as Appointment,
  CreateAppointmentRequest,
  VideoSessionResponse as VideoSession,
  CreateVideoSessionRequest,
  ReviewResponse as Review,
  CreateReviewRequest,
  AgentResponse,
};

export const medicalApi = {
  searchDoctors: async (params: SearchDoctorsParams): Promise<DoctorSearchResponse[]> => {
    return searchDoctors(params);
  },

  getAllDoctors: async (): Promise<DoctorProfileResponse[]> => {
    return getAllDoctorProfiles();
  },

  getDoctorById: async (id: number): Promise<DoctorProfileResponse> => {
    return getDoctorProfileById(id);
  },

  getSlots: async (doctorId?: number, isBookable?: boolean): Promise<SlotResponse[]> => {
    return getSlots({ doctorId, isBookable });
  },

  createSlot: async (data: CreateSlotRequest): Promise<SlotResponse> => {
    return createSlot(data);
  },

  getAppointments: async (params?: {
    patientId?: number;
    doctorId?: number;
    status?: string;
  }): Promise<AppointmentResponse[]> => {
    return getAppointments(params);
  },

  getAppointmentById: async (id: number): Promise<AppointmentResponse> => {
    return getAppointmentById(id);
  },

  createAppointment: async (data: CreateAppointmentRequest): Promise<AppointmentResponse> => {
    return createAppointment(data);
  },

  cancelAppointment: async (id: number): Promise<AppointmentResponse> => {
    return cancelAppointment(id);
  },

  getVideoSessionByAppointmentId: async (appointmentId: number): Promise<VideoSessionResponse> => {
    return getVideoSessionByAppointmentId(appointmentId);
  },

  createVideoSession: async (data: CreateVideoSessionRequest): Promise<VideoSessionResponse> => {
    return createVideoSession(data);
  },

  updateVideoSession: async (id: number, data: UpdateVideoSessionRequest): Promise<VideoSessionResponse> => {
    return updateVideoSession(id, data);
  },

  getReviews: async (doctorId?: number, patientId?: number): Promise<ReviewResponse[]> => {
    return getReviews({ doctorId, patientId });
  },

  createReview: async (data: CreateReviewRequest): Promise<ReviewResponse> => {
    return createReview(data);
  },

  askAiAgent: async (query: string): Promise<AgentResponse> => {
    try {
      return await ask1({ query });
    } catch {
      return await ask({ query });
    }
  },
};
