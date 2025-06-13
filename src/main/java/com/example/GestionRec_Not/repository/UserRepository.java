package com.example.GestionRec_Not.repository;

import com.example.GestionRec_Not.entities.Userr;
import com.example.GestionRec_Not.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<Userr, Long> {
    List<Userr> findByRole(Role role);
}
