package com.example.GestionRec_Not.services;

import com.example.GestionRec_Not.entities.Priorite;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrioriteService {

    private final Map<Priorite, List<String>> motsClePriorite;

    public PrioriteService() {
        motsClePriorite = new HashMap<>();
        
        // Mots-clés pour priorité HAUTE
        motsClePriorite.put(Priorite.HIGH, Arrays.asList(
                "erreur", "note incorrecte", "urgent", "immédiat", "grave", 
                "problème", "critique", "important", "sérieux", "incorrect", 
                "faux", "échec", "examen", "note", "résultat"
        ));
        
        // Mots-clés pour priorité MOYENNE
        motsClePriorite.put(Priorite.MEDIUM, Arrays.asList(
                "question", "clarification", "information", "précision", 
                "détail", "comprendre", "comment", "pourquoi", "quand"
        ));
        
        // Mots-clés pour priorité BASSE
        motsClePriorite.put(Priorite.LOW, Arrays.asList(
                "suggestion", "remarque", "amélioration", "idée", 
                "proposition", "recommandation", "conseil", "avis"
        ));
    }

    /**
     * Détermine la priorité d'une réclamation en fonction des mots-clés présents
     * dans le titre et la description.
     *
     * @param titre Le titre de la réclamation
     * @param description La description de la réclamation
     * @return La priorité déterminée
     */
    public Priorite determinerPriorite(String titre, String description) {
        String texteComplet = (titre + " " + description).toLowerCase();
        
        // Vérifier d'abord pour les mots-clés à priorité HAUTE
        for (String motCle : motsClePriorite.get(Priorite.HIGH)) {
            if (texteComplet.contains(motCle.toLowerCase())) {
                return Priorite.HIGH;
            }
        }
        
        // Ensuite, vérifier pour les mots-clés à priorité BASSE
        for (String motCle : motsClePriorite.get(Priorite.LOW)) {
            if (texteComplet.contains(motCle.toLowerCase())) {
                return Priorite.LOW;
            }
        }
        
        // Par défaut, attribuer une priorité MOYENNE
        return Priorite.MEDIUM;
    }
} 