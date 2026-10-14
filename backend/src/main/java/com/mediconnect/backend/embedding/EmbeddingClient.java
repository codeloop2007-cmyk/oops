package com.mediconnect.backend.embedding;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class EmbeddingClient {

    private final HttpClient http;
    private final String baseUrl;

    public EmbeddingClient(@Value("${embedding.service.base-url:http://localhost:8000}") String baseUrl) {
        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.http = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)     // forces plain HTTP/1.1
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public float[] embed(String text) {
        if (text == null || text.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "text for embedding cannot be blank");
        }

        String json = "{\"text\":\"" + escapeJson(text) + "\"}";
        System.out.println("[EmbeddingClient] -> POST " + baseUrl + "/embed body=" + json);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/embed"))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = http.send(
                    request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            System.out.println("[EmbeddingClient] <- status=" + response.statusCode()
                    + " bodyLen=" + response.body().length());

            if (response.statusCode() / 100 != 2) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "Embedding service returned " + response.statusCode()
                                + ": " + preview(response.body()));
            }
            if (response.body().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "Embedding service returned empty response");
            }

            return parseEmbedding(response.body());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Embedding service unavailable: " + e.getMessage());
        }
    }

    // ---------- helpers ----------

    private static float[] parseEmbedding(String json) {
        int keyIdx = json.indexOf("\"embedding\"");
        if (keyIdx < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "No 'embedding' key in response: " + preview(json));
        }
        int open  = json.indexOf('[', keyIdx);
        int close = json.indexOf(']', open);
        if (open < 0 || close < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Malformed embedding array: " + preview(json));
        }
        String inner = json.substring(open + 1, close).trim();
        if (inner.isEmpty()) return new float[0];

        String[] parts = inner.split(",");
        float[] out = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            out[i] = Float.parseFloat(parts[i].trim());
        }
        return out;
    }

    private static String preview(String s) {
        return s == null ? "null" : (s.length() > 200 ? s.substring(0, 200) + "..." : s);
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