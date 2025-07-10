package com.example.backend.Repository;

import com.example.backend.Entity.Pull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PullRepository extends JpaRepository<Pull,Long> {
}
