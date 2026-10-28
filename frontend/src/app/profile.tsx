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
import { useSession } from "@/context/SessionContext";
import type { RegisterUserRequest } from "@/api/generated/model";

export default function AuthScreen() {
  const router = useRouter();
  const {
    clientIdentityId,
    user,
    isLoading,
    isRegistered,
    error: sessionError,
    setIdentity,
    generateNewIdentity,
    registerProfile,
    logout,
  } = useSession();

  // Mode: "identity" | "register"
  const [identityInput, setIdentityInput] = useState(clientIdentityId || "");
  const [isSwitchingIdentity, setIsSwitchingIdentity] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);
  const [formSuccess, setFormSuccess] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Registration form fields matching RegisterUserRequest and Address
  const [name, setName] = useState(user?.name || "");
  const [email, setEmail] = useState(user?.email || "");
  const [phone, setPhone] = useState(user?.phone || "");
  const [preferredLanguage, setPreferredLanguage] = useState(user?.preferredLanguage || "en");
  const [addressLine1, setAddressLine1] = useState(user?.address?.addressLine1 || "");
  const [addressLine2, setAddressLine2] = useState(user?.address?.addressLine2 || "");
  const [city, setCity] = useState(user?.address?.city || "");
  const [state, setState] = useState(user?.address?.state || "");
  const [postalCode, setPostalCode] = useState(user?.address?.postalCode || "");
  const [country, setCountry] = useState(user?.address?.country || "USA");

  const handleApplyIdentity = async (idToApply?: string) => {
    const target = (idToApply ?? identityInput).trim();
    if (!target) {
      setFormError("Please enter a valid UUID client identity.");
      return;
    }
    setFormError(null);
    setFormSuccess(null);
    try {
      await setIdentity(target);
      setIdentityInput(target);
      setIsSwitchingIdentity(false);
      setFormSuccess("Identity synchronized successfully with backend.");
    } catch (err: any) {
      setFormError(err?.message || "Failed to switch identity.");
    }
  };

  const handleGenerateIdentity = async () => {
    setFormError(null);
    setFormSuccess(null);
    try {
      const newId = await generateNewIdentity();
      setIdentityInput(newId);
      setIsSwitchingIdentity(false);
      setFormSuccess("Generated new client identity UUID.");
    } catch (err: any) {
      setFormError(err?.message || "Failed to generate identity.");
    }
  };

  const handleRegister = async () => {
    setFormError(null);
    setFormSuccess(null);

    // Validation
    if (!name.trim()) {
      setFormError("Full name is required.");
      return;
    }
    if (!email.trim() || !email.includes("@")) {
      setFormError("A valid email address is required.");
      return;
    }
    if (!phone.trim() || phone.trim().length < 7) {
      setFormError("Valid phone number (7-20 characters) is required.");
      return;
    }
    if (!addressLine1.trim() || !city.trim() || !state.trim() || !postalCode.trim() || !country.trim()) {
      setFormError("Please fill in all required address fields.");
      return;
    }

    const payload: RegisterUserRequest = {
      name: name.trim(),
      email: email.trim().toLowerCase(),
      phone: phone.trim(),
      preferredLanguage: preferredLanguage.trim() || "en",
      address: {
        addressLine1: addressLine1.trim(),
        addressLine2: addressLine2.trim() || undefined,
        city: city.trim(),
        state: state.trim(),
        postalCode: postalCode.trim(),
        country: country.trim(),
        isDefault: true,
      },
    };

    setIsSubmitting(true);
    try {
      await registerProfile(payload);
      setFormSuccess("Profile registered successfully! Registration status is now COMPLETED.");
      setTimeout(() => {
        router.push("/doctors" as any);
      }, 1200);
    } catch (err: any) {
      setFormError(err?.message || "Registration failed. Please check your details.");
    } finally {
      setIsSubmitting(false);
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
          subtitle="Patient Authentication & Identity Synchronization"
        />
        <AppNavigation />

        {/* Global Notifications */}
        {sessionError ? (
          <View style={styles.errorBanner}>
            <Text style={styles.errorBannerText}>⚠️ {sessionError}</Text>
          </View>
        ) : null}

        {formError ? (
          <View style={styles.errorBanner}>
            <Text style={styles.errorBannerText}>⚠️ {formError}</Text>
          </View>
        ) : null}

        {formSuccess ? (
          <View style={styles.successBanner}>
            <Text style={styles.successBannerText}>✅ {formSuccess}</Text>
          </View>
        ) : null}

        {/* Identity & Session Card */}
        <View style={styles.card}>
          <View style={styles.cardHeader}>
            <View style={styles.cardIconBox}>
              <Text style={styles.cardIcon}>🔑</Text>
            </View>
            <View style={styles.cardHeaderText}>
              <Text style={styles.cardTitle}>Client Identity Session</Text>
              <Text style={styles.cardSubtitle}>
                MediConnect links patient health records to a secure external UUID header (
                <Text style={styles.codeText}>X-Client-User-Id</Text>)
              </Text>
            </View>
          </View>

          <View style={styles.identityBox}>
            <Text style={styles.identityLabel}>Current Active UUID:</Text>
            <Text style={styles.identityValue} selectable>
              {clientIdentityId || "None assigned"}
            </Text>

            <View style={styles.statusRow}>
              <View
                style={[
                  styles.statusTag,
                  isRegistered ? styles.statusTagCompleted : styles.statusTagPending,
                ]}
              >
                <Text
                  style={[
                    styles.statusTagText,
                    isRegistered ? styles.statusTagTextCompleted : styles.statusTagTextPending,
                  ]}
                >
                  Status: {user?.registrationStatus || (isLoading ? "SYNCING..." : "PENDING")}
                </Text>
              </View>

              {user?.userId ? (
                <Text style={styles.userIdText}>Internal ID: #{user.userId}</Text>
              ) : null}
            </View>
          </View>

          {isSwitchingIdentity ? (
            <View style={styles.switchBox}>
              <Text style={styles.fieldLabel}>Custom UUID Identity:</Text>
              <TextInput
                style={styles.input}
                value={identityInput}
                onChangeText={setIdentityInput}
                placeholder="e.g. 123e4567-e89b-12d3-a456-426614174000"
                placeholderTextColor="#94A3B8"
                autoCapitalize="none"
                autoCorrect={false}
              />
              <View style={styles.btnRow}>
                <Pressable
                  style={styles.primaryBtn}
                  onPress={() => handleApplyIdentity()}
                >
                  <Text style={styles.primaryBtnText}>Apply & Sync</Text>
                </Pressable>
                <Pressable
                  style={styles.secondaryBtn}
                  onPress={() => setIsSwitchingIdentity(false)}
                >
                  <Text style={styles.secondaryBtnText}>Cancel</Text>
                </Pressable>
              </View>
            </View>
          ) : (
            <View style={styles.identityActionsRow}>
              <Pressable
                style={styles.outlineActionBtn}
                onPress={() => {
                  setIdentityInput(clientIdentityId || "");
                  setIsSwitchingIdentity(true);
                }}
              >
                <Text style={styles.outlineActionBtnText}>Switch Identity</Text>
              </Pressable>

              <Pressable
                style={styles.outlineActionBtn}
                onPress={handleGenerateIdentity}
              >
                <Text style={styles.outlineActionBtnText}>New UUID</Text>
              </Pressable>

              <Pressable
                style={styles.dangerActionBtn}
                onPress={logout}
              >
                <Text style={styles.dangerActionBtnText}>Reset</Text>
              </Pressable>
            </View>
          )}
        </View>

        {/* Completed Profile State */}
        {isRegistered && user ? (
          <View style={styles.card}>
            <View style={styles.cardHeader}>
              <View style={[styles.cardIconBox, { backgroundColor: "#DCFCE7" }]}>
                <Text style={styles.cardIcon}>👤</Text>
              </View>
              <View style={styles.cardHeaderText}>
                <Text style={styles.cardTitle}>{user.name}</Text>
                <Text style={styles.cardSubtitle}>Verified Patient Profile</Text>
              </View>
            </View>

            <View style={styles.profileDetailsGrid}>
              <View style={styles.detailItem}>
                <Text style={styles.detailLabel}>Email</Text>
                <Text style={styles.detailValue}>{user.email || "N/A"}</Text>
              </View>
              <View style={styles.detailItem}>
                <Text style={styles.detailLabel}>Phone</Text>
                <Text style={styles.detailValue}>{user.phone || "N/A"}</Text>
              </View>
              <View style={styles.detailItem}>
                <Text style={styles.detailLabel}>Language</Text>
                <Text style={styles.detailValue}>{user.preferredLanguage || "en"}</Text>
              </View>
              <View style={styles.detailItem}>
                <Text style={styles.detailLabel}>Location</Text>
                <Text style={styles.detailValue}>
                  {user.address ? `${user.address.city}, ${user.address.state}, ${user.address.country}` : "Not specified"}
                </Text>
              </View>
            </View>

            <View style={styles.cardFooterActions}>
              <Pressable
                style={styles.primaryBtn}
                onPress={() => router.push("/doctors" as any)}
              >
                <Text style={styles.primaryBtnText}>Browse Available Doctors →</Text>
              </Pressable>
            </View>
          </View>
        ) : (
          /* Registration Form for Pending User */
          <View style={styles.card}>
            <View style={styles.cardHeader}>
              <View style={[styles.cardIconBox, { backgroundColor: "#FEF3C7" }]}>
                <Text style={styles.cardIcon}>📝</Text>
              </View>
              <View style={styles.cardHeaderText}>
                <Text style={styles.cardTitle}>Complete Patient Registration</Text>
                <Text style={styles.cardSubtitle}>
                  Your identity is in <Text style={styles.boldText}>PENDING</Text> status. Submit your medical contact information to activate your account.
                </Text>
              </View>
            </View>

            <View style={styles.formSection}>
              <Text style={styles.sectionHeaderTitle}>1. Personal Information</Text>

              <View style={styles.fieldGroup}>
                <Text style={styles.fieldLabel}>Full Name *</Text>
                <TextInput
                  style={styles.input}
                  value={name}
                  onChangeText={setName}
                  placeholder="e.g. Jane Doe"
                  placeholderTextColor="#94A3B8"
                />
              </View>

              <View style={styles.twoColumnRow}>
                <View style={[styles.fieldGroup, { flex: 1 }]}>
                  <Text style={styles.fieldLabel}>Email Address *</Text>
                  <TextInput
                    style={styles.input}
                    value={email}
                    onChangeText={setEmail}
                    placeholder="jane.doe@example.com"
                    placeholderTextColor="#94A3B8"
                    keyboardType="email-address"
                    autoCapitalize="none"
                  />
                </View>

                <View style={[styles.fieldGroup, { flex: 1 }]}>
                  <Text style={styles.fieldLabel}>Phone Number *</Text>
                  <TextInput
                    style={styles.input}
                    value={phone}
                    onChangeText={setPhone}
                    placeholder="+1234567890"
                    placeholderTextColor="#94A3B8"
                    keyboardType="phone-pad"
                  />
                </View>
              </View>

              <View style={styles.fieldGroup}>
                <Text style={styles.fieldLabel}>Preferred Language</Text>
                <TextInput
                  style={styles.input}
                  value={preferredLanguage}
                  onChangeText={setPreferredLanguage}
                  placeholder="en"
                  placeholderTextColor="#94A3B8"
                />
              </View>
            </View>

            <View style={styles.formSection}>
              <Text style={styles.sectionHeaderTitle}>2. Residential Address</Text>

              <View style={styles.fieldGroup}>
                <Text style={styles.fieldLabel}>Street Address Line 1 *</Text>
                <TextInput
                  style={styles.input}
                  value={addressLine1}
                  onChangeText={setAddressLine1}
                  placeholder="123 Healthcare Ave"
                  placeholderTextColor="#94A3B8"
                />
              </View>

              <View style={styles.fieldGroup}>
                <Text style={styles.fieldLabel}>Address Line 2 (Optional)</Text>
                <TextInput
                  style={styles.input}
                  value={addressLine2}
                  onChangeText={setAddressLine2}
                  placeholder="Suite or Apt 4B"
                  placeholderTextColor="#94A3B8"
                />
              </View>

              <View style={styles.twoColumnRow}>
                <View style={[styles.fieldGroup, { flex: 1 }]}>
                  <Text style={styles.fieldLabel}>City *</Text>
                  <TextInput
                    style={styles.input}
                    value={city}
                    onChangeText={setCity}
                    placeholder="Springfield"
                    placeholderTextColor="#94A3B8"
                  />
                </View>

                <View style={[styles.fieldGroup, { flex: 1 }]}>
                  <Text style={styles.fieldLabel}>State / Province *</Text>
                  <TextInput
                    style={styles.input}
                    value={state}
                    onChangeText={setState}
                    placeholder="IL"
                    placeholderTextColor="#94A3B8"
                  />
                </View>
              </View>

              <View style={styles.twoColumnRow}>
                <View style={[styles.fieldGroup, { flex: 1 }]}>
                  <Text style={styles.fieldLabel}>Postal / ZIP Code *</Text>
                  <TextInput
                    style={styles.input}
                    value={postalCode}
                    onChangeText={setPostalCode}
                    placeholder="62701"
                    placeholderTextColor="#94A3B8"
                  />
                </View>

                <View style={[styles.fieldGroup, { flex: 1 }]}>
                  <Text style={styles.fieldLabel}>Country *</Text>
                  <TextInput
                    style={styles.input}
                    value={country}
                    onChangeText={setCountry}
                    placeholder="USA"
                    placeholderTextColor="#94A3B8"
                  />
                </View>
              </View>
            </View>

            <View style={styles.submitSection}>
              <Pressable
                style={[styles.primaryBtn, isSubmitting && styles.btnDisabled]}
                onPress={handleRegister}
                disabled={isSubmitting}
              >
                {isSubmitting ? (
                  <ActivityIndicator color="#ffffff" size="small" />
                ) : (
                  <Text style={styles.primaryBtnText}>Register Profile</Text>
                )}
              </Pressable>
            </View>
          </View>
        )}
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
  errorBanner: {
    marginHorizontal: 16,
    marginBottom: 12,
    padding: 12,
    backgroundColor: "#FEF2F2",
    borderRadius: 10,
    borderWidth: 1,
    borderColor: "#FCA5A5",
  },
  errorBannerText: {
    color: "#B91C1C",
    fontSize: 13,
    fontWeight: "600",
  },
  successBanner: {
    marginHorizontal: 16,
    marginBottom: 12,
    padding: 12,
    backgroundColor: "#ECFDF5",
    borderRadius: 10,
    borderWidth: 1,
    borderColor: "#A7F3D0",
  },
  successBannerText: {
    color: "#047857",
    fontSize: 13,
    fontWeight: "600",
  },
  card: {
    marginHorizontal: 16,
    marginBottom: 20,
    padding: 20,
    backgroundColor: "#ffffff",
    borderRadius: 16,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.04,
    shadowRadius: 8,
    elevation: 2,
  },
  cardHeader: {
    flexDirection: "row",
    alignItems: "flex-start",
    marginBottom: 16,
    gap: 12,
  },
  cardIconBox: {
    width: 44,
    height: 44,
    borderRadius: 12,
    backgroundColor: "#E0F2FE",
    alignItems: "center",
    justifyContent: "center",
  },
  cardIcon: {
    fontSize: 22,
  },
  cardHeaderText: {
    flex: 1,
  },
  cardTitle: {
    fontSize: 17,
    fontWeight: "800",
    color: "#0F172A",
    marginBottom: 4,
  },
  cardSubtitle: {
    fontSize: 13,
    color: "#64748B",
    lineHeight: 18,
  },
  codeText: {
    fontFamily: Platform.OS === "ios" ? "Courier" : "monospace",
    fontWeight: "700",
    color: "#0284C7",
  },
  identityBox: {
    backgroundColor: "#F8FAFC",
    borderRadius: 12,
    padding: 14,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    marginBottom: 14,
  },
  identityLabel: {
    fontSize: 11,
    fontWeight: "700",
    color: "#64748B",
    textTransform: "uppercase",
    letterSpacing: 0.5,
    marginBottom: 4,
  },
  identityValue: {
    fontSize: 14,
    fontWeight: "700",
    color: "#0F172A",
    marginBottom: 10,
  },
  statusRow: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
  },
  statusTag: {
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 6,
  },
  statusTagCompleted: {
    backgroundColor: "#DCFCE7",
  },
  statusTagPending: {
    backgroundColor: "#FEF3C7",
  },
  statusTagText: {
    fontSize: 11,
    fontWeight: "700",
  },
  statusTagTextCompleted: {
    color: "#166534",
  },
  statusTagTextPending: {
    color: "#92400E",
  },
  userIdText: {
    fontSize: 12,
    color: "#64748B",
    fontWeight: "600",
  },
  identityActionsRow: {
    flexDirection: "row",
    gap: 8,
  },
  outlineActionBtn: {
    flex: 1,
    paddingVertical: 10,
    backgroundColor: "#F1F5F9",
    borderRadius: 8,
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#CBD5E1",
  },
  outlineActionBtnText: {
    fontSize: 12,
    fontWeight: "700",
    color: "#334155",
  },
  dangerActionBtn: {
    paddingHorizontal: 14,
    paddingVertical: 10,
    backgroundColor: "#FEE2E2",
    borderRadius: 8,
    alignItems: "center",
  },
  dangerActionBtnText: {
    fontSize: 12,
    fontWeight: "700",
    color: "#DC2626",
  },
  switchBox: {
    marginTop: 10,
    backgroundColor: "#F1F5F9",
    padding: 12,
    borderRadius: 10,
  },
  fieldLabel: {
    fontSize: 12,
    fontWeight: "700",
    color: "#334155",
    marginBottom: 6,
  },
  input: {
    backgroundColor: "#ffffff",
    borderWidth: 1,
    borderColor: "#CBD5E1",
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 10,
    fontSize: 13,
    color: "#0F172A",
    marginBottom: 10,
  },
  btnRow: {
    flexDirection: "row",
    gap: 8,
    marginTop: 4,
  },
  primaryBtn: {
    flex: 1,
    backgroundColor: "#0284C7",
    paddingVertical: 12,
    borderRadius: 8,
    alignItems: "center",
    justifyContent: "center",
  },
  primaryBtnText: {
    color: "#ffffff",
    fontWeight: "700",
    fontSize: 14,
  },
  secondaryBtn: {
    paddingHorizontal: 16,
    paddingVertical: 12,
    backgroundColor: "#E2E8F0",
    borderRadius: 8,
    alignItems: "center",
    justifyContent: "center",
  },
  secondaryBtnText: {
    color: "#475569",
    fontWeight: "700",
    fontSize: 13,
  },
  btnDisabled: {
    opacity: 0.6,
  },
  formSection: {
    marginTop: 10,
    marginBottom: 16,
    borderTopWidth: 1,
    borderTopColor: "#F1F5F9",
    paddingTop: 14,
  },
  sectionHeaderTitle: {
    fontSize: 14,
    fontWeight: "800",
    color: "#0F172A",
    marginBottom: 12,
  },
  fieldGroup: {
    marginBottom: 10,
  },
  twoColumnRow: {
    flexDirection: "row",
    gap: 10,
  },
  submitSection: {
    marginTop: 10,
  },
  boldText: {
    fontWeight: "700",
  },
  profileDetailsGrid: {
    backgroundColor: "#F8FAFC",
    borderRadius: 12,
    padding: 14,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    gap: 10,
  },
  detailItem: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    borderBottomWidth: 1,
    borderBottomColor: "#EDF2F7",
    paddingBottom: 6,
  },
  detailLabel: {
    fontSize: 12,
    fontWeight: "600",
    color: "#64748B",
  },
  detailValue: {
    fontSize: 13,
    fontWeight: "700",
    color: "#0F172A",
  },
  cardFooterActions: {
    marginTop: 16,
  },
});
