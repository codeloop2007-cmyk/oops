import React, { useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TextInput,
  Pressable,
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
} from "react-native";
import { useRouter } from "expo-router";
import { AppHeader } from "@/components/AppHeader";
import { AppNavigation } from "@/components/AppNavigation";
import { ask1, ask } from "@/api/generated/mediConnectHealthcareAPI";
import type { AgentResponse, AgentRequest } from "@/api/generated/model";

const SUGGESTED_QUERIES = [
  "I have sharp chest pain that radiates to my left arm when breathing deeply.",
  "My 4-year-old child has high fever, vomiting, and refusal to eat.",
  "Sudden severe migraine with blurred vision and sensitivity to bright light.",
  "Itchy red circular rash on my neck and arms that appeared two days ago.",
  "Chronic lower back pain and stiffness after lifting weights at the gym.",
];

export default function AiAssistantScreen() {
  const router = useRouter();
  const [query, setQuery] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);
  const [result, setResult] = useState<AgentResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleAskAgent = async (promptToAsk?: string) => {
    const q = (promptToAsk !== undefined ? promptToAsk : query).trim();
    if (!q) {
      setError("Please describe your symptoms or medical inquiry.");
      return;
    }

    setLoading(true);
    setError(null);
    try {
      const payload: AgentRequest = { query: q };
      // Call Orval generated endpoint function
      const data = await ask1(payload);
      setResult(data);
    } catch (err: any) {
      try {
        const fallbackData = await ask({ query: q });
        setResult(fallbackData);
      } catch (fallbackErr: any) {
        setError(
          fallbackErr?.response?.data?.message ||
            fallbackErr?.response?.data?.error ||
            fallbackErr?.message ||
            "AI service is currently unavailable. Ensure the Python AI service on port 8000 is active."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === "ios" ? "padding" : undefined}
      style={styles.keyboardContainer}
    >
      <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
        <AppHeader
          title="MediConnect"
          subtitle="AI Medical Triage & ReAct Specialist Recommendation"
        />
        <AppNavigation />

        {/* Hero Card */}
        <View style={styles.heroCard}>
          <View style={styles.badgeRow}>
            <View style={styles.agentBadge}>
              <Text style={styles.agentBadgeText}>🤖 Gemini 2.5 Flash ReAct Agent</Text>
            </View>
          </View>

          <Text style={styles.heroTitle}>Symptom Triage & Specialist Matching</Text>
          <Text style={styles.heroSubtitle}>
            Describe what you are experiencing in plain language. Our autonomous medical agent analyzes your symptoms, consults doctor embeddings, and provides matched specialists.
          </Text>

          <View style={styles.inputBox}>
            <TextInput
              style={styles.textArea}
              value={query}
              onChangeText={setQuery}
              placeholder="Describe symptoms in detail (e.g. onset, severity, duration)..."
              placeholderTextColor="#94A3B8"
              multiline
              numberOfLines={4}
            />
            <View style={styles.inputFooter}>
              <Pressable
                style={[styles.submitBtn, loading && styles.btnDisabled]}
                onPress={() => handleAskAgent()}
                disabled={loading}
              >
                {loading ? (
                  <ActivityIndicator color="#ffffff" size="small" />
                ) : (
                  <Text style={styles.submitBtnText}>Ask Medical Agent →</Text>
                )}
              </Pressable>
            </View>
          </View>
        </View>

        {/* Quick Samples */}
        <View style={styles.quickSection}>
          <Text style={styles.quickSectionTitle}>Or click an example clinical query:</Text>
          <View style={styles.queriesList}>
            {SUGGESTED_QUERIES.map((q, idx) => (
              <Pressable
                key={idx}
                style={styles.sampleChip}
                onPress={() => {
                  setQuery(q);
                  handleAskAgent(q);
                }}
              >
                <Text style={styles.sampleChipText}>"{q}"</Text>
              </Pressable>
            ))}
          </View>
        </View>

        {/* Error Notification */}
        {error ? (
          <View style={styles.errorBox}>
            <Text style={styles.errorText}>⚠️ {error}</Text>
          </View>
        ) : null}

        {/* Loading Indicator */}
        {loading ? (
          <View style={styles.centerBox}>
            <ActivityIndicator size="large" color="#0284C7" />
            <Text style={styles.loadingTitle}>Agent is analyzing symptoms...</Text>
            <Text style={styles.loadingSubtitle}>
              Calling backend doctor search tool and synthesizing medical summary
            </Text>
          </View>
        ) : null}

        {/* Agent Structured Response Result */}
        {result && !loading ? (
          <View style={styles.resultCard}>
            <View style={styles.resultHeader}>
              <View style={styles.resultIconBox}>
                <Text style={styles.resultIcon}>🩺</Text>
              </View>
              <View style={styles.resultHeaderInfo}>
                <Text style={styles.resultTitle}>Agent Clinical Assessment</Text>
                <Text style={styles.resultSubtitle}>
                  {(result.doctors || []).length} Recommended Specialist{(result.doctors || []).length === 1 ? "" : "s"}
                </Text>
              </View>
            </View>

            <View style={styles.summaryBox}>
              <Text style={styles.summaryLabel}>Triage Summary & Recommendation:</Text>
              <Text style={styles.summaryText}>{result.summary}</Text>
            </View>

            {result.doctors && result.doctors.length > 0 ? (
              <View style={styles.matchedDoctorsSection}>
                <Text style={styles.matchedTitle}>Matched Healthcare Providers:</Text>

                <View style={styles.matchedGrid}>
                  {result.doctors.map((doc, idx) => (
                    <View key={doc.doctorId ?? idx} style={styles.doctorItemCard}>
                      <View style={styles.docItemTop}>
                        <View style={styles.docAvatar}>
                          <Text style={styles.docAvatarText}>👨‍⚕️</Text>
                        </View>
                        <View style={styles.docItemDetails}>
                          <Text style={styles.docItemName}>{doc.name}</Text>
                          <Text style={styles.docItemSpec}>
                            {doc.specialization || "Medical Specialist"}
                          </Text>
                          {doc.qualification ? (
                            <Text style={styles.docItemQual}>{doc.qualification}</Text>
                          ) : null}
                        </View>
                        <View style={styles.docFee}>
                          <Text style={styles.docFeeAmount}>${doc.consultationFee}</Text>
                          <Text style={styles.docFeeLabel}>Fee</Text>
                        </View>
                      </View>

                      <View style={styles.docItemActions}>
                        <Pressable
                          style={styles.viewDocBtn}
                          onPress={() => router.push(`/doctors/${doc.doctorId}` as any)}
                        >
                          <Text style={styles.viewDocBtnText}>View Doctor Profile</Text>
                        </Pressable>

                        <Pressable
                          style={styles.bookDocBtn}
                          onPress={() => router.push(`/slots?doctorId=${doc.doctorId}` as any)}
                        >
                          <Text style={styles.bookDocBtnText}>Book Slot →</Text>
                        </Pressable>
                      </View>
                    </View>
                  ))}
                </View>
              </View>
            ) : null}
          </View>
        ) : null}
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  keyboardContainer: {
    flex: 1,
  },
  container: {
    flex: 1,
    backgroundColor: "#F8FAFC",
  },
  contentContainer: {
    paddingBottom: 48,
  },
  heroCard: {
    marginHorizontal: 16,
    marginBottom: 16,
    padding: 20,
    backgroundColor: "#0F172A",
    borderRadius: 16,
  },
  badgeRow: {
    marginBottom: 10,
  },
  agentBadge: {
    alignSelf: "flex-start",
    backgroundColor: "rgba(2, 132, 199, 0.25)",
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: "#0284C7",
  },
  agentBadgeText: {
    color: "#38BDF8",
    fontSize: 12,
    fontWeight: "700",
  },
  heroTitle: {
    fontSize: 20,
    fontWeight: "800",
    color: "#ffffff",
    marginBottom: 6,
  },
  heroSubtitle: {
    fontSize: 13,
    color: "#94A3B8",
    marginBottom: 16,
    lineHeight: 18,
  },
  inputBox: {
    backgroundColor: "#1E293B",
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#334155",
    padding: 12,
  },
  textArea: {
    minHeight: 80,
    color: "#ffffff",
    fontSize: 14,
    textAlignVertical: "top",
    marginBottom: 8,
  },
  inputFooter: {
    alignItems: "flex-end",
  },
  submitBtn: {
    backgroundColor: "#0284C7",
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 8,
  },
  submitBtnText: {
    color: "#ffffff",
    fontWeight: "700",
    fontSize: 13,
  },
  btnDisabled: {
    opacity: 0.6,
  },
  quickSection: {
    marginHorizontal: 16,
    marginBottom: 16,
  },
  quickSectionTitle: {
    fontSize: 12,
    fontWeight: "700",
    color: "#64748B",
    marginBottom: 8,
    textTransform: "uppercase",
    letterSpacing: 0.5,
  },
  queriesList: {
    gap: 8,
  },
  sampleChip: {
    backgroundColor: "#ffffff",
    padding: 12,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: "#E2E8F0",
  },
  sampleChipText: {
    fontSize: 12,
    color: "#334155",
    fontStyle: "italic",
    lineHeight: 16,
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
  centerBox: {
    padding: 30,
    alignItems: "center",
  },
  loadingTitle: {
    marginTop: 12,
    fontSize: 14,
    fontWeight: "700",
    color: "#0F172A",
  },
  loadingSubtitle: {
    fontSize: 12,
    color: "#64748B",
    marginTop: 2,
    textAlign: "center",
  },
  resultCard: {
    marginHorizontal: 16,
    padding: 20,
    backgroundColor: "#ffffff",
    borderRadius: 16,
    borderWidth: 1,
    borderColor: "#E2E8F0",
  },
  resultHeader: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
    marginBottom: 16,
  },
  resultIconBox: {
    width: 44,
    height: 44,
    borderRadius: 12,
    backgroundColor: "#E0F2FE",
    alignItems: "center",
    justifyContent: "center",
  },
  resultIcon: {
    fontSize: 22,
  },
  resultHeaderInfo: {
    flex: 1,
  },
  resultTitle: {
    fontSize: 17,
    fontWeight: "800",
    color: "#0F172A",
  },
  resultSubtitle: {
    fontSize: 12,
    color: "#0284C7",
    fontWeight: "600",
    marginTop: 2,
  },
  summaryBox: {
    backgroundColor: "#F8FAFC",
    padding: 14,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    marginBottom: 18,
  },
  summaryLabel: {
    fontSize: 11,
    fontWeight: "700",
    color: "#64748B",
    textTransform: "uppercase",
    letterSpacing: 0.5,
    marginBottom: 6,
  },
  summaryText: {
    fontSize: 14,
    color: "#1E293B",
    lineHeight: 22,
  },
  matchedDoctorsSection: {
    borderTopWidth: 1,
    borderTopColor: "#F1F5F9",
    paddingTop: 16,
  },
  matchedTitle: {
    fontSize: 14,
    fontWeight: "800",
    color: "#0F172A",
    marginBottom: 12,
  },
  matchedGrid: {
    gap: 12,
  },
  doctorItemCard: {
    backgroundColor: "#F8FAFC",
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    padding: 14,
  },
  docItemTop: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 12,
  },
  docAvatar: {
    width: 40,
    height: 40,
    borderRadius: 10,
    backgroundColor: "#ffffff",
    borderWidth: 1,
    borderColor: "#CBD5E1",
    alignItems: "center",
    justifyContent: "center",
    marginRight: 10,
  },
  docAvatarText: {
    fontSize: 20,
  },
  docItemDetails: {
    flex: 1,
  },
  docItemName: {
    fontSize: 15,
    fontWeight: "800",
    color: "#0F172A",
  },
  docItemSpec: {
    fontSize: 12,
    fontWeight: "700",
    color: "#0284C7",
    marginTop: 1,
  },
  docItemQual: {
    fontSize: 11,
    color: "#64748B",
  },
  docFee: {
    alignItems: "flex-end",
  },
  docFeeAmount: {
    fontSize: 14,
    fontWeight: "800",
    color: "#0F172A",
  },
  docFeeLabel: {
    fontSize: 10,
    color: "#64748B",
  },
  docItemActions: {
    flexDirection: "row",
    gap: 8,
  },
  viewDocBtn: {
    flex: 1,
    paddingVertical: 8,
    backgroundColor: "#ffffff",
    borderRadius: 6,
    borderWidth: 1,
    borderColor: "#CBD5E1",
    alignItems: "center",
  },
  viewDocBtnText: {
    fontSize: 12,
    fontWeight: "700",
    color: "#334155",
  },
  bookDocBtn: {
    flex: 1.2,
    paddingVertical: 8,
    backgroundColor: "#0284C7",
    borderRadius: 6,
    alignItems: "center",
  },
  bookDocBtnText: {
    fontSize: 12,
    fontWeight: "700",
    color: "#ffffff",
  },
});

