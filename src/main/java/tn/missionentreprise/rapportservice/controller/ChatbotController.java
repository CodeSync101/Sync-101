package tn.missionentreprise.rapportservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.missionentreprise.rapportservice.services.ChatbotService;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin("http://localhost:4200")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/prompt")
    public String handlePrompt(@RequestBody String prompt) {
        return chatbotService.processPrompt(prompt);
    }
}