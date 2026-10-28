import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TextInput,
  Pressable,
  ActivityIndicator,
} from "react-native";
import { useRouter, useLocalSearchParams } from "expo-router";
import { AppHeader } from "@/components/AppHeader";
import { AppNavigation } from "@/components/AppNavigation";
import { searchDoctors } from "@/api/generated/mediConnectHealthcareAPI";
import type { DoctorSearchResponse, SearchDoctorsParams } from "@/api/generated/model";

const SPECIALIZATIONS = [
  "All",
  "Cardiology",
  "Dermatology",
  "Neurology",
  "Pediatrics",
  "Orthopedics",
  "General Physician",
];

export default function DoctorsScreen() {
  const router = useRouter();
  const params = useLocalSearchParams<{ q?: string; spec?: string }>();

  const [query, setQuery] = useState(params.q || "");
  const [selectedSpec, setSelectedSpec] = useState(params.spec || "All");
  const [minFee, setMinFee] = useState<string>("");
  const [maxFee, setMaxFee] = useState<string>("");
  const [city, setCity] = useState<string>("");

  const [doctors, setDoctors] = useState<DoctorSearchResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const fetchDoctors = async (searchTerm?: string) => {
    setLoading(true);
    setError(null);
    try {
      const q = (searchTerm !== undefined ? searchTerm : query).trim();
      const specFilter = selectedSpec !== "All" ? selectedSpec : undefined;
      const minFeeNum = minFee ? parseFloat(minFee) : undefined;
      const maxFeeNum = maxFee ? parseFloat(maxFee) : undefined;
      const cityFilter = city.trim() || undefined;

      const searchParams: SearchDoctorsParams = {
        q: q || "doctor physician medical",
        specialization: specFilter,
        minFee: minFeeNum,
        maxFee: maxFeeNum,
        city: cityFilter,
        limit: 20,
      };

      const data = await searchDoctors(searchParams);
      setDoctors(data || []);
    } catch (err: any) {
      setError(err?.message || "Failed to search doctors.");
      setDoctors([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDoctors(params.q || "");
  }, [params.q, selectedSpec]);

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <AppHeader
        title="MediConnect"
        subtitle="Search specialists with AI semantic matching & book direct consultation slots"
      />
      <AppNavigation />

      {/* Search Header Banner */}
      <View style={styles.searchBanner}>
        <Text style={styles.bannerTitle}>Find & Filter Certified Doctors</Text>
        <Text style={styles.bannerSubtitle}>
          Search by symptom (e.g. "sharp knee pain"), specialty, or doctor name.
        </Text>

        <View style={styles.searchBar}>
          <Text style={styles.searchIcon}>🔍</Text>
          <TextInput
            style={styles.searchInput}
            value={query}
            onChangeText={setQuery}
            placeholder="Search symptoms, specialties, or doctor name..."
            placeholderTextColor="#94A3B8"
            returnKeyType="search"
            onSubmitEditing={() => fetchDoctors()}
          />
          <Pressable style={styles.searchBtn} onPress={() => fetchDoctors()}>
            <Text style={styles.searchBtnText}>Search</Text>
          </Pressable>
        </View>

        {/* Specialization Filter Chips */}
        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.chipScroll}
        >
          {SPECIALIZATIONS.map((spec) => (
            <Pressable
              key={spec}
              style={[
                styles.chip,
                selectedSpec === spec && styles.chipActive,
              ]}
              onPress={() => setSelectedSpec(spec)}
            >
              <Text
                style={[
                  styles.chipText,
                  selectedSpec === spec && styles.chipTextActive,
                ]}
              >
                {spec}
              </Text>
            </Pressable>
          ))}
        </ScrollView>
      </View>

      {/* Advanced Filters Accordion */}
      <View style={styles.filterCard}>
        <Text style={styles.filterCardTitle}>Advanced Filters</Text>
        <View style={styles.filterRow}>
          <View style={styles.filterInputCol}>
            <Text style={styles.filterLabel}>Min Fee ($)</Text>
            <TextInput
              style={styles.filterInput}
              value={minFee}
              onChangeText={setMinFee}
              placeholder="0"
              keyboardType="numeric"
            />
          </View>
          <View style={styles.filterInputCol}>
            <Text style={styles.filterLabel}>Max Fee ($)</Text>
            <TextInput
              style={styles.filterInput}
              value={maxFee}
              onChangeText={setMaxFee}
              placeholder="500"
              keyboardType="numeric"
            />
          </View>
          <View style={[styles.filterInputCol, { flex: 1.5 }]}>
            <Text style={styles.filterLabel}>City / Location</Text>
            <TextInput
              style={styles.filterInput}
              value={city}
              onChangeText={setCity}
              placeholder="e.g. New York"
            />
          </View>
          <Pressable style={styles.applyFilterBtn} onPress={() => fetchDoctors()}>
            <Text style={styles.applyFilterBtnText}>Apply</Text>
          </Pressable>
        </View>
      </View>

      {/* Results Header */}
      <View style={styles.resultsHeader}>
        <Text style={styles.resultsCount}>
          {loading ? "Searching..." : `${doctors.length} doctor${doctors.length === 1 ? "" : "s"} found`}
        </Text>
      </View>

      {error ? (
        <View style={styles.errorBox}>
          <Text style={styles.errorText}>⚠️ {error}</Text>
        </View>
      ) : null}

      {loading ? (
        <View style={styles.loadingBox}>
          <ActivityIndicator size="large" color="#0284C7" />
          <Text style={styles.loadingText}>Searching medical database with vector embeddings...</Text>
        </View>
      ) : doctors.length === 0 ? (
        <View style={styles.emptyBox}>
          <Text style={styles.emptyIcon}>🩺</Text>
          <Text style={styles.emptyTitle}>No doctors found</Text>
          <Text style={styles.emptySubtitle}>
            Try adjusting your search query, clearing filters, or asking the AI Assistant for recommendations.
          </Text>
          <Pressable
            style={styles.aiTriageBtn}
            onPress={() => router.push("/ai-assistant" as any)}
          >
            <Text style={styles.aiTriageBtnText}>Try AI Symptom Triage 🤖</Text>
          </Pressable>
        </View>
      ) : (
        <View style={styles.doctorGrid}>
          {doctors.map((doc) => (
            <View key={doc.doctorId} style={styles.doctorCard}>
              <View style={styles.docHeader}>
                <View style={styles.avatarBox}>
                  <Text style={styles.avatarText}>👨‍⚕️</Text>
                </View>
                <View style={styles.docInfo}>
                  <Text style={styles.docName}>{doc.name}</Text>
                  <Text style={styles.docSpec}>{doc.specialization || "Specialist"}</Text>
                  {doc.qualification ? (
                    <Text style={styles.docQual}>{doc.qualification}</Text>
                  ) : null}
                </View>
                <View style={styles.feeBadge}>
                  <Text style={styles.feeAmount}>${doc.consultationFee}</Text>
                  <Text style={styles.feeLabel}>per visit</Text>
                </View>
              </View>

              <View style={styles.metaRow}>
                {doc.location ? (
                  <Text style={styles.metaText}>📍 {doc.location}</Text>
                ) : null}
                {doc.language ? (
                  <Text style={styles.metaText}>🗣️ {doc.language}</Text>
                ) : null}
              </View>

              <View style={styles.cardActions}>
                <Pressable
                  style={styles.viewProfileBtn}
                  onPress={() => router.push(`/doctors/${doc.doctorId}` as any)}
                >
                  <Text style={styles.viewProfileBtnText}>View Profile</Text>
                </Pressable>

                <Pressable
                  style={styles.bookSlotBtn}
                  onPress={() => router.push(`/slots?doctorId=${doc.doctorId}` as any)}
                >
                  <Text style={styles.bookSlotBtnText}>View Slots & Book →</Text>
                </Pressable>
              </View>
            </View>
          ))}
        </View>
      )}
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
  searchBanner: {
    marginHorizontal: 16,
    marginBottom: 16,
    padding: 20,
    backgroundColor: "#0F172A",
    borderRadius: 16,
  },
  bannerTitle: {
    fontSize: 20,
    fontWeight: "800",
    color: "#ffffff",
    marginBottom: 4,
  },
  bannerSubtitle: {
    fontSize: 13,
    color: "#94A3B8",
    marginBottom: 16,
    lineHeight: 18,
  },
  searchBar: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#1E293B",
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#334155",
    paddingHorizontal: 12,
    paddingVertical: 4,
    marginBottom: 14,
  },
  searchIcon: {
    fontSize: 16,
    marginRight: 8,
  },
  searchInput: {
    flex: 1,
    height: 44,
    fontSize: 14,
    color: "#ffffff",
  },
  searchBtn: {
    backgroundColor: "#0284C7",
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 8,
  },
  searchBtnText: {
    color: "#ffffff",
    fontWeight: "700",
    fontSize: 13,
  },
  chipScroll: {
    gap: 8,
  },
  chip: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 20,
    backgroundColor: "rgba(255, 255, 255, 0.08)",
    borderWidth: 1,
    borderColor: "rgba(255, 255, 255, 0.15)",
  },
  chipActive: {
    backgroundColor: "#0284C7",
    borderColor: "#38BDF8",
  },
  chipText: {
    color: "#E2E8F0",
    fontSize: 12,
    fontWeight: "600",
  },
  chipTextActive: {
    color: "#ffffff",
    fontWeight: "700",
  },
  filterCard: {
    marginHorizontal: 16,
    marginBottom: 16,
    padding: 14,
    backgroundColor: "#ffffff",
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#E2E8F0",
  },
  filterCardTitle: {
    fontSize: 12,
    fontWeight: "700",
    color: "#64748B",
    textTransform: "uppercase",
    letterSpacing: 0.5,
    marginBottom: 8,
  },
  filterRow: {
    flexDirection: "row",
    gap: 8,
    alignItems: "flex-end",
  },
  filterInputCol: {
    flex: 1,
  },
  filterLabel: {
    fontSize: 11,
    color: "#475569",
    fontWeight: "600",
    marginBottom: 4,
  },
  filterInput: {
    backgroundColor: "#F8FAFC",
    borderWidth: 1,
    borderColor: "#CBD5E1",
    borderRadius: 6,
    paddingHorizontal: 8,
    paddingVertical: 6,
    fontSize: 12,
    color: "#0F172A",
  },
  applyFilterBtn: {
    backgroundColor: "#0F172A",
    paddingHorizontal: 14,
    paddingVertical: 8,
    borderRadius: 6,
    justifyContent: "center",
  },
  applyFilterBtnText: {
    color: "#ffffff",
    fontSize: 12,
    fontWeight: "700",
  },
  resultsHeader: {
    marginHorizontal: 16,
    marginBottom: 12,
  },
  resultsCount: {
    fontSize: 13,
    fontWeight: "700",
    color: "#64748B",
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
  loadingBox: {
    padding: 40,
    alignItems: "center",
  },
  loadingText: {
    marginTop: 12,
    fontSize: 13,
    color: "#64748B",
  },
  emptyBox: {
    padding: 40,
    alignItems: "center",
    backgroundColor: "#ffffff",
    marginHorizontal: 16,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: "#E2E8F0",
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
  aiTriageBtn: {
    backgroundColor: "#0284C7",
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 8,
  },
  aiTriageBtnText: {
    color: "#ffffff",
    fontWeight: "700",
    fontSize: 13,
  },
  doctorGrid: {
    marginHorizontal: 16,
    gap: 12,
  },
  doctorCard: {
    backgroundColor: "#ffffff",
    borderRadius: 14,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    padding: 16,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.03,
    shadowRadius: 6,
    elevation: 1,
  },
  docHeader: {
    flexDirection: "row",
    alignItems: "flex-start",
    marginBottom: 10,
  },
  avatarBox: {
    width: 48,
    height: 48,
    borderRadius: 12,
    backgroundColor: "#E0F2FE",
    alignItems: "center",
    justifyContent: "center",
    marginRight: 12,
  },
  avatarText: {
    fontSize: 24,
  },
  docInfo: {
    flex: 1,
  },
  docName: {
    fontSize: 16,
    fontWeight: "800",
    color: "#0F172A",
    marginBottom: 2,
  },
  docSpec: {
    fontSize: 13,
    fontWeight: "700",
    color: "#0284C7",
    marginBottom: 2,
  },
  docQual: {
    fontSize: 12,
    color: "#64748B",
  },
  feeBadge: {
    alignItems: "flex-end",
    backgroundColor: "#F8FAFC",
    paddingHorizontal: 10,
    paddingVertical: 6,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "#E2E8F0",
  },
  feeAmount: {
    fontSize: 15,
    fontWeight: "800",
    color: "#0F172A",
  },
  feeLabel: {
    fontSize: 10,
    color: "#64748B",
  },
  metaRow: {
    flexDirection: "row",
    gap: 16,
    marginBottom: 14,
    paddingVertical: 6,
    borderTopWidth: 1,
    borderBottomWidth: 1,
    borderColor: "#F1F5F9",
  },
  metaText: {
    fontSize: 12,
    color: "#475569",
    fontWeight: "500",
  },
  cardActions: {
    flexDirection: "row",
    gap: 8,
  },
  viewProfileBtn: {
    flex: 1,
    paddingVertical: 10,
    backgroundColor: "#F1F5F9",
    borderRadius: 8,
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#CBD5E1",
  },
  viewProfileBtnText: {
    color: "#334155",
    fontSize: 13,
    fontWeight: "700",
  },
  bookSlotBtn: {
    flex: 1.5,
    paddingVertical: 10,
    backgroundColor: "#0284C7",
    borderRadius: 8,
    alignItems: "center",
  },
  bookSlotBtnText: {
    color: "#ffffff",
    fontSize: 13,
    fontWeight: "700",
  },
});

