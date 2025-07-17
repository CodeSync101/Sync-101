package com.mission_entreprise.web_api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "pull")
@Getter
@Setter
public class Pull {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;
    private String title;
    private String createdAt;
    private String repositoryName;
    private String htmlUrl;
    private String state ;
    private String mergedAt ;
    private String organization ;
}
