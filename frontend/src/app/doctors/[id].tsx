import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Pressable,
  ActivityIndicator,
} from "react-native";
import { useRouter, useLocalSearchParams } from "expo-router";
import { AppHeader } from "@/components/AppHeader";
import { AppNavigation } from "@/components/AppNavigation";
import {
  getDoctorProfileById,
  getReviews,
  getUserById,
} from "@/api/generated/mediConnectHealthcareAPI";
import type {
  DoctorProfileResponse,
  ReviewResponse,
  UserResponse,
} from "@/api/generated/model";

export default function DoctorDetailScreen() {
  const router = useRouter();
  const { id } = useLocalSearchParams<{ id: string }>();
  const doctorId = parseInt(id || "0", 10);

  const [doctor, setDoctor] = useState<DoctorProfileResponse | null>(null);
  const [doctorUser, setDoctorUser] = useState<UserResponse | null>(null);
  const [reviews, setReviews] = useState<ReviewResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!doctorId) return;
    const fetchDetails = async () => {
      setLoading(true);
      setError(null);
      try {
        const [docData, reviewsData, userData] = await Promise.all([
          getDoctorProfileById(doctorId),
          getReviews({ doctorId }).catch(() => [] as ReviewResponse[]),
          getUserById(doctorId).catch(() => null as UserResponse | null),
        ]);
        setDoctor(docData);
        setReviews(reviewsData || []);
        setDoctorUser(userData);
      } catch (err: any) {
        setError(err?.message || "Failed to load doctor profile.");
      } finally {
        setLoading(false);
      }
    };
    fetchDetails();
  }, [doctorId]);

  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#0284C7" />
        <Text style={styles.loadingText}>Loading doctor credentials...</Text>
      </View>
    );
  }

  if (error || !doctor) {
    return (
      <ScrollView style={styles.container}>
        <AppHeader title="MediConnect" />
        <AppNavigation />
        <View style={styles.errorBox}>
          <Text style={styles.errorText}>⚠️ {error || "Doctor not found."}</Text>
          <Pressable style={styles.backBtn} onPress={() => router.push("/doctors")}>
            <Text style={styles.backBtnText}>← Back to Doctors List</Text>
          </Pressable>
        </View>
      </ScrollView>
    );
  }

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <AppHeader
        title="MediConnect"
        subtitle="Specialist Verification & Practice Information"
      />
      <AppNavigation />

      {/* Profile Card */}
      <View style={styles.profileCard}>
        <View style={styles.topRow}>
          <View style={styles.avatarBox}>
            <Text style={styles.avatarText}>👨‍⚕️</Text>
          </View>
          <View style={styles.infoCol}>
            <Text style={styles.docTitle}>
              {doctorUser?.name ? `${doctorUser.name} (Dr. #${doctor.userId})` : `Doctor ID #${doctor.userId}`}
            </Text>
            <Text style={styles.qualification}>{doctor.qualification}</Text>
            <View style={styles.verificationBadge}>
              <Text style={styles.verificationText}>
                🛡️ Status: {doctor.verificationStatus || "VERIFIED"}
              </Text>
            </View>
          </View>
          <View style={styles.feeBox}>
            <Text style={styles.feeVal}>${doctor.consultationFee}</Text>
            <Text style={styles.feeSub}>per session</Text>
          </View>
        </View>

        {/* License & Timezone */}
        <View style={styles.metaGrid}>
          <View style={styles.metaItem}>
            <Text style={styles.metaLabel}>Medical License</Text>
            <Text style={styles.metaVal}>{doctor.licenseNumber}</Text>
          </View>
          <View style={styles.metaItem}>
            <Text style={styles.metaLabel}>Timezone</Text>
            <Text style={styles.metaVal}>{doctor.timezone || "UTC"}</Text>
          </View>
        </View>

        {/* Biography */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Professional Bio</Text>
          <Text style={styles.bioText}>
            {doctor.bio || "Certified medical practitioner dedicated to patient clinical excellence."}
          </Text>
        </View>

        {/* Assigned Specializations */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Medical Specializations</Text>
          <View style={styles.specsRow}>
            {doctor.specializations && doctor.specializations.length > 0 ? (
              doctor.specializations.map((spec) => (
                <View key={spec.specializationId} style={styles.specTag}>
                  <Text style={styles.specTagText}>{spec.name}</Text>
                </View>
              ))
            ) : (
              <Text style={styles.emptySpecsText}>General Medicine Specialist</Text>
            )}
          </View>
        </View>

        {/* Action Button */}
        <Pressable
          style={styles.bookSlotsBtn}
          onPress={() => router.push(`/slots?doctorId=${doctor.userId}` as any)}
        >
          <Text style={styles.bookSlotsBtnText}>Browse Available Consultation Slots 📅</Text>
        </Pressable>
      </View>

      {/* Patient Reviews Section */}
      <View style={styles.reviewsCard}>
        <View style={styles.reviewsHeader}>
          <Text style={styles.reviewsTitle}>Patient Feedback & Reviews ({reviews.length})</Text>
        </View>

        {reviews.length === 0 ? (
          <Text style={styles.noReviewsText}>
            No patient reviews recorded yet for this doctor profile.
          </Text>
        ) : (
          <View style={styles.reviewsList}>
            {reviews.map((rev, idx) => {
              const stars = Math.max(1, Math.min(5, rev.rating || 5));
              const dateStr = rev.createdAt ? new Date(rev.createdAt).toLocaleDateString() : "Recent visit";
              return (
                <View key={rev.reviewId ?? idx} style={styles.reviewItem}>
                  <View style={styles.reviewTop}>
                    <Text style={styles.reviewStars}>
                      {"⭐".repeat(stars)}
                      {"☆".repeat(5 - stars)}
                    </Text>
                    <Text style={styles.reviewDate}>{dateStr}</Text>
                  </View>
                  <Text style={styles.reviewComment}>
                    {rev.comment || "Verified patient consultation."}
                  </Text>
                </View>
              );
            })}
          </View>
        )}
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
  centerContainer: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "#F8FAFC",
  },
  loadingText: {
    marginTop: 12,
    fontSize: 14,
    color: "#64748B",
  },
  errorBox: {
    margin: 20,
    padding: 16,
    backgroundColor: "#FEF2F2",
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#FCA5A5",
  },
  errorText: {
    color: "#B91C1C",
    fontSize: 14,
    marginBottom: 12,
  },
  backBtn: {
    backgroundColor: "#0284C7",
    paddingVertical: 10,
    paddingHorizontal: 16,
    borderRadius: 8,
    alignSelf: "flex-start",
  },
  backBtnText: {
    color: "#ffffff",
    fontWeight: "700",
  },
  profileCard: {
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
    alignItems: "center",
    marginBottom: 16,
  },
  avatarBox: {
    width: 60,
    height: 60,
    borderRadius: 16,
    backgroundColor: "#E0F2FE",
    alignItems: "center",
    justifyContent: "center",
    marginRight: 14,
  },
  avatarText: {
    fontSize: 30,
  },
  infoCol: {
    flex: 1,
  },
  docTitle: {
    fontSize: 18,
    fontWeight: "800",
    color: "#0F172A",
    marginBottom: 2,
  },
  qualification: {
    fontSize: 13,
    color: "#0284C7",
    fontWeight: "700",
    marginBottom: 4,
  },
  verificationBadge: {
    alignSelf: "flex-start",
    backgroundColor: "#ECFDF5",
    paddingHorizontal: 8,
    paddingVertical: 2,
    borderRadius: 6,
  },
  verificationText: {
    fontSize: 11,
    color: "#065F46",
    fontWeight: "700",
  },
  feeBox: {
    backgroundColor: "#F8FAFC",
    padding: 10,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    alignItems: "flex-end",
  },
  feeVal: {
    fontSize: 18,
    fontWeight: "800",
    color: "#0F172A",
  },
  feeSub: {
    fontSize: 11,
    color: "#64748B",
  },
  metaGrid: {
    flexDirection: "row",
    gap: 12,
    backgroundColor: "#F8FAFC",
    padding: 12,
    borderRadius: 10,
    marginBottom: 16,
  },
  metaItem: {
    flex: 1,
  },
  metaLabel: {
    fontSize: 11,
    color: "#64748B",
    fontWeight: "600",
    textTransform: "uppercase",
  },
  metaVal: {
    fontSize: 13,
    fontWeight: "700",
    color: "#0F172A",
    marginTop: 2,
  },
  section: {
    marginBottom: 16,
  },
  sectionTitle: {
    fontSize: 14,
    fontWeight: "800",
    color: "#0F172A",
    marginBottom: 6,
  },
  bioText: {
    fontSize: 13,
    color: "#475569",
    lineHeight: 20,
  },
  specsRow: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8,
  },
  specTag: {
    backgroundColor: "#E0F2FE",
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 8,
  },
  specTagText: {
    color: "#0369A1",
    fontSize: 12,
    fontWeight: "700",
  },
  emptySpecsText: {
    color: "#64748B",
    fontSize: 13,
  },
  bookSlotsBtn: {
    backgroundColor: "#0284C7",
    paddingVertical: 14,
    borderRadius: 10,
    alignItems: "center",
    marginTop: 8,
  },
  bookSlotsBtnText: {
    color: "#ffffff",
    fontWeight: "800",
    fontSize: 14,
  },
  reviewsCard: {
    marginHorizontal: 16,
    padding: 20,
    backgroundColor: "#ffffff",
    borderRadius: 16,
    borderWidth: 1,
    borderColor: "#E2E8F0",
  },
  reviewsHeader: {
    marginBottom: 14,
  },
  reviewsTitle: {
    fontSize: 16,
    fontWeight: "800",
    color: "#0F172A",
  },
  noReviewsText: {
    color: "#64748B",
    fontSize: 13,
    fontStyle: "italic",
  },
  reviewsList: {
    gap: 12,
  },
  reviewItem: {
    padding: 12,
    backgroundColor: "#F8FAFC",
    borderRadius: 10,
    borderWidth: 1,
    borderColor: "#E2E8F0",
  },
  reviewTop: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 6,
  },
  reviewStars: {
    fontSize: 14,
  },
  reviewDate: {
    fontSize: 11,
    color: "#94A3B8",
  },
  reviewComment: {
    fontSize: 13,
    color: "#334155",
    lineHeight: 18,
  },
});

