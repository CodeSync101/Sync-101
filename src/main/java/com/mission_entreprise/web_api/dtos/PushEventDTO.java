package com.mission_entreprise.web_api.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;


@Getter
@Setter
@AllArgsConstructor
public class PushEventDTO {
    private ZonedDateTime date;
    private String author ;
    private String htmlUrl ;
    private String eventType ;


}
