package com.mission_entreprise.web_api.services.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class OpenRouterService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey = "AIzaSyCiZATuNVkUdd8zP5NtJaMyfiOqdYdkQRY";
    private final String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

    public OpenRouterService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String generateResponse(String prompt) {
        try {
            // Créer le corps de la requête pour Gemini
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", new Object[]{part});

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", new Object[]{content});

            // Définir les headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Envoyer la requête
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            // Extraire le texte de la réponse
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            return jsonResponse
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (Exception e) {
            return "Erreur lors de la communication avec Gemini : " + e.getMessage();
        }
    }
}
