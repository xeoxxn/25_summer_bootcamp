package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<com.example.demo.entity.User, Long> {
    boolean existsByUserId(String email);
    Optional<User> findByUserId(String userId);
}
