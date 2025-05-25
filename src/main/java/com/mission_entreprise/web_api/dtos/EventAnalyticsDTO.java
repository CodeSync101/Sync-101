package com.mission_entreprise.web_api.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventAnalyticsDTO {

    private String date;
    private String author ;
    private String htmlUrl ;
    private String eventType ;

}
