package com.example.scanner.pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@JsonSerialize
public record Document(float version, @NotNull String text, @NotNull String fileName,
                       @NotNull HashMap<String, String> metadata) {

    public String toJsonString() {
        var mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return null;
        }
    }
}
