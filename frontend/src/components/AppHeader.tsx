import React from "react";
import { View, Text, StyleSheet, Pressable } from "react-native";
import { useRouter } from "expo-router";
import { useSession } from "@/context/SessionContext";

interface AppHeaderProps {
  title?: string;
  subtitle?: string;
}

export const AppHeader: React.FC<AppHeaderProps> = ({
  title = "MediConnect",
  subtitle = "Next-generation healthcare platform powered by AI semantic search & telehealth",
}) => {
  const router = useRouter();
  const { user, isRegistered, isLoading } = useSession();

  return (
    <View style={styles.headerContainer}>
      <View style={styles.topRow}>
        <Pressable onPress={() => router.push("/")} style={styles.brandRow}>
          <View style={styles.logoBadge}>
            <Text style={styles.logoIcon}>🩺</Text>
          </View>
          <View>
            <Text style={styles.brandTitle}>{title}</Text>
            <Text style={styles.brandTagline}>Healthcare Platform</Text>
          </View>
        </Pressable>

        <Pressable
          style={[
            styles.authStatusBadge,
            isRegistered ? styles.authStatusActive : styles.authStatusPending,
          ]}
          onPress={() => router.push("/profile" as any)}
        >
          <View
            style={[
              styles.statusDot,
              isRegistered ? styles.statusDotActive : styles.statusDotPending,
            ]}
          />
          <Text
            style={[
              styles.authStatusText,
              isRegistered ? styles.authStatusTextActive : styles.authStatusTextPending,
            ]}
          >
            {isLoading
              ? "Connecting..."
              : isRegistered
              ? user?.name || "Patient Active"
              : "Pending Profile"}
          </Text>
        </Pressable>
      </View>

      {subtitle ? <Text style={styles.subtitleText}>{subtitle}</Text> : null}
    </View>
  );
};

const styles = StyleSheet.create({
  headerContainer: {
    paddingHorizontal: 16,
    paddingTop: 16,
    paddingBottom: 12,
    backgroundColor: "#ffffff",
    borderBottomWidth: 1,
    borderBottomColor: "#E2E8F0",
    marginBottom: 16,
  },
  topRow: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
  },
  brandRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
  },
  logoBadge: {
    width: 42,
    height: 42,
    borderRadius: 12,
    backgroundColor: "#E0F2FE",
    alignItems: "center",
    justifyContent: "center",
  },
  logoIcon: {
    fontSize: 22,
  },
  brandTitle: {
    fontSize: 20,
    fontWeight: "800",
    color: "#0F172A",
    letterSpacing: -0.3,
  },
  brandTagline: {
    fontSize: 11,
    fontWeight: "600",
    color: "#0284C7",
    textTransform: "uppercase",
    letterSpacing: 0.5,
  },
  subtitleText: {
    fontSize: 13,
    color: "#64748B",
    marginTop: 8,
    lineHeight: 18,
  },
  authStatusBadge: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 20,
    borderWidth: 1,
    gap: 6,
  },
  authStatusActive: {
    backgroundColor: "#ECFDF5",
    borderColor: "#A7F3D0",
  },
  authStatusPending: {
    backgroundColor: "#FFFBEB",
    borderColor: "#FDE68A",
  },
  statusDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
  },
  statusDotActive: {
    backgroundColor: "#10B981",
  },
  statusDotPending: {
    backgroundColor: "#F59E0B",
  },
  authStatusText: {
    fontSize: 12,
    fontWeight: "700",
  },
  authStatusTextActive: {
    color: "#065F46",
  },
  authStatusTextPending: {
    color: "#92400E",
  },
});
