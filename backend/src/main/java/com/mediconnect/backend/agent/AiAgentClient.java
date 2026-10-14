package com.mediconnect.backend.agent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.mediconnect.backend.doctor.Doctor;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class AiAgentClient {

    private final HttpClient http;
    private final String baseUrl;

    public AiAgentClient(@Value("${ai.service.base-url:http://127.0.0.1:8000}") String baseUrl) {
        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.http = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)   // ← required for uvicorn
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public AgentResponse ask(String query) {
        if (query == null || query.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "query cannot be blank");
        }

        String json = "{\"query\":\"" + escapeJson(query) + "\"}";
        System.out.println("[AiAgentClient] -> POST " + baseUrl + "/agent body=" + json);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/agent"))
                    .timeout(Duration.ofSeconds(60))          // LLM can take a while
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = http.send(
                    request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            System.out.println("[AiAgentClient] <- status=" + response.statusCode()
                    + " bodyLen=" + response.body().length());

            if (response.statusCode() / 100 != 2) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "AI agent returned " + response.statusCode()
                                + ": " + preview(response.body()));
            }
            if (response.body().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "AI agent returned empty response");
            }

            return parseAgentResponse(response.body());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "AI agent unavailable: " + e.getMessage());
        }
    }

    // ---------- helpers: hand-rolled parser (no Jackson needed) ----------

    private static AgentResponse parseAgentResponse(String json) {
        AgentResponse out = new AgentResponse();
        out.setSummary(extractString(json, "summary"));
        out.setDoctorIds(extractLongArray(json, "doctorIds"));
        out.setDoctors(extractDoctors(json));
        return out;
    }

    /** Extract `"key":"value"` — handles escaped quotes minimally. */
    private static String extractString(String json, String key) {
        int k = json.indexOf("\"" + key + "\"");
        if (k < 0) return null;
        int colon = json.indexOf(':', k);
        if (colon < 0) return null;
        int firstQuote = json.indexOf('"', colon + 1);
        if (firstQuote < 0) return null;

        StringBuilder sb = new StringBuilder();
        boolean escape = false;
        for (int i = firstQuote + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escape) {
                switch (c) {
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    default -> sb.append(c);
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '"') {
                return sb.toString();
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /** Extract `"key":[1,2,3]`. Returns empty list if absent. */
    private static List<Long> extractLongArray(String json, String key) {
        List<Long> out = new ArrayList<>();
        int k = json.indexOf("\"" + key + "\"");
        if (k < 0) return out;
        int open = json.indexOf('[', k);
        int close = json.indexOf(']', open);
        if (open < 0 || close < 0) return out;

        String inner = json.substring(open + 1, close).trim();
        if (inner.isEmpty()) return out;

        for (String p : inner.split(",")) {
            String t = p.trim();
            if (!t.isEmpty()) {
                try { out.add(Long.parseLong(t)); } catch (NumberFormatException ignored) {}
            }
        }
        return out;
    }

    /** Extract `"doctors":[{...},{...}]` — parses each object's fields. */
    private static List<Doctor> extractDoctors(String json) {
        List<Doctor> out = new ArrayList<>();
        int k = json.indexOf("\"doctors\"");
        if (k < 0) return out;
        int open = json.indexOf('[', k);
        if (open < 0) return out;

        // Find matching close bracket — counts nested []
        int depth = 0;
        int close = -1;
        for (int i = open; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') depth++;
            else if (c == ']') { depth--; if (depth == 0) { close = i; break; } }
        }
        if (close < 0) return out;

        String arr = json.substring(open + 1, close).trim();
        if (arr.isEmpty()) return out;

        // Walk each top-level {...} object
        int i = 0;
        while (i < arr.length()) {
            int objStart = arr.indexOf('{', i);
            if (objStart < 0) break;
            int objDepth = 0;
            int objEnd = -1;
            for (int j = objStart; j < arr.length(); j++) {
                char c = arr.charAt(j);
                if (c == '{') objDepth++;
                else if (c == '}') { objDepth--; if (objDepth == 0) { objEnd = j; break; } }
            }
            if (objEnd < 0) break;
            String objJson = arr.substring(objStart, objEnd + 1);
            out.add(parseDoctor(objJson));
            i = objEnd + 1;
        }
        return out;
    }

    private static Doctor parseDoctor(String objJson) {
        Doctor d = new Doctor();
        d.setDoctorId(extractLong(objJson, "doctorId"));
        d.setName(extractString(objJson, "name"));
        d.setSpecialization(extractString(objJson, "specialization"));
        d.setQualification(extractString(objJson, "qualification"));
        d.setLanguage(extractString(objJson, "language"));
        d.setLocation(extractString(objJson, "location"));
        d.setConsultationFee(extractBigDecimal(objJson, "consultationFee"));
        return d;
    }

    private static Long extractLong(String json, String key) {
        int k = json.indexOf("\"" + key + "\"");
        if (k < 0) return null;
        int colon = json.indexOf(':', k);
        if (colon < 0) return null;
        int start = colon + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) end++;
        if (end == start) return null;
        try { return Long.parseLong(json.substring(start, end)); } catch (NumberFormatException e) { return null; }
    }

    private static BigDecimal extractBigDecimal(String json, String key) {
        int k = json.indexOf("\"" + key + "\"");
        if (k < 0) return null;
        int colon = json.indexOf(':', k);
        if (colon < 0) return null;
        int start = colon + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;
        // numeric literal (may include . and -)
        int end = start;
        while (end < json.length()
                && (Character.isDigit(json.charAt(end))
                || json.charAt(end) == '.' || json.charAt(end) == '-'
                || json.charAt(end) == 'e' || json.charAt(end) == 'E'
                || json.charAt(end) == '+' )) end++;
        if (end == start) return null;
        try { return new BigDecimal(json.substring(start, end)); } catch (NumberFormatException e) { return null; }
    }

    private static String preview(String s) {
        return s == null ? "null" : (s.length() > 300 ? s.substring(0, 300) + "..." : s);
    }

    private static String escapeJson(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"'  -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        return sb.toString();
    }
}