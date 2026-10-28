import React from "react";
import { View, Text, StyleSheet, Pressable, ScrollView } from "react-native";
import { useRouter, usePathname } from "expo-router";

interface NavItem {
  label: string;
  route: string;
  icon: string;
}

const NAV_ITEMS: NavItem[] = [
  { label: "Home", route: "/", icon: "🏠" },
  { label: "Find Doctors", route: "/doctors", icon: "🩺" },
  { label: "AI Triage", route: "/ai-assistant", icon: "🤖" },
  { label: "Doctor Slots", route: "/slots", icon: "⏰" },
  { label: "Appointments", route: "/appointments", icon: "📅" },
  { label: "Video Telehealth", route: "/video-demo", icon: "📹" },
  { label: "My Profile", route: "/profile", icon: "👤" },
];

export const AppNavigation: React.FC = () => {
  const router = useRouter();
  const pathname = usePathname();

  return (
    <View style={styles.navContainer}>
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.navScroll}
      >
        {NAV_ITEMS.map((item) => {
          const isActive =
            item.route === "/"
              ? pathname === "/"
              : pathname.startsWith(item.route);

          return (
            <Pressable
              key={item.route}
              style={[styles.navItem, isActive && styles.navItemActive]}
              onPress={() => router.push(item.route as any)}
            >
              <Text style={styles.navIcon}>{item.icon}</Text>
              <Text style={[styles.navLabel, isActive && styles.navLabelActive]}>
                {item.label}
              </Text>
            </Pressable>
          );
        })}
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  navContainer: {
    backgroundColor: "#ffffff",
    borderBottomWidth: 1,
    borderBottomColor: "#E2E8F0",
    paddingVertical: 8,
    marginBottom: 16,
  },
  navScroll: {
    paddingHorizontal: 16,
    gap: 8,
    flexDirection: "row",
    alignItems: "center",
  },
  navItem: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 14,
    paddingVertical: 8,
    borderRadius: 12,
    backgroundColor: "#F8FAFC",
    borderWidth: 1,
    borderColor: "#E2E8F0",
    gap: 6,
  },
  navItemActive: {
    backgroundColor: "#0284C7",
    borderColor: "#0284C7",
  },
  navIcon: {
    fontSize: 14,
  },
  navLabel: {
    fontSize: 13,
    fontWeight: "600",
    color: "#475569",
  },
  navLabelActive: {
    color: "#ffffff",
    fontWeight: "700",
  },
});
