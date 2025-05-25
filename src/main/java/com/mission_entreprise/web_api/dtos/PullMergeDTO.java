package com.mission_entreprise.web_api.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PullMergeDTO {
    private String createdAt;
    private String author ;
    private String htmlUrl ;
    private String state ;
    private String mergedAt ;
    private String eventType ;
}
