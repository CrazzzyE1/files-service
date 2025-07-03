package ru.litvak.files_service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

public class JwtTokenMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static UUID getUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }
        String jwtToken = authHeader.replace("Bearer ", "");
        String[] parts = jwtToken.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token format");
        }

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        Map<String, Object> claims;
        try {
            claims = new ObjectMapper().readValue(payloadJson, Map.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid readValue for Claims");
        }

        return UUID.fromString((String) claims.get("sub"));
    }
}
