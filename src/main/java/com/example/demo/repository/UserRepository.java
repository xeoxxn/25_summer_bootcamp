package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//JpaRepository는 Spring Data JPA가 제공하는 기본 CRUD Repository
public interface UserRepository extends JpaRepository<com.example.demo.entity.User, Long> {
    // userId가 존재하는지 확인하는 메서드 / Spring Data JPA가 구현체를 자동으로 만들어줌
    boolean existsByUserId(String userId);
    // userId로 User를 찾는 메서드, Optional<User> 형태로 변환
    Optional<User> findByUserId(String userId);
}
