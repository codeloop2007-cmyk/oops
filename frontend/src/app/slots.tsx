import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TextInput,
  Pressable,
  ActivityIndicator,
  Modal,
} from "react-native";
import { useRouter, useLocalSearchParams } from "expo-router";
import { AppHeader } from "@/components/AppHeader";
import { AppNavigation } from "@/components/AppNavigation";
import { useSession } from "@/context/SessionContext";
import {
  getSlots,
  createSlot,
  createAppointment,
} from "@/api/generated/mediConnectHealthcareAPI";
import type {
  SlotResponse,
  CreateAppointmentRequest,
  CreateSlotRequest,
} from "@/api/generated/model";

export default function SlotsScreen() {
  const router = useRouter();
  const params = useLocalSearchParams<{ doctorId?: string }>();
  const { user, isRegistered } = useSession();

  const [selectedDoctorId, setSelectedDoctorId] = useState<string>(params.doctorId || "");
  const [slots, setSlots] = useState<SlotResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  // Booking Modal State
  const [bookingSlot, setBookingSlot] = useState<SlotResponse | null>(null);
  const [consultationMode, setConsultationMode] = useState<"VIDEO" | "IN_PERSON">("VIDEO");
  const [reason, setReason] = useState<string>("");
  const [bookingInProgress, setBookingInProgress] = useState<boolean>(false);

  // New Slot Creator State (useful for testing doctor availability creation)
  const [showCreateSlot, setShowCreateSlot] = useState<boolean>(false);
  const [createDoctorId, setCreateDoctorId] = useState<string>(params.doctorId || "1");
  const [slotStartMinutes, setSlotStartMinutes] = useState<string>("60"); // starting in X minutes

  const fetchSlots = async () => {
    setLoading(true);
    setError(null);
    try {
      const docIdNum = selectedDoctorId ? parseInt(selectedDoctorId, 10) : undefined;
      const data = await getSlots({
        doctorId: docIdNum,
        isBookable: true,
      });
      setSlots(data || []);
    } catch (err: any) {
      setError(err?.message || "Failed to load appointment slots.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSlots();
  }, [selectedDoctorId]);

  const handleBookSlot = async () => {
    if (!bookingSlot || !bookingSlot.slotId) return;

    if (!isRegistered || !user || !user.userId) {
      setError("Please complete your patient profile before booking an appointment.");
      router.push("/profile" as any);
      return;
    }

    setBookingInProgress(true);
    setError(null);
    setSuccessMsg(null);

    const payload: CreateAppointmentRequest = {
      patientUserId: user.userId,
      slotId: bookingSlot.slotId,
      consultationMode,
      reason: reason.trim() || "Consultation with specialist",
    };

    try {
      const appointment = await createAppointment(payload);
      setSuccessMsg(`Appointment #${appointment.appointmentId} booked successfully!`);
      setBookingSlot(null);
      setReason("");
      fetchSlots(); // refresh slots list
      setTimeout(() => {
        router.push("/appointments" as any);
      }, 1500);
    } catch (err: any) {
      setError(err?.message || "Failed to book slot. It may have already been reserved.");
    } finally {
      setBookingInProgress(false);
    }
  };

  const handleCreateNewSlot = async () => {
    const docId = parseInt(createDoctorId, 10);
    if (!docId) {
      setError("Valid doctor user ID is required.");
      return;
    }

    const minutesOffset = parseInt(slotStartMinutes, 10) || 60;
    const now = new Date();
    const start = new Date(now.getTime() + minutesOffset * 60 * 1000);
    const end = new Date(start.getTime() + 30 * 60 * 1000);

    try {
      const req: CreateSlotRequest = {
        doctorUserId: docId,
        startAt: start.toISOString(),
        endAt: end.toISOString(),
        isBookable: true,
      };
      await createSlot(req);
      setSuccessMsg(`New 30-min slot created for Doctor #${docId}!`);
      setShowCreateSlot(false);
      fetchSlots();
    } catch (err: any) {
      setError(err?.message || "Failed to create slot.");
    }
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <AppHeader
        title="MediConnect"
        subtitle="Doctor Availability Slots & Direct Booking"
      />
      <AppNavigation />

      {/* Top Banner & Filter */}
      <View style={styles.topCard}>
        <Text style={styles.title}>Browse Available Consultation Slots</Text>
        <Text style={styles.subtitle}>
          Select an open time slot to book an immediate video or in-person consultation.
        </Text>

        <View style={styles.filterRow}>
          <View style={styles.inputCol}>
            <Text style={styles.label}>Filter by Doctor ID:</Text>
            <TextInput
              style={styles.input}
              value={selectedDoctorId}
              onChangeText={setSelectedDoctorId}
              placeholder="e.g. 1 (Leave blank for all)"
              keyboardType="numeric"
            />
          </View>
          <Pressable style={styles.refreshBtn} onPress={fetchSlots}>
            <Text style={styles.refreshBtnText}>Filter</Text>
          </Pressable>
          <Pressable
            style={styles.newSlotBtn}
            onPress={() => setShowCreateSlot(!showCreateSlot)}
          >
            <Text style={styles.newSlotBtnText}>+ Open Slot</Text>
          </Pressable>
        </View>
      </View>

      {/* Admin/Doctor Creator Form */}
      {showCreateSlot ? (
        <View style={styles.createSlotCard}>
          <Text style={styles.createTitle}>Open a New Doctor Slot</Text>
          <View style={styles.createRow}>
            <View style={{ flex: 1 }}>
              <Text style={styles.createLabel}>Doctor User ID:</Text>
              <TextInput
                style={styles.createInput}
                value={createDoctorId}
                onChangeText={setCreateDoctorId}
                keyboardType="numeric"
                placeholder="1"
              />
            </View>
            <View style={{ flex: 1.5 }}>
              <Text style={styles.createLabel}>Starts in (minutes from now):</Text>
              <TextInput
                style={styles.createInput}
                value={slotStartMinutes}
                onChangeText={setSlotStartMinutes}
                keyboardType="numeric"
                placeholder="60"
              />
            </View>
          </View>
          <Pressable style={styles.publishSlotBtn} onPress={handleCreateNewSlot}>
            <Text style={styles.publishSlotBtnText}>Publish 30-min Availability Slot</Text>
          </Pressable>
        </View>
      ) : null}

      {/* Messages */}
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

      {/* Slots List */}
      {loading ? (
        <View style={styles.centerBox}>
          <ActivityIndicator size="large" color="#0284C7" />
          <Text style={styles.loadingText}>Fetching available consultation slots...</Text>
        </View>
      ) : slots.length === 0 ? (
        <View style={styles.emptyBox}>
          <Text style={styles.emptyIcon}>📅</Text>
          <Text style={styles.emptyTitle}>No open slots found</Text>
          <Text style={styles.emptySubtitle}>
            There are currently no bookable slots for the selected doctor. You can create a new slot above or search for another doctor.
          </Text>
          <Pressable
            style={styles.browseDoctorsBtn}
            onPress={() => router.push("/doctors")}
          >
            <Text style={styles.browseDoctorsBtnText}>Browse Other Doctors →</Text>
          </Pressable>
        </View>
      ) : (
        <View style={styles.slotsGrid}>
          {slots.map((slot, idx) => {
            const startDate = slot.startAt ? new Date(slot.startAt) : new Date();
            const endDate = slot.endAt ? new Date(slot.endAt) : new Date();
            const isToday = startDate.toDateString() === new Date().toDateString();

            return (
              <View key={slot.slotId ?? idx} style={styles.slotCard}>
                <View style={styles.slotLeft}>
                  <View style={styles.dateBadge}>
                    <Text style={styles.dateDay}>
                      {startDate.toLocaleDateString(undefined, { weekday: "short" })}
                    </Text>
                    <Text style={styles.dateNum}>{startDate.getDate()}</Text>
                    <Text style={styles.dateMonth}>
                      {startDate.toLocaleDateString(undefined, { month: "short" })}
                    </Text>
                  </View>
                  <View style={styles.slotTimeInfo}>
                    <Text style={styles.doctorLabel}>
                      Doctor #{slot.doctorUserId}
                    </Text>
                    <Text style={styles.timeRange}>
                      {startDate.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })} –{" "}
                      {endDate.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
                    </Text>
                    <View style={styles.tagRow}>
                      <View style={styles.openTag}>
                        <Text style={styles.openTagText}>Available</Text>
                      </View>
                      {isToday ? (
                        <View style={styles.todayTag}>
                          <Text style={styles.todayTagText}>Today</Text>
                        </View>
                      ) : null}
                    </View>
                  </View>
                </View>

                <Pressable
                  style={styles.bookBtn}
                  onPress={() => setBookingSlot(slot)}
                >
                  <Text style={styles.bookBtnText}>Book Slot</Text>
                </Pressable>
              </View>
            );
          })}
        </View>
      )}

      {/* Booking Confirmation Dialog Modal */}
      <Modal
        visible={!!bookingSlot}
        transparent
        animationType="fade"
        onRequestClose={() => setBookingSlot(null)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <Text style={styles.modalTitle}>Confirm Appointment Booking</Text>
            {bookingSlot ? (
              <Text style={styles.modalSubtitle}>
                Slot #{bookingSlot.slotId} with Doctor #{bookingSlot.doctorUserId} on{" "}
                {bookingSlot.startAt ? new Date(bookingSlot.startAt).toLocaleString() : "scheduled time"}
              </Text>
            ) : null}

            <Text style={styles.modalFieldLabel}>Consultation Mode:</Text>
            <View style={styles.modePicker}>
              <Pressable
                style={[
                  styles.modeOption,
                  consultationMode === "VIDEO" && styles.modeOptionActive,
                ]}
                onPress={() => setConsultationMode("VIDEO")}
              >
                <Text
                  style={[
                    styles.modeOptionText,
                    consultationMode === "VIDEO" && styles.modeOptionTextActive,
                  ]}
                >
                  📹 Live Video Consult
                </Text>
              </Pressable>
              <Pressable
                style={[
                  styles.modeOption,
                  consultationMode === "IN_PERSON" && styles.modeOptionActive,
                ]}
                onPress={() => setConsultationMode("IN_PERSON")}
              >
                <Text
                  style={[
                    styles.modeOptionText,
                    consultationMode === "IN_PERSON" && styles.modeOptionTextActive,
                  ]}
                >
                  🏥 In-Person Clinic
                </Text>
              </Pressable>
            </View>

            <Text style={styles.modalFieldLabel}>Medical Symptoms / Reason:</Text>
            <TextInput
              style={styles.modalTextInput}
              value={reason}
              onChangeText={setReason}
              placeholder="e.g. Follow-up for chest tightness, fever, etc."
              multiline
              numberOfLines={3}
            />

            <View style={styles.modalBtnRow}>
              <Pressable
                style={styles.modalCancelBtn}
                onPress={() => setBookingSlot(null)}
                disabled={bookingInProgress}
              >
                <Text style={styles.modalCancelBtnText}>Cancel</Text>
              </Pressable>

              <Pressable
                style={[styles.modalConfirmBtn, bookingInProgress && { opacity: 0.6 }]}
                onPress={handleBookSlot}
                disabled={bookingInProgress}
              >
                {bookingInProgress ? (
                  <ActivityIndicator color="#ffffff" size="small" />
                ) : (
                  <Text style={styles.modalConfirmBtnText}>Confirm & Book</Text>
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
  title: {
    fontSize: 18,
    fontWeight: "800",
    color: "#0F172A",
    marginBottom: 4,
  },
  subtitle: {
    fontSize: 13,
    color: "#64748B",
    marginBottom: 16,
    lineHeight: 18,
  },
  filterRow: {
    flexDirection: "row",
    gap: 8,
    alignItems: "flex-end",
  },
  inputCol: {
    flex: 1,
  },
  label: {
    fontSize: 11,
    fontWeight: "600",
    color: "#475569",
    marginBottom: 4,
  },
  input: {
    backgroundColor: "#F8FAFC",
    borderWidth: 1,
    borderColor: "#CBD5E1",
    borderRadius: 8,
    paddingHorizontal: 10,
    paddingVertical: 8,
    fontSize: 13,
  },
  refreshBtn: {
    backgroundColor: "#0F172A",
    paddingHorizontal: 14,
    paddingVertical: 10,
    borderRadius: 8,
  },
  refreshBtnText: {
    color: "#ffffff",
    fontWeight: "700",
    fontSize: 12,
  },
  newSlotBtn: {
    backgroundColor: "#0284C7",
    paddingHorizontal: 14,
    paddingVertical: 10,
    borderRadius: 8,
  },
  newSlotBtnText: {
    color: "#ffffff",
    fontWeight: "700",
    fontSize: 12,
  },
  createSlotCard: {
    marginHorizontal: 16,
    marginBottom: 16,
    padding: 16,
    backgroundColor: "#F0F9FF",
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#BAE6FD",
  },
  createTitle: {
    fontSize: 14,
    fontWeight: "800",
    color: "#0369A1",
    marginBottom: 10,
  },
  createRow: {
    flexDirection: "row",
    gap: 10,
    marginBottom: 12,
  },
  createLabel: {
    fontSize: 11,
    fontWeight: "600",
    color: "#0369A1",
    marginBottom: 4,
  },
  createInput: {
    backgroundColor: "#ffffff",
    borderWidth: 1,
    borderColor: "#7DD3FC",
    borderRadius: 6,
    paddingHorizontal: 10,
    paddingVertical: 6,
    fontSize: 13,
  },
  publishSlotBtn: {
    backgroundColor: "#0284C7",
    paddingVertical: 10,
    borderRadius: 6,
    alignItems: "center",
  },
  publishSlotBtnText: {
    color: "#ffffff",
    fontWeight: "700",
    fontSize: 13,
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
  browseDoctorsBtn: {
    backgroundColor: "#0284C7",
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 8,
  },
  browseDoctorsBtnText: {
    color: "#ffffff",
    fontWeight: "700",
    fontSize: 13,
  },
  slotsGrid: {
    marginHorizontal: 16,
    gap: 10,
  },
  slotCard: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    backgroundColor: "#ffffff",
    padding: 14,
    borderRadius: 14,
    borderWidth: 1,
    borderColor: "#E2E8F0",
  },
  slotLeft: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
    flex: 1,
  },
  dateBadge: {
    backgroundColor: "#E0F2FE",
    paddingHorizontal: 10,
    paddingVertical: 6,
    borderRadius: 10,
    alignItems: "center",
    minWidth: 50,
  },
  dateDay: {
    fontSize: 10,
    fontWeight: "700",
    color: "#0284C7",
    textTransform: "uppercase",
  },
  dateNum: {
    fontSize: 16,
    fontWeight: "800",
    color: "#0369A1",
  },
  dateMonth: {
    fontSize: 10,
    color: "#0284C7",
  },
  slotTimeInfo: {
    flex: 1,
  },
  doctorLabel: {
    fontSize: 11,
    fontWeight: "600",
    color: "#64748B",
    marginBottom: 2,
  },
  timeRange: {
    fontSize: 14,
    fontWeight: "700",
    color: "#0F172A",
    marginBottom: 4,
  },
  tagRow: {
    flexDirection: "row",
    gap: 6,
  },
  openTag: {
    backgroundColor: "#DCFCE7",
    paddingHorizontal: 6,
    paddingVertical: 2,
    borderRadius: 4,
  },
  openTagText: {
    color: "#166534",
    fontSize: 10,
    fontWeight: "700",
  },
  todayTag: {
    backgroundColor: "#FEF3C7",
    paddingHorizontal: 6,
    paddingVertical: 2,
    borderRadius: 4,
  },
  todayTagText: {
    color: "#92400E",
    fontSize: 10,
    fontWeight: "700",
  },
  bookBtn: {
    backgroundColor: "#0284C7",
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 8,
  },
  bookBtnText: {
    color: "#ffffff",
    fontSize: 13,
    fontWeight: "700",
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
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.1,
    shadowRadius: 12,
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
    lineHeight: 18,
  },
  modalFieldLabel: {
    fontSize: 12,
    fontWeight: "700",
    color: "#334155",
    marginBottom: 6,
  },
  modePicker: {
    flexDirection: "row",
    gap: 8,
    marginBottom: 14,
  },
  modeOption: {
    flex: 1,
    paddingVertical: 10,
    backgroundColor: "#F1F5F9",
    borderRadius: 8,
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#E2E8F0",
  },
  modeOptionActive: {
    backgroundColor: "#0284C7",
    borderColor: "#0284C7",
  },
  modeOptionText: {
    fontSize: 12,
    fontWeight: "700",
    color: "#475569",
  },
  modeOptionTextActive: {
    color: "#ffffff",
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
  modalConfirmBtn: {
    flex: 1.5,
    paddingVertical: 12,
    backgroundColor: "#0284C7",
    borderRadius: 8,
    alignItems: "center",
  },
  modalConfirmBtnText: {
    color: "#ffffff",
    fontWeight: "700",
  },
});

