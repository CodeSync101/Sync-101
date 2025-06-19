package com.example.backend.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "author")
@Getter
@Setter
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty("name")
    @Column(name = "author_name")
    private String name;

    @JsonProperty("email")
    @Column(name = "author_email")
    private String email;

    @JsonProperty("date")
    @Column(name = "date")
    private String date;
}
