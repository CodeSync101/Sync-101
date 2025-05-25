package com.mission_entreprise.web_api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
public class ContributionSummaryDTO {
    private int totalContributions;
    private List<ContributorDTO> topContributors;

    @Data
    @AllArgsConstructor
    public static class ContributorDTO {
        private String author;
        private int contributions;
    }
}