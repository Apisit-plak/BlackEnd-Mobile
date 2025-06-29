package com.example.IOT_HELL.database.jpaRepo;

import com.example.IOT_HELL.database.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UsersRepoJpa extends JpaRepository<Users, Long> {


    Page<Users> findAll(Specification<Users> specification, Pageable pageable);

    boolean existsByUserName(String userName);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);


    Optional<Users> findByUserName(String userName);
}