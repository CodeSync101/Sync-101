package com.mission_entreprise.web_api.repositories;

import com.mission_entreprise.web_api.entities.Commit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitRepository extends JpaRepository<Commit,Long> {

    boolean existsByAuthorAndDate(String author, String date);
}
