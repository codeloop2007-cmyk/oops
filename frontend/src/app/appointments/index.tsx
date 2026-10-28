import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Pressable,
  ActivityIndicator,
  Modal,
  TextInput,
} from "react-native";
import { useRouter } from "expo-router";
import { AppHeader } from "@/components/AppHeader";
import { AppNavigation } from "@/components/AppNavigation";
import { useSession } from "@/context/SessionContext";
import {
  getAppointments,
  cancelAppointment,
  createReview,
} from "@/api/generated/mediConnectHealthcareAPI";
import type {
  AppointmentResponse,
  CreateReviewRequest,
} from "@/api/generated/model";

export default function AppointmentsScreen() {
  const router = useRouter();
  const { user, isRegistered } = useSession();

  const [appointments, setAppointments] = useState<AppointmentResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState<string>("ALL");

  // Review Modal State
  const [reviewAppt, setReviewAppt] = useState<AppointmentResponse | null>(null);
  const [rating, setRating] = useState<number>(5);
  const [reviewComment, setReviewComment] = useState<string>("");
  const [reviewSubmitting, setReviewSubmitting] = useState<boolean>(false);

  const fetchAppointments = async () => {
    setLoading(true);
    setError(null);
    try {
      const patientId = user?.userId;
      const status = statusFilter !== "ALL" ? statusFilter : undefined;
      const data = await getAppointments({
        patientId,
        status,
      });
      setAppointments(data || []);
    } catch (err: any) {
      setError(err?.message || "Failed to load appointments.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAppointments();
  }, [user?.userId, statusFilter]);

  const handleCancelAppointment = async (apptId: number) => {
    setError(null);
    setSuccessMsg(null);
    try {
      await cancelAppointment(apptId);
      setSuccessMsg(`Appointment #${apptId} has been cancelled.`);
      fetchAppointments();
    } catch (err: any) {
      setError(err?.message || "Failed to cancel appointment.");
    }
  };

  const handleSubmitReview = async () => {
    if (!reviewAppt || !reviewAppt.appointmentId) return;
    setReviewSubmitting(true);
    setError(null);
    try {
      const req: CreateReviewRequest = {
        appointmentId: reviewAppt.appointmentId,
        rating,
        comment: reviewComment.trim() || undefined,
      };
      await createReview(req);
      setSuccessMsg(`Feedback submitted for Appointment #${reviewAppt.appointmentId}!`);
      setReviewAppt(null);
      setReviewComment("");
    } catch (err: any) {
      setError(err?.message || "Failed to submit review.");
    } finally {
      setReviewSubmitting(false);
    }
  };

  const handleLaunchVideo = (appt: AppointmentResponse) => {
    router.push(`/video-demo?appointmentId=${appt.appointmentId}&doctorUserId=${appt.doctorUserId}` as any);
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <AppHeader
        title="MediConnect"
        subtitle="Manage Consultations, Live Telehealth Calls & Feedback"
      />
      <AppNavigation />

      {/* Top Banner & Filter Chips */}
      <View style={styles.topCard}>
        <View style={styles.topRow}>
          <Text style={styles.title}>My Scheduled Consultations</Text>
          <Pressable
            style={styles.bookNewBtn}
            onPress={() => router.push("/slots" as any)}
          >
            <Text style={styles.bookNewBtnText}>+ Book Appointment</Text>
          </Pressable>
        </View>

        <Text style={styles.subtitle}>
          Track your consultation statuses, launch secure LiveKit telehealth calls, and leave feedback.
        </Text>

        <View style={styles.filterTabs}>
          {["ALL", "CONFIRMED", "COMPLETED", "CANCELLED"].map((st) => (
            <Pressable
              key={st}
              style={[styles.filterTab, statusFilter === st && styles.filterTabActive]}
              onPress={() => setStatusFilter(st)}
            >
              <Text
                style={[
                  styles.filterTabText,
                  statusFilter === st && styles.filterTabTextActive,
                ]}
              >
                {st}
              </Text>
            </Pressable>
          ))}
        </View>
      </View>

      {/* Status Messages */}
      {error ? (
        <View style={styles.errorBox}>
          <Text style={styles.errorText}>⚠️ {error}</Text>
        </View>
      ) : null}

      {successMsg ? (
        <View style={styles.successBox}>
          <Text style={styles.successText}>✅ {successMsg}</Text>
        </View>
      ) : null}

      {/* Appointments List */}
      {loading ? (
        <View style={styles.centerBox}>
          <ActivityIndicator size="large" color="#0284C7" />
          <Text style={styles.loadingText}>Loading your appointments...</Text>
        </View>
      ) : appointments.length === 0 ? (
        <View style={styles.emptyBox}>
          <Text style={styles.emptyIcon}>📅</Text>
          <Text style={styles.emptyTitle}>No appointments found</Text>
          <Text style={styles.emptySubtitle}>
            You do not have any scheduled appointments under this filter. Browse open doctor slots to book your next consultation.
          </Text>
          <Pressable
            style={styles.browseSlotsBtn}
            onPress={() => router.push("/slots" as any)}
          >
            <Text style={styles.browseSlotsBtnText}>Find Available Slots →</Text>
          </Pressable>
        </View>
      ) : (
        <View style={styles.appointmentsGrid}>
          {appointments.map((appt) => {
            const isConfirmed = appt.status === "CONFIRMED";
            const isCompleted = appt.status === "COMPLETED";
            const isCancelled = appt.status === "CANCELLED";

            return (
              <View key={appt.appointmentId} style={styles.apptCard}>
                <View style={styles.cardHeader}>
                  <View style={styles.headerLeft}>
                    <Text style={styles.cardId}>Appointment #{appt.appointmentId}</Text>
                    <Text style={styles.cardDoc}>Doctor #{appt.doctorUserId}</Text>
                  </View>
                  <View
                    style={[
                      styles.statusPill,
                      isConfirmed
                        ? styles.statusConfirmed
                        : isCompleted
                        ? styles.statusCompleted
                        : styles.statusCancelled,
                    ]}
                  >
                    <Text
                      style={[
                        styles.statusPillText,
                        isConfirmed
                          ? styles.statusConfirmedText
                          : isCompleted
                          ? styles.statusCompletedText
                          : styles.statusCancelledText,
                      ]}
                    >
                      {appt.status}
                    </Text>
                  </View>
                </View>

                <View style={styles.detailsRow}>
                  <Text style={styles.detailText}>
                    📹 Mode: <Text style={styles.bold}>{appt.consultationMode}</Text>
                  </Text>
                  <Text style={styles.detailText}>
                    Slot: <Text style={styles.bold}>#{appt.slotId}</Text>
                  </Text>
                </View>

                {appt.reason ? (
                  <View style={styles.reasonBox}>
                    <Text style={styles.reasonLabel}>Reason for visit:</Text>
                    <Text style={styles.reasonText}>{appt.reason}</Text>
                  </View>
                ) : null}

                <View style={styles.cardActions}>
                  {/* Join Video Call Button */}
                  {isConfirmed && appt.consultationMode === "VIDEO" ? (
                    <Pressable
                      style={styles.joinVideoBtn}
                      onPress={() => handleLaunchVideo(appt)}
                    >
                      <Text style={styles.joinVideoBtnText}>📹 Join LiveKit Video Call</Text>
                    </Pressable>
                  ) : null}

                  {/* Cancel Button */}
                  {isConfirmed && appt.appointmentId ? (
                    <Pressable
                      style={styles.cancelBtn}
                      onPress={() => handleCancelAppointment(appt.appointmentId!)}
                    >
                      <Text style={styles.cancelBtnText}>Cancel</Text>
                    </Pressable>
                  ) : null}

                  {/* Review Button */}
                  {isCompleted ? (
                    <Pressable
                      style={styles.reviewBtn}
                      onPress={() => setReviewAppt(appt)}
                    >
                      <Text style={styles.reviewBtnText}>⭐ Leave Review</Text>
                    </Pressable>
                  ) : null}
                </View>
              </View>
            );
          })}
        </View>
      )}

      {/* Review Modal */}
      <Modal
        visible={!!reviewAppt}
        transparent
        animationType="fade"
        onRequestClose={() => setReviewAppt(null)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <Text style={styles.modalTitle}>Rate Your Consultation</Text>
            {reviewAppt ? (
              <Text style={styles.modalSubtitle}>
                Appointment #{reviewAppt.appointmentId} with Doctor #{reviewAppt.doctorUserId}
              </Text>
            ) : null}

            <Text style={styles.modalFieldLabel}>Rating (1 to 5 Stars):</Text>
            <View style={styles.ratingRow}>
              {[1, 2, 3, 4, 5].map((star) => (
                <Pressable key={star} onPress={() => setRating(star)}>
                  <Text style={[styles.starIcon, rating >= star && styles.starIconActive]}>
                    ⭐
                  </Text>
                </Pressable>
              ))}
            </View>

            <Text style={styles.modalFieldLabel}>Feedback & Comments:</Text>
            <TextInput
              style={styles.modalTextInput}
              value={reviewComment}
              onChangeText={setReviewComment}
              placeholder="How was your consultation experience?"
              multiline
              numberOfLines={3}
            />

            <View style={styles.modalBtnRow}>
              <Pressable
                style={styles.modalCancelBtn}
                onPress={() => setReviewAppt(null)}
                disabled={reviewSubmitting}
              >
                <Text style={styles.modalCancelBtnText}>Cancel</Text>
              </Pressable>

              <Pressable
                style={styles.modalSubmitBtn}
                onPress={handleSubmitReview}
                disabled={reviewSubmitting}
              >
                {reviewSubmitting ? (
                  <ActivityIndicator color="#ffffff" size="small" />
                ) : (
                  <Text style={styles.modalSubmitBtnText}>Submit Feedback</Text>
                )}
              </Pressable>
            </View>
          </View>
        </View>
      </Modal>
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
  topCard: {
    marginHorizontal: 16,
    marginBottom: 16,
    padding: 20,
    backgroundColor: "#ffffff",
    borderRadius: 16,
    borderWidth: 1,
    borderColor: "#E2E8F0",
  },
  topRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 6,
  },
  title: {
    fontSize: 18,
    fontWeight: "800",
    color: "#0F172A",
  },
  bookNewBtn: {
    backgroundColor: "#0284C7",
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 8,
  },
  bookNewBtnText: {
    color: "#ffffff",
    fontSize: 12,
    fontWeight: "700",
  },
  subtitle: {
    fontSize: 13,
    color: "#64748B",
    marginBottom: 14,
    lineHeight: 18,
  },
  filterTabs: {
    flexDirection: "row",
    gap: 8,
  },
  filterTab: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 8,
    backgroundColor: "#F1F5F9",
  },
  filterTabActive: {
    backgroundColor: "#0284C7",
  },
  filterTabText: {
    fontSize: 12,
    fontWeight: "700",
    color: "#475569",
  },
  filterTabTextActive: {
    color: "#ffffff",
  },
  errorBox: {
    marginHorizontal: 16,
    marginBottom: 16,
    padding: 12,
    backgroundColor: "#FEF2F2",
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "#FCA5A5",
  },
  errorText: {
    color: "#B91C1C",
    fontSize: 13,
  },
  successBox: {
    marginHorizontal: 16,
    marginBottom: 16,
    padding: 12,
    backgroundColor: "#ECFDF5",
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "#A7F3D0",
  },
  successText: {
    color: "#047857",
    fontSize: 13,
    fontWeight: "600",
  },
  centerBox: {
    padding: 40,
    alignItems: "center",
  },
  loadingText: {
    marginTop: 12,
    fontSize: 13,
    color: "#64748B",
  },
  emptyBox: {
    marginHorizontal: 16,
    padding: 36,
    backgroundColor: "#ffffff",
    borderRadius: 16,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    alignItems: "center",
  },
  emptyIcon: {
    fontSize: 36,
    marginBottom: 10,
  },
  emptyTitle: {
    fontSize: 16,
    fontWeight: "800",
    color: "#0F172A",
    marginBottom: 6,
  },
  emptySubtitle: {
    fontSize: 13,
    color: "#64748B",
    textAlign: "center",
    lineHeight: 18,
    marginBottom: 16,
  },
  browseSlotsBtn: {
    backgroundColor: "#0284C7",
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 8,
  },
  browseSlotsBtnText: {
    color: "#ffffff",
    fontWeight: "700",
    fontSize: 13,
  },
  appointmentsGrid: {
    marginHorizontal: 16,
    gap: 12,
  },
  apptCard: {
    backgroundColor: "#ffffff",
    borderRadius: 14,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    padding: 16,
  },
  cardHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "flex-start",
    marginBottom: 10,
  },
  headerLeft: {
    flex: 1,
  },
  cardId: {
    fontSize: 15,
    fontWeight: "800",
    color: "#0F172A",
  },
  cardDoc: {
    fontSize: 13,
    fontWeight: "700",
    color: "#0284C7",
    marginTop: 2,
  },
  statusPill: {
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 6,
  },
  statusConfirmed: {
    backgroundColor: "#DCFCE7",
  },
  statusCompleted: {
    backgroundColor: "#E0F2FE",
  },
  statusCancelled: {
    backgroundColor: "#FEE2E2",
  },
  statusPillText: {
    fontSize: 11,
    fontWeight: "700",
  },
  statusConfirmedText: {
    color: "#166534",
  },
  statusCompletedText: {
    color: "#0369A1",
  },
  statusCancelledText: {
    color: "#991B1B",
  },
  detailsRow: {
    flexDirection: "row",
    gap: 16,
    marginBottom: 8,
  },
  detailText: {
    fontSize: 12,
    color: "#475569",
  },
  bold: {
    fontWeight: "700",
    color: "#0F172A",
  },
  reasonBox: {
    backgroundColor: "#F8FAFC",
    padding: 10,
    borderRadius: 8,
    marginBottom: 12,
  },
  reasonLabel: {
    fontSize: 11,
    fontWeight: "600",
    color: "#64748B",
    marginBottom: 2,
  },
  reasonText: {
    fontSize: 12,
    color: "#1E293B",
  },
  cardActions: {
    flexDirection: "row",
    gap: 8,
    marginTop: 6,
  },
  joinVideoBtn: {
    flex: 2,
    backgroundColor: "#16A34A",
    paddingVertical: 10,
    borderRadius: 8,
    alignItems: "center",
  },
  joinVideoBtnText: {
    color: "#ffffff",
    fontWeight: "700",
    fontSize: 13,
  },
  cancelBtn: {
    flex: 1,
    backgroundColor: "#FEE2E2",
    paddingVertical: 10,
    borderRadius: 8,
    alignItems: "center",
  },
  cancelBtnText: {
    color: "#DC2626",
    fontWeight: "700",
    fontSize: 12,
  },
  reviewBtn: {
    flex: 1,
    backgroundColor: "#FEF3C7",
    paddingVertical: 10,
    borderRadius: 8,
    alignItems: "center",
  },
  reviewBtnText: {
    color: "#92400E",
    fontWeight: "700",
    fontSize: 12,
  },
  modalOverlay: {
    flex: 1,
    backgroundColor: "rgba(15, 23, 42, 0.6)",
    justifyContent: "center",
    padding: 20,
  },
  modalContent: {
    backgroundColor: "#ffffff",
    borderRadius: 16,
    padding: 24,
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: "800",
    color: "#0F172A",
    marginBottom: 4,
  },
  modalSubtitle: {
    fontSize: 13,
    color: "#64748B",
    marginBottom: 16,
  },
  modalFieldLabel: {
    fontSize: 12,
    fontWeight: "700",
    color: "#334155",
    marginBottom: 6,
  },
  ratingRow: {
    flexDirection: "row",
    gap: 8,
    marginBottom: 16,
  },
  starIcon: {
    fontSize: 28,
    opacity: 0.4,
  },
  starIconActive: {
    opacity: 1,
  },
  modalTextInput: {
    backgroundColor: "#F8FAFC",
    borderWidth: 1,
    borderColor: "#CBD5E1",
    borderRadius: 8,
    padding: 10,
    fontSize: 13,
    color: "#0F172A",
    minHeight: 70,
    textAlignVertical: "top",
    marginBottom: 20,
  },
  modalBtnRow: {
    flexDirection: "row",
    gap: 10,
  },
  modalCancelBtn: {
    flex: 1,
    paddingVertical: 12,
    backgroundColor: "#F1F5F9",
    borderRadius: 8,
    alignItems: "center",
  },
  modalCancelBtnText: {
    color: "#475569",
    fontWeight: "700",
  },
  modalSubmitBtn: {
    flex: 1.5,
    paddingVertical: 12,
    backgroundColor: "#0284C7",
    borderRadius: 8,
    alignItems: "center",
  },
  modalSubmitBtnText: {
    color: "#ffffff",
    fontWeight: "700",
  },
});

