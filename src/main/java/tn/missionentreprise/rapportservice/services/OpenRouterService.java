package tn.missionentreprise.rapportservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenRouterService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiUrl;
    private final String apiKey;

    public OpenRouterService(@Value("${openrouter.api.url}") String apiUrl,
                             @Value("${openrouter.api.key}") String apiKey) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    public String generateResponse(String prompt) {
        try {
            // Créer les headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            // Créer le corps de la requête
            String requestBody = """
                    {
                        "model": "mistralai/mixtral-8x7b-instruct",
                        "messages": [
                            {
                                "role": "user",
                                "content": "%s"
                            }
                        ]
                    }
                    """.formatted(prompt.replace("\"", "\\\""));

            // Envoyer la requête
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            // Extraire la réponse
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            return jsonResponse.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            return "Erreur lors de la communication avec OpenRouter : " + e.getMessage();
        }
    }
}