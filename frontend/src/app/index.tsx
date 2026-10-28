import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Pressable,
  TextInput,
} from 'react-native';
import { useRouter } from 'expo-router';
import { AppHeader } from '@/components/AppHeader';
import { AppNavigation } from '@/components/AppNavigation';

const QUICK_TOPICS = [
  { label: '❤️ Heart & Chest', query: 'chest pain palpitations heart cardiologist' },
  { label: '👶 Infant Fever', query: 'child fever pediatrician baby' },
  { label: '🧴 Skin & Allergies', query: 'skin rash itching allergy dermatologist' },
  { label: '🧠 Migraine & Nerve', query: 'migraine headache dizziness neurologist' },
  { label: '🦴 Bone & Spine', query: 'knee back pain joints orthopedic' },
  { label: '🩺 General Health', query: 'fever fatigue weakness physician' },
];

export default function HomeScreen() {
  const router = useRouter();
  const [searchQuery, setSearchQuery] = useState('');

  const handleHeroSearch = (queryToSearch?: string) => {
    const q = (queryToSearch ?? searchQuery).trim();
    if (q) {
      router.push(`/doctors?q=${encodeURIComponent(q)}` as any);
    } else {
      router.push('/doctors');
    }
  };

  const featureCards = [
    {
      title: 'Find Doctors & Semantic Search',
      desc: 'Match with certified specialists by describing symptoms in everyday natural language.',
      link: '/doctors',
      badge: 'AI Powered',
      badgeColor: '#E0F2FE',
      badgeTextColor: '#0369A1',
      icon: '🩺',
    },
    {
      title: 'AI Medical Assistant',
      desc: 'Ask medical inquiries, triage symptoms, and receive instant specialist recommendations.',
      link: '/ai-assistant',
      badge: 'Gemini ReAct',
      badgeColor: '#FEF3C7',
      badgeTextColor: '#92400E',
      icon: '🤖',
    },
    {
      title: 'Appointments & Slots',
      desc: 'Browse available consultation hours and manage your scheduled doctor appointments.',
      link: '/appointments',
      badge: 'Live Booking',
      badgeColor: '#DCFCE7',
      badgeTextColor: '#166534',
      icon: '📅',
    },
    {
      title: 'Telehealth Video Consults',
      desc: 'Live high-definition encrypted video appointments with doctors using LiveKit WebRTC.',
      link: '/video-demo',
      badge: 'Encrypted',
      badgeColor: '#F3E8FF',
      badgeTextColor: '#6B21A8',
      icon: '📹',
    },
    {
      title: 'Patient Profile & Records',
      desc: 'Review your registered account details, patient ID, and consultation history.',
      link: '/profile',
      badge: 'User Portal',
      badgeColor: '#F1F5F9',
      badgeTextColor: '#334155',
      icon: '👤',
    },
  ];

  const highlights = [
    { label: 'Semantic AI Search', desc: 'Symptom matching', icon: '✨' },
    { label: 'Instant Booking', desc: 'Real-time open slots', icon: '⚡' },
    { label: 'Verified Doctors', desc: 'Certified specialists', icon: '🛡️' },
    { label: 'HD Video Calls', desc: 'Secure telehealth', icon: '🔒' },
  ];

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <AppHeader
        title="MediConnect"
        subtitle="Next-generation healthcare platform powered by AI semantic search & telehealth"
      />
      <AppNavigation />

      {/* Hero Semantic Search Banner */}
      <View style={styles.heroSection}>
        <View style={styles.heroTag}>
          <Text style={styles.heroTagText}>⚡ AI-Powered Healthcare</Text>
        </View>

        <Text style={styles.heroTitle}>Find the Right Doctor by Describing Your Symptoms</Text>
        <Text style={styles.heroSubtitle}>
          Type your symptoms naturally. Our semantic AI matches you with the ideal medical specialists.
        </Text>

        <View style={styles.searchBarContainer}>
          <Text style={styles.searchBarIcon}>🔍</Text>
          <TextInput
            style={styles.heroInput}
            value={searchQuery}
            onChangeText={setSearchQuery}
            placeholder="e.g. sharp chest pain, itchy red rash, persistent migraine..."
            placeholderTextColor="#94A3B8"
            returnKeyType="search"
            onSubmitEditing={() => handleHeroSearch()}
          />
          <Pressable style={styles.heroSearchBtn} onPress={() => handleHeroSearch()}>
            <Text style={styles.heroSearchBtnText}>Search</Text>
          </Pressable>
        </View>

        <View style={styles.quickChipsSection}>
          <Text style={styles.quickChipsTitle}>Common inquiries:</Text>
          <ScrollView
            horizontal
            showsHorizontalScrollIndicator={false}
            contentContainerStyle={styles.quickChipsScroll}
          >
            {QUICK_TOPICS.map((topic) => (
              <Pressable
                key={topic.label}
                style={styles.quickChip}
                onPress={() => handleHeroSearch(topic.query)}
              >
                <Text style={styles.quickChipText}>{topic.label}</Text>
              </Pressable>
            ))}
          </ScrollView>
        </View>
      </View>

      {/* Highlights bar */}
      <View style={styles.highlightsContainer}>
        {highlights.map((item) => (
          <View key={item.label} style={styles.highlightCard}>
            <Text style={styles.highlightIcon}>{item.icon}</Text>
            <Text style={styles.highlightLabel}>{item.label}</Text>
            <Text style={styles.highlightDesc}>{item.desc}</Text>
          </View>
        ))}
      </View>

      {/* Main Core Features Section */}
      <View style={styles.sectionHeader}>
        <Text style={styles.sectionTitle}>Healthcare Services</Text>
        <Text style={styles.sectionSubtitle}>
          Everything you need for comprehensive medical consultations
        </Text>
      </View>

      <View style={styles.cardsGrid}>
        {featureCards.map((feature) => (
          <Pressable
            key={feature.link}
            style={styles.card}
            onPress={() => router.push(feature.link as any)}
          >
            <View style={styles.cardHeader}>
              <View style={styles.cardIconBox}>
                <Text style={styles.cardIcon}>{feature.icon}</Text>
              </View>
              <View
                style={[
                  styles.cardBadge,
                  { backgroundColor: feature.badgeColor },
                ]}
              >
                <Text
                  style={[
                    styles.cardBadgeText,
                    { color: feature.badgeTextColor },
                  ]}
                >
                  {feature.badge}
                </Text>
              </View>
            </View>

            <Text style={styles.cardTitle}>{feature.title}</Text>
            <Text style={styles.cardDesc}>{feature.desc}</Text>

            <View style={styles.cardFooter}>
              <Text style={styles.cardAction}>Open feature</Text>
              <Text style={styles.cardArrow}>&rarr;</Text>
            </View>
          </Pressable>
        ))}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F8FAFC',
  },
  contentContainer: {
    paddingBottom: 48,
  },
  heroSection: {
    marginHorizontal: 16,
    marginBottom: 20,
    padding: 24,
    backgroundColor: '#0F172A',
    borderRadius: 20,
    shadowColor: '#0F172A',
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.15,
    shadowRadius: 16,
    elevation: 4,
  },
  heroTag: {
    alignSelf: 'flex-start',
    backgroundColor: 'rgba(2, 132, 199, 0.25)',
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: '#0284C7',
    marginBottom: 14,
  },
  heroTagText: {
    color: '#38BDF8',
    fontSize: 12,
    fontWeight: '700',
    letterSpacing: 0.3,
  },
  heroTitle: {
    fontSize: 22,
    fontWeight: '800',
    color: '#ffffff',
    lineHeight: 28,
    marginBottom: 8,
  },
  heroSubtitle: {
    fontSize: 14,
    lineHeight: 22,
    color: '#94A3B8',
    marginBottom: 18,
  },
  searchBarContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#1E293B',
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#334155',
    paddingHorizontal: 12,
    paddingVertical: 4,
  },
  searchBarIcon: {
    fontSize: 16,
    marginRight: 8,
  },
  heroInput: {
    flex: 1,
    height: 44,
    fontSize: 14,
    color: '#ffffff',
  },
  heroSearchBtn: {
    backgroundColor: '#0284C7',
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 8,
  },
  heroSearchBtnText: {
    color: '#ffffff',
    fontWeight: '700',
    fontSize: 13,
  },
  quickChipsSection: {
    marginTop: 16,
  },
  quickChipsTitle: {
    fontSize: 11,
    fontWeight: '600',
    color: '#64748B',
    marginBottom: 8,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  quickChipsScroll: {
    gap: 8,
  },
  quickChip: {
    backgroundColor: 'rgba(255, 255, 255, 0.08)',
    borderWidth: 1,
    borderColor: 'rgba(255, 255, 255, 0.15)',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 16,
  },
  quickChipText: {
    color: '#E2E8F0',
    fontSize: 12,
    fontWeight: '500',
  },
  highlightsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginHorizontal: 16,
    gap: 10,
    marginBottom: 24,
  },
  highlightCard: {
    flex: 1,
    minWidth: '46%',
    backgroundColor: '#ffffff',
    padding: 14,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#E2E8F0',
  },
  highlightIcon: {
    fontSize: 20,
    marginBottom: 6,
  },
  highlightLabel: {
    fontSize: 13,
    fontWeight: '700',
    color: '#0F172A',
  },
  highlightDesc: {
    fontSize: 12,
    color: '#64748B',
    marginTop: 2,
  },
  sectionHeader: {
    marginHorizontal: 16,
    marginBottom: 14,
  },
  sectionTitle: {
    fontSize: 19,
    fontWeight: '800',
    color: '#0F172A',
  },
  sectionSubtitle: {
    fontSize: 13,
    color: '#64748B',
    marginTop: 2,
  },
  cardsGrid: {
    marginHorizontal: 16,
    gap: 14,
  },
  card: {
    padding: 18,
    backgroundColor: '#ffffff',
    borderRadius: 16,
    borderWidth: 1,
    borderColor: '#E2E8F0',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.03,
    shadowRadius: 6,
    elevation: 1,
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 10,
  },
  cardIconBox: {
    width: 40,
    height: 40,
    borderRadius: 10,
    backgroundColor: '#F1F5F9',
    alignItems: 'center',
    justifyContent: 'center',
  },
  cardIcon: {
    fontSize: 20,
  },
  cardBadge: {
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 8,
  },
  cardBadgeText: {
    fontSize: 11,
    fontWeight: '700',
  },
  cardTitle: {
    fontSize: 16,
    fontWeight: '700',
    color: '#0F172A',
    marginBottom: 6,
  },
  cardDesc: {
    fontSize: 13,
    lineHeight: 20,
    color: '#64748B',
    marginBottom: 14,
  },
  cardFooter: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    borderTopWidth: 1,
    borderTopColor: '#F1F5F9',
    paddingTop: 10,
  },
  cardAction: {
    fontSize: 13,
    fontWeight: '700',
    color: '#0284C7',
  },
  cardArrow: {
    fontSize: 16,
    color: '#0284C7',
    fontWeight: '700',
  },
});
