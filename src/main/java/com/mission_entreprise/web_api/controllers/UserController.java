package com.mission_entreprise.web_api.controllers;


import com.mission_entreprise.web_api.entities.GitHubUser;
import com.mission_entreprise.web_api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/{username}")
    public GitHubUser getUser(@PathVariable String username) {
        return service.getUser(username);
    }

}
