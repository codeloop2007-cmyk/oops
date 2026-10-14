package com.mediconnect.backend.embedding;

public record EmbeddingResponse(String text, int dimension, float[] embedding) {}