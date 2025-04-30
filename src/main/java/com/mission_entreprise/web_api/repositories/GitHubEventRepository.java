package com.mission_entreprise.web_api.repositories;

import com.mission_entreprise.web_api.entities.GitHubEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface GitHubEventRepository extends JpaRepository<GitHubEventEntity,Long> {
    Optional<GitHubEventEntity> findByEventId(String eventId);

    List<GitHubEventEntity> findByRepoNameOrderByCreatedAtDesc(String repoName);

    List<GitHubEventEntity> findByActorLoginOrderByCreatedAtDesc(String actorLogin);

    List<GitHubEventEntity> findByRepoNameAndTypeOrderByCreatedAtDesc(String repoName, String type);

    boolean existsByEventId(String eventId);
}
