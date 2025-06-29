package com.example.IOT_HELL.service.Impl;



import com.example.IOT_HELL.Exception.AlreadyExistsException;
import com.example.IOT_HELL.database.UserPrincipal;
import com.example.IOT_HELL.database.entity.Users;
import com.example.IOT_HELL.database.jpaRepo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws BadCredentialsException {
        Users user = userRepo.findByUserName(username);
        if (user == null) {
            throw new AlreadyExistsException.BadCredentialsException("User or password not found");
        }
        return new UserPrincipal(user);
    }


}
