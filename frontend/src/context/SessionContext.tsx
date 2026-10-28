import React, { createContext, useContext, useEffect, useState, useCallback } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { setClientIdentityId } from "@/api/axios";
import { getCurrentUser, registerUser } from "@/api/generated/mediConnectHealthcareAPI";
import type { UserResponse, RegisterUserRequest } from "@/api/generated/model";

const STORAGE_KEY_CLIENT_ID = "mediconnect_client_identity_id";

export interface SessionContextType {
  clientIdentityId: string | null;
  user: UserResponse | null;
  isLoading: boolean;
  isRegistered: boolean;
  error: string | null;
  setIdentity: (id: string) => Promise<void>;
  generateNewIdentity: () => Promise<string>;
  refreshSession: () => Promise<void>;
  registerProfile: (data: RegisterUserRequest) => Promise<UserResponse>;
  logout: () => Promise<void>;
}

const SessionContext = createContext<SessionContextType | undefined>(undefined);

// Helper to generate a valid RFC4122 v4 UUID
export const generateUUID = (): string => {
  return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0;
    const v = c === "x" ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
};

export const SessionProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [clientIdentityId, setClientIdentityIdState] = useState<string | null>(null);
  const [user, setUser] = useState<UserResponse | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const syncWithBackend = useCallback(async (uuid: string) => {
    setIsLoading(true);
    setError(null);
    try {
      setClientIdentityId(uuid);
      const res = await getCurrentUser();
      setUser(res);
      return res;
    } catch (err: any) {
      setError(err?.message || "Failed to synchronize identity with backend.");
      setUser(null);
      throw err;
    } finally {
      setIsLoading(false);
    }
  }, []);

  // Initialize identity from storage or generate default
  useEffect(() => {
    const initSession = async () => {
      try {
        const storedId = await AsyncStorage.getItem(STORAGE_KEY_CLIENT_ID);
        const activeId = storedId || generateUUID();
        if (!storedId) {
          await AsyncStorage.setItem(STORAGE_KEY_CLIENT_ID, activeId);
        }
        setClientIdentityIdState(activeId);
        await syncWithBackend(activeId);
      } catch (e: any) {
        console.warn("Session initialization warning:", e?.message);
        setIsLoading(false);
      }
    };

    initSession();
  }, [syncWithBackend]);

  const setIdentity = async (newId: string) => {
    const trimmed = newId.trim();
    if (!trimmed) return;
    await AsyncStorage.setItem(STORAGE_KEY_CLIENT_ID, trimmed);
    setClientIdentityIdState(trimmed);
    await syncWithBackend(trimmed);
  };

  const generateNewIdentity = async (): Promise<string> => {
    const newId = generateUUID();
    await setIdentity(newId);
    return newId;
  };

  const refreshSession = async () => {
    if (clientIdentityId) {
      await syncWithBackend(clientIdentityId);
    }
  };

  const registerProfile = async (data: RegisterUserRequest): Promise<UserResponse> => {
    setIsLoading(true);
    setError(null);
    try {
      const res = await registerUser(data);
      setUser(res);
      return res;
    } catch (err: any) {
      setError(err?.message || "Registration failed.");
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = async () => {
    await AsyncStorage.removeItem(STORAGE_KEY_CLIENT_ID);
    setClientIdentityId(null);
    setClientIdentityIdState(null);
    setUser(null);
    setError(null);
  };

  const isRegistered = user?.registrationStatus === "COMPLETED";

  return (
    <SessionContext.Provider
      value={{
        clientIdentityId,
        user,
        isLoading,
        isRegistered,
        error,
        setIdentity,
        generateNewIdentity,
        refreshSession,
        registerProfile,
        logout,
      }}
    >
      {children}
    </SessionContext.Provider>
  );
};

export const useSession = (): SessionContextType => {
  const context = useContext(SessionContext);
  if (!context) {
    throw new Error("useSession must be used within a SessionProvider");
  }
  return context;
};

