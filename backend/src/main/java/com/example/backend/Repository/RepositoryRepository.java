package com.example.backend.Repository;

import com.example.backend.Entity.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface RepositoryRepository extends JpaRepository<Repository, Long> {


    Repository findByName(String name);
}
