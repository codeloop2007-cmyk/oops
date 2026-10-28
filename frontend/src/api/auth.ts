/**
 * Authentication API endpoints
 * Backed by Orval generated endpoints
 */
import {
  getCurrentUser,
  registerUser,
} from "@/api/generated/mediConnectHealthcareAPI";
import type { RegisterUserRequest, UserResponse } from "@/api/generated/model";

export const authApi = {
  getMe: async (): Promise<UserResponse> => {
    return getCurrentUser();
  },

  register: async (data: RegisterUserRequest): Promise<UserResponse> => {
    return registerUser(data);
  },
};

export { getCurrentUser, registerUser };
