package com.mission_entreprise.web_api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "branch", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "repositoryName"})
})

@Getter
@Setter
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean isProtected;

    private String repositoryName;

    private String organization;
}
