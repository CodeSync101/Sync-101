package com.example.GestionRec_Not.repository;

import com.example.GestionRec_Not.entities.Userr;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Userr, Long> {
}
