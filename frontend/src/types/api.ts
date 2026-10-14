export type UserResponse = {
  id: number;
  userId: string;
  name: string;
  gmail: string;
};

export type UserRequest = {
  userId: string;
  name: string;
  gmail: string;
};

export type DoctorResponse = {
  doctorId: number;
  name: string;
  specialization: string;
  qualification?: string | null;
  language?: string | null;
  location?: string | null;
  consultationFee: number;
};

export type DoctorRequest = {
  name: string;
  specialization: string;
  qualification?: string;
  language?: string;
  location?: string;
  consultationFee: number;
};

export type SlotResponse = {
  slotId: number;
  time: string;
  doctorId: number;
  booked: boolean;
  bookedUserId?: number | null;
  liveKitData?: string | null;
};

export type SlotRequest = {
  time: string;
  doctorId: number;
};

export type BookSlotRequest = {
  bookedUserId: number;
  liveKitData: string;
};

export type AgentRequest = {
  query: string;
};

export type AgentResponse = {
  summary: string;
  doctorIds: number[];
  doctors: DoctorResponse[];
};

export type LiveKitTokenRequest = {
  room: string;
  identity: string;
  name?: string;
};

export type LiveKitTokenResponse = {
  token: string;
  url?: string | null;
};
