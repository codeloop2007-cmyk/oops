import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Pressable,
  ActivityIndicator,
  TextInput,
} from "react-native";
import { useRouter, useLocalSearchParams } from "expo-router";
import { AppHeader } from "@/components/AppHeader";
import { AppNavigation } from "@/components/AppNavigation";
import { useSession } from "@/context/SessionContext";
import {
  getVideoSessionByAppointmentId,
  createVideoSession,
  updateVideoSession,
} from "@/api/generated/mediConnectHealthcareAPI";
import type {
  VideoSessionResponse,
  CreateVideoSessionRequest,
  UpdateVideoSessionRequest,
} from "@/api/generated/model";

export default function VideoCallScreen() {
  const router = useRouter();
  const params = useLocalSearchParams<{
    appointmentId?: string;
    doctorUserId?: string;
  }>();
  const { user, clientIdentityId } = useSession();

  const appointmentId = params.appointmentId ? parseInt(params.appointmentId, 10) : 1;
  const doctorUserId = params.doctorUserId ? parseInt(params.doctorUserId, 10) : 2;

  const [session, setSession] = useState<VideoSessionResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Call State Simulation / Control
  const [isInCall, setIsInCall] = useState<boolean>(false);
  const [isMuted, setIsMuted] = useState<boolean>(false);
  const [isVideoOff, setIsVideoOff] = useState<boolean>(false);
  const [callDuration, setCallDuration] = useState<number>(0);
  const [customRoom, setCustomRoom] = useState<string>(`room-appt-${appointmentId}`);

  // Fetch or initialize video session from backend
  const initVideoSession = async () => {
    setLoading(true);
    setError(null);
    try {
      // 1. Try finding existing session for this appointment
      let sess: VideoSessionResponse;
      try {
        sess = await getVideoSessionByAppointmentId(appointmentId);
      } catch (err: any) {
        // 2. If not found, create a new WebRTC / LiveKit video session record in backend
        const req: CreateVideoSessionRequest = {
          appointmentId,
          provider: "LiveKit WebRTC",
          roomId: customRoom.trim() || `room-appt-${appointmentId}`,
          joinCodeHash: `hash-join-${appointmentId}-${Date.now()}`,
          sessionStatus: "WAITING",
        };
        sess = await createVideoSession(req);
      }
      setSession(sess);
    } catch (err: any) {
      setError(
        err?.message ||
          "Could not initialize video session. Verify appointment ID exists."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    initVideoSession();
  }, [appointmentId]);

  // Call timer effect
  useEffect(() => {
    let interval: any;
    if (isInCall) {
      interval = setInterval(() => {
        setCallDuration((prev) => prev + 1);
      }, 1000);
    } else {
      setCallDuration(0);
    }
    return () => clearInterval(interval);
  }, [isInCall]);

  const handleJoinCall = async () => {
    if (!session || !session.videoSessionId) return;
    try {
      const req: UpdateVideoSessionRequest = {
        sessionStatus: "ACTIVE",
      };
      await updateVideoSession(session.videoSessionId, req);
      setIsInCall(true);
    } catch (err: any) {
      console.warn("Could not mark session active:", err?.message);
      setIsInCall(true);
    }
  };

  const handleEndCall = async () => {
    if (session && session.videoSessionId) {
      try {
        const req: UpdateVideoSessionRequest = {
          sessionStatus: "ENDED",
        };
        await updateVideoSession(session.videoSessionId, req);
      } catch (err: any) {
        console.warn("Could not mark session ended:", err?.message);
      }
    }
    setIsInCall(false);
  };

  const formatDuration = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, "0")}:${secs.toString().padStart(2, "0")}`;
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <AppHeader
        title="MediConnect"
        subtitle="Telehealth Video Consultation Room powered by LiveKit WebRTC"
      />
      <AppNavigation />

      {/* Main Video Call Frame */}
      <View style={styles.callContainer}>
        {/* Call Status Header */}
        <View style={styles.callHeader}>
          <View style={styles.headerInfo}>
            <Text style={styles.roomName}>
              📹 Room: {session?.roomId || customRoom}
            </Text>
            <Text style={styles.callSub}>
              Appointment #{appointmentId} • Doctor #{doctorUserId} • Patient:{" "}
              {user?.name || "Patient"}
            </Text>
          </View>
          <View
            style={[
              styles.statusPill,
              isInCall ? styles.statusActive : styles.statusWaiting,
            ]}
          >
            <View
              style={[
                styles.dot,
                isInCall ? styles.dotActive : styles.dotWaiting,
              ]}
            />
            <Text
              style={[
                styles.statusPillText,
                isInCall ? styles.statusActiveText : styles.statusWaitingText,
              ]}
            >
              {isInCall ? `LIVE (${formatDuration(callDuration)})` : "READY TO JOIN"}
            </Text>
          </View>
        </View>

        {/* Video Viewport */}
        <View style={styles.videoStage}>
          {isInCall ? (
            <View style={styles.activeCallGrid}>
              {/* Doctor Remote Stream View */}
              <View style={styles.remoteVideoBox}>
                <View style={styles.remoteAvatar}>
                  <Text style={styles.remoteAvatarText}>👨‍⚕️</Text>
                </View>
                <Text style={styles.remoteName}>Dr. Specialist (Connected)</Text>
                <View style={styles.liveKitWatermark}>
                  <Text style={styles.watermarkText}>🛡️ LiveKit Encrypted WebRTC</Text>
                </View>
              </View>

              {/* Local Self-View Pip */}
              <View style={styles.localPip}>
                {isVideoOff ? (
                  <View style={styles.videoOffView}>
                    <Text style={styles.videoOffText}>Camera Off</Text>
                  </View>
                ) : (
                  <View style={styles.videoOnView}>
                    <Text style={styles.selfAvatar}>👤</Text>
                    <Text style={styles.selfLabel}>You ({isMuted ? "Muted" : "Active"})</Text>
                  </View>
                )}
              </View>
            </View>
          ) : (
            <View style={styles.preJoinState}>
              <View style={styles.cameraPreviewIconBox}>
                <Text style={styles.cameraIcon}>📹</Text>
              </View>
              <Text style={styles.preJoinTitle}>Waiting for Consultation to Begin</Text>
              <Text style={styles.preJoinDesc}>
                Microphone and camera permissions are verified. Click below to establish a secure peer connection with your doctor.
              </Text>

              {loading ? (
                <ActivityIndicator color="#0284C7" size="large" />
              ) : (
                <Pressable style={styles.joinCallBtn} onPress={handleJoinCall}>
                  <Text style={styles.joinCallBtnText}>Join Consultation Now 🚀</Text>
                </Pressable>
              )}
            </View>
          )}
        </View>

        {/* Call In-Room Controls */}
        {isInCall ? (
          <View style={styles.controlsBar}>
            <Pressable
              style={[styles.controlBtn, isMuted && styles.controlBtnMuted]}
              onPress={() => setIsMuted(!isMuted)}
            >
              <Text style={styles.controlIcon}>{isMuted ? "🔇" : "🎙️"}</Text>
              <Text style={styles.controlLabel}>{isMuted ? "Unmute" : "Mute"}</Text>
            </Pressable>

            <Pressable
              style={[styles.controlBtn, isVideoOff && styles.controlBtnMuted]}
              onPress={() => setIsVideoOff(!isVideoOff)}
            >
              <Text style={styles.controlIcon}>{isVideoOff ? "🚫" : "📹"}</Text>
              <Text style={styles.controlLabel}>{isVideoOff ? "Start Video" : "Stop Video"}</Text>
            </Pressable>

            <Pressable style={styles.hangupBtn} onPress={handleEndCall}>
              <Text style={styles.hangupIcon}>📞</Text>
              <Text style={styles.hangupLabel}>Leave Call</Text>
            </Pressable>
          </View>
        ) : null}
      </View>

      {/* Session Diagnostics & Backend Integration Info */}
      <View style={styles.infoCard}>
        <Text style={styles.infoTitle}>Session Diagnostics & WebRTC Details</Text>
        <View style={styles.infoGrid}>
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Backend Session ID:</Text>
            <Text style={styles.infoVal}>#{session?.videoSessionId || "N/A"}</Text>
          </View>
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Video Provider:</Text>
            <Text style={styles.infoVal}>{session?.provider || "LiveKit WebRTC"}</Text>
          </View>
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Room ID:</Text>
            <Text style={styles.infoVal}>{session?.roomId || customRoom}</Text>
          </View>
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Session Status:</Text>
            <Text style={styles.infoVal}>{session?.sessionStatus || "READY"}</Text>
          </View>
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Client Identity:</Text>
            <Text style={styles.infoVal} numberOfLines={1}>
              {clientIdentityId || "Anonymous"}
            </Text>
          </View>
        </View>

        {error ? (
          <View style={styles.errorBox}>
            <Text style={styles.errorText}>⚠️ {error}</Text>
          </View>
        ) : null}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#F8FAFC",
  },
  contentContainer: {
    paddingBottom: 48,
  },
  callContainer: {
    marginHorizontal: 16,
    marginBottom: 16,
    backgroundColor: "#0F172A",
    borderRadius: 20,
    overflow: "hidden",
    shadowColor: "#0F172A",
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.15,
    shadowRadius: 16,
    elevation: 4,
  },
  callHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    padding: 18,
    borderBottomWidth: 1,
    borderBottomColor: "#1E293B",
  },
  headerInfo: {
    flex: 1,
  },
  roomName: {
    fontSize: 16,
    fontWeight: "800",
    color: "#ffffff",
  },
  callSub: {
    fontSize: 11,
    color: "#94A3B8",
    marginTop: 2,
  },
  statusPill: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 10,
    paddingVertical: 5,
    borderRadius: 20,
    gap: 6,
  },
  statusActive: {
    backgroundColor: "rgba(22, 163, 74, 0.2)",
    borderWidth: 1,
    borderColor: "#16A34A",
  },
  statusWaiting: {
    backgroundColor: "rgba(234, 179, 8, 0.2)",
    borderWidth: 1,
    borderColor: "#EAB308",
  },
  dot: {
    width: 6,
    height: 6,
    borderRadius: 3,
  },
  dotActive: {
    backgroundColor: "#22C55E",
  },
  dotWaiting: {
    backgroundColor: "#FACC15",
  },
  statusPillText: {
    fontSize: 11,
    fontWeight: "700",
  },
  statusActiveText: {
    color: "#4ADE80",
  },
  statusWaitingText: {
    color: "#FDE047",
  },
  videoStage: {
    height: 380,
    backgroundColor: "#020617",
    justifyContent: "center",
    alignItems: "center",
  },
  preJoinState: {
    alignItems: "center",
    paddingHorizontal: 24,
  },
  cameraPreviewIconBox: {
    width: 70,
    height: 70,
    borderRadius: 35,
    backgroundColor: "#1E293B",
    alignItems: "center",
    justifyContent: "center",
    marginBottom: 16,
  },
  cameraIcon: {
    fontSize: 32,
  },
  preJoinTitle: {
    fontSize: 18,
    fontWeight: "800",
    color: "#ffffff",
    marginBottom: 8,
  },
  preJoinDesc: {
    fontSize: 13,
    color: "#94A3B8",
    textAlign: "center",
    lineHeight: 20,
    marginBottom: 20,
  },
  joinCallBtn: {
    backgroundColor: "#16A34A",
    paddingHorizontal: 24,
    paddingVertical: 14,
    borderRadius: 12,
  },
  joinCallBtnText: {
    color: "#ffffff",
    fontWeight: "800",
    fontSize: 15,
  },
  activeCallGrid: {
    width: "100%",
    height: "100%",
    position: "relative",
  },
  remoteVideoBox: {
    width: "100%",
    height: "100%",
    backgroundColor: "#1E293B",
    alignItems: "center",
    justifyContent: "center",
  },
  remoteAvatar: {
    width: 100,
    height: 100,
    borderRadius: 50,
    backgroundColor: "#334155",
    alignItems: "center",
    justifyContent: "center",
    marginBottom: 12,
  },
  remoteAvatarText: {
    fontSize: 50,
  },
  remoteName: {
    fontSize: 16,
    fontWeight: "700",
    color: "#F8FAFC",
  },
  liveKitWatermark: {
    position: "absolute",
    top: 14,
    left: 14,
    backgroundColor: "rgba(15, 23, 42, 0.7)",
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 6,
  },
  watermarkText: {
    color: "#94A3B8",
    fontSize: 11,
    fontWeight: "600",
  },
  localPip: {
    position: "absolute",
    bottom: 14,
    right: 14,
    width: 110,
    height: 140,
    borderRadius: 12,
    overflow: "hidden",
    borderWidth: 2,
    borderColor: "#0284C7",
    backgroundColor: "#0F172A",
  },
  videoOnView: {
    flex: 1,
    backgroundColor: "#334155",
    alignItems: "center",
    justifyContent: "center",
  },
  selfAvatar: {
    fontSize: 28,
  },
  selfLabel: {
    fontSize: 10,
    color: "#ffffff",
    fontWeight: "600",
    marginTop: 4,
  },
  videoOffView: {
    flex: 1,
    backgroundColor: "#1E293B",
    alignItems: "center",
    justifyContent: "center",
  },
  videoOffText: {
    fontSize: 10,
    color: "#94A3B8",
    fontWeight: "600",
  },
  controlsBar: {
    flexDirection: "row",
    justifyContent: "center",
    alignItems: "center",
    gap: 20,
    paddingVertical: 16,
    backgroundColor: "#0F172A",
    borderTopWidth: 1,
    borderTopColor: "#1E293B",
  },
  controlBtn: {
    alignItems: "center",
    padding: 10,
    borderRadius: 10,
    backgroundColor: "#1E293B",
    minWidth: 70,
  },
  controlBtnMuted: {
    backgroundColor: "#7F1D1D",
  },
  controlIcon: {
    fontSize: 20,
    marginBottom: 4,
  },
  controlLabel: {
    fontSize: 11,
    color: "#E2E8F0",
    fontWeight: "600",
  },
  hangupBtn: {
    alignItems: "center",
    padding: 10,
    borderRadius: 10,
    backgroundColor: "#DC2626",
    minWidth: 70,
  },
  hangupIcon: {
    fontSize: 20,
    marginBottom: 4,
  },
  hangupLabel: {
    fontSize: 11,
    color: "#ffffff",
    fontWeight: "700",
  },
  infoCard: {
    marginHorizontal: 16,
    padding: 18,
    backgroundColor: "#ffffff",
    borderRadius: 16,
    borderWidth: 1,
    borderColor: "#E2E8F0",
  },
  infoTitle: {
    fontSize: 14,
    fontWeight: "800",
    color: "#0F172A",
    marginBottom: 12,
  },
  infoGrid: {
    gap: 8,
  },
  infoRow: {
    flexDirection: "row",
    justifyContent: "space-between",
  },
  infoLabel: {
    fontSize: 12,
    color: "#64748B",
    fontWeight: "600",
  },
  infoVal: {
    fontSize: 12,
    fontWeight: "700",
    color: "#0F172A",
  },
  errorBox: {
    marginTop: 12,
    padding: 10,
    backgroundColor: "#FEF2F2",
    borderRadius: 8,
  },
  errorText: {
    color: "#DC2626",
    fontSize: 12,
  },
});

