package com.mission_entreprise.web_api.repositories;

import com.mission_entreprise.web_api.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author,Long> {


}
