package com.mission_entreprise.web_api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PullResponse {
    private String title;

    @JsonProperty("html_url")
    private String htmlUrl;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("state")
    private String state;

    @JsonProperty("merged_at")
    private String merged_at;

    @JsonProperty("user")
    private User user;

    @Getter
    @Setter
    public static class User {
        private String login;
    }
}
