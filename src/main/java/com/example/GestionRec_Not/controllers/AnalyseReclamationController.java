package com.example.GestionRec_Not.controllers;

import com.example.GestionRec_Not.entities.Priorite;
import com.example.GestionRec_Not.services.AnalyseReclamationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analyse")
public class AnalyseReclamationController {

    @Autowired
    private AnalyseReclamationService analyseService;

    /**
     * Retourne les mots-clés les plus fréquents dans les réclamations
     */
    @GetMapping("/mots-cles-frequents")
    public ResponseEntity<Map<String, Integer>> getMotsClesFrequents() {
        return ResponseEntity.ok(analyseService.analyserMotsClesFrequents());
    }

    /**
     * Retourne la distribution des réclamations par priorité
     */
    @GetMapping("/distribution-priorites")
    public ResponseEntity<Map<String, Double>> getDistributionPriorites() {
        Map<Priorite, Double> distribution = analyseService.calculerDistributionPriorites();
        Map<String, Double> result = new HashMap<>();
        
        for (Map.Entry<Priorite, Double> entry : distribution.entrySet()) {
            result.put(entry.getKey().name(), entry.getValue());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Retourne le temps moyen de résolution des réclamations par priorité
     */
    @GetMapping("/temps-resolution")
    public ResponseEntity<Map<String, Double>> getTempsResolutionMoyen() {
        Map<Priorite, Double> tempsResolution = analyseService.calculerTempsResolutionMoyen();
        Map<String, Double> result = new HashMap<>();
        
        for (Map.Entry<Priorite, Double> entry : tempsResolution.entrySet()) {
            result.put(entry.getKey().name(), entry.getValue());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Retourne des suggestions pour améliorer le système de priorisation
     */
    @GetMapping("/suggestions-amelioration")
    public ResponseEntity<List<String>> getSuggestionsAmelioration() {
        return ResponseEntity.ok(analyseService.suggererAmeliorationsPriorite());
    }

    /**
     * Retourne un tableau de bord complet avec toutes les statistiques
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        dashboard.put("motsClesFrequents", analyseService.analyserMotsClesFrequents());
        
        Map<Priorite, Double> distribution = analyseService.calculerDistributionPriorites();
        Map<String, Double> distributionFormatted = new HashMap<>();
        for (Map.Entry<Priorite, Double> entry : distribution.entrySet()) {
            distributionFormatted.put(entry.getKey().name(), entry.getValue());
        }
        dashboard.put("distributionPriorites", distributionFormatted);
        
        Map<Priorite, Double> tempsResolution = analyseService.calculerTempsResolutionMoyen();
        Map<String, Double> tempsResolutionFormatted = new HashMap<>();
        for (Map.Entry<Priorite, Double> entry : tempsResolution.entrySet()) {
            tempsResolutionFormatted.put(entry.getKey().name(), entry.getValue());
        }
        dashboard.put("tempsResolutionMoyen", tempsResolutionFormatted);
        
        dashboard.put("suggestionsAmelioration", analyseService.suggererAmeliorationsPriorite());
        
        return ResponseEntity.ok(dashboard);
    }
} 