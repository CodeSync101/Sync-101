package com.mission_entreprise.web_api.controllers.report;

import com.mission_entreprise.web_api.services.report.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/prompt")
    public String handlePrompt(@RequestBody String prompt) {
        return chatbotService.processPrompt(prompt);
    }
}