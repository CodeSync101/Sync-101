package com.mission_entreprise.web_api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommitResponse {
    private String sha;
    private CommitDetails commit;
    @JsonProperty("html_url")
    private String htmlUrl;
    private String organization ;
    @Getter
    @Setter
    public static class CommitDetails {
        private CommitAuthor author;
        private String message;

        @Getter
        @Setter
        public static class CommitAuthor {
            private String name;
            private String email;
            private String date;
        }
    }
}
