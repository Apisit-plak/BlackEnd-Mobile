package com.example.IOT_HELL.database.jpaRepo;


import com.example.IOT_HELL.database.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository <Users, Long> {

        Users findByUserName(String userName);

}
