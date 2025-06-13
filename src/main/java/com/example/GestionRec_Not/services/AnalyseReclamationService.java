package com.example.GestionRec_Not.services;

import com.example.GestionRec_Not.entities.Priorite;
import com.example.GestionRec_Not.entities.Reclamation;
import com.example.GestionRec_Not.entities.Statut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class AnalyseReclamationService {

    @Autowired
    @Lazy
    private ReclamationService reclamationService;


    public Map<String, Integer> analyserMotsClesFrequents() {
        List<Reclamation> reclamations = reclamationService.getAllReclamations();
        Map<String, Integer> frequenceMotsCles = new HashMap<>();
        
        // Liste de mots à ignorer (articles, prépositions, etc.)
        Set<String> motsIgnores = Set.of("le", "la", "les", "un", "une", "des", 
                                        "de", "du", "et", "à", "au", "aux", 
                                        "en", "dans", "sur", "pour", "avec", "je", "tu", "il", "elle");
        
        for (Reclamation reclamation : reclamations) {
            String texteComplet = (reclamation.getTitre() + " " + reclamation.getDescription()).toLowerCase();
            String[] mots = texteComplet.split("\\s+|\\p{Punct}");
            
            for (String mot : mots) {
                mot = mot.trim();
                if (mot.length() > 2 && !motsIgnores.contains(mot)) {
                    frequenceMotsCles.put(mot, frequenceMotsCles.getOrDefault(mot, 0) + 1);
                }
            }
        }
        
        // Trier par fréquence décroissante et limiter aux 20 premiers
        return frequenceMotsCles.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(20)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
    

    public Map<Priorite, Double> calculerDistributionPriorites() {
        List<Reclamation> reclamations = reclamationService.getAllReclamations();
        Map<Priorite, Long> comptage = reclamations.stream()
                .collect(Collectors.groupingBy(Reclamation::getPriorite, Collectors.counting()));
        
        int total = reclamations.size();
        Map<Priorite, Double> distribution = new HashMap<>();
        
        for (Priorite priorite : Priorite.values()) {
            long count = comptage.getOrDefault(priorite, 0L);
            double pourcentage = total > 0 ? (count * 100.0) / total : 0;
            distribution.put(priorite, pourcentage);
        }
        
        return distribution;
    }
    

    public Map<Priorite, Double> calculerTempsResolutionMoyen() {
        List<Reclamation> reclamations = reclamationService.getAllReclamations();
        LocalDate aujourdhui = LocalDate.now();
        
        // Filtrer seulement les réclamations résolues
        Map<Priorite, List<Long>> dureeParPriorite = new HashMap<>();
        
        for (Reclamation reclamation : reclamations) {
            if (reclamation.getStatut() == Statut.TRAITEE) {
                LocalDate dateCreation = reclamation.getDateCreation();
                // Calculer le nombre de jours entre la création et aujourd'hui (ou utiliser une date de résolution si disponible)
                long joursDeTraitement = ChronoUnit.DAYS.between(dateCreation, aujourdhui);
                
                Priorite priorite = reclamation.getPriorite();
                if (!dureeParPriorite.containsKey(priorite)) {
                    dureeParPriorite.put(priorite, new ArrayList<>());
                }
                dureeParPriorite.get(priorite).add(joursDeTraitement);
            }
        }
        
        // Calculer la moyenne pour chaque priorité
        Map<Priorite, Double> tempsResolutionMoyen = new HashMap<>();
        for (Map.Entry<Priorite, List<Long>> entry : dureeParPriorite.entrySet()) {
            List<Long> durees = entry.getValue();
            if (!durees.isEmpty()) {
                double moyenne = durees.stream().mapToLong(Long::longValue).average().orElse(0);
                tempsResolutionMoyen.put(entry.getKey(), moyenne);
            } else {
                tempsResolutionMoyen.put(entry.getKey(), 0.0);
            }
        }
        
        return tempsResolutionMoyen;
    }
    

    public List<String> suggererAmeliorationsPriorite() {
        List<String> suggestions = new ArrayList<>();
        Map<Priorite, Double> distribution = calculerDistributionPriorites();
        Map<String, Integer> motsClesFrequents = analyserMotsClesFrequents();
        
        // Vérifier si la distribution des priorités est déséquilibrée
        if (distribution.getOrDefault(Priorite.HIGH, 0.0) > 50) {
            suggestions.add("Trop de réclamations à priorité HAUTE. Envisagez d'affiner les critères de détection.");
        }
        
        if (distribution.getOrDefault(Priorite.LOW, 0.0) < 10) {
            suggestions.add("Très peu de réclamations à priorité BASSE. Envisagez d'élargir les critères pour cette catégorie.");
        }
        
        // Analyser les mots-clés fréquents qui pourraient être ajoutés aux critères
        List<String> motsFrequentsNonCouverts = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : motsClesFrequents.entrySet()) {
            if (entry.getValue() > 5) {
                motsFrequentsNonCouverts.add(entry.getKey());
            }
        }
        
        if (!motsFrequentsNonCouverts.isEmpty()) {
            suggestions.add("Envisagez d'ajouter ces mots-clés fréquents aux critères de priorité: " 
                            + String.join(", ", motsFrequentsNonCouverts));
        }
        
        return suggestions;
    }
} 