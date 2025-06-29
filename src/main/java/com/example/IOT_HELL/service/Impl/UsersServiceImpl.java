package com.example.IOT_HELL.service.Impl;


import com.example.IOT_HELL.Exception.AlreadyExistsException;
import com.example.IOT_HELL.database.jpaRepo.IoTDataRepository;
import com.example.IOT_HELL.database.jpaRepo.UsersRepoJpa;
import com.example.IOT_HELL.payload.request.*;
import com.example.IOT_HELL.payload.response.UsersResponseAll;
import com.example.IOT_HELL.payload.response.UsersResponseSearch;
import com.example.IOT_HELL.database.entity.Users;
import com.example.IOT_HELL.service.UsersService;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class UsersServiceImpl implements UsersService {

    private final UsersRepoJpa usersRepoJpa;

    private final IoTDataRepository ioTDataRepository;

    private final JWTService jwtService;

    private final AuthenticationManager authenticationManager;


    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Override
    public void createUser(UsersCreateRequest request) {
        List<String> errors = new ArrayList<>();

        if (usersRepoJpa.existsByUserName(request.getUserName())) {
            errors.add("UserName already exists!");
        }
        if (usersRepoJpa.existsByEmail(request.getEmail())) {
            errors.add("Email already exists!");
        }
        if (usersRepoJpa.existsByPhoneNumber(request.getPhoneNumber())) {
            errors.add("Phone number already exists!");
        }

        if (!errors.isEmpty()) {
            throw new AlreadyExistsException.UserAlreadyExistsException(errors);
        }

        Users users = new Users();
        users.setUserName(request.getUserName())
                .setPassword(encoder.encode(request.getPassWord()))
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName())
                .setEmail(request.getEmail())
                .setPhoneNumber(request.getPhoneNumber())
                .setRole("user")
                .setFlagDelete("F");
        usersRepoJpa.save(users);
    }

    @Override
    public List<UsersResponseAll> getAllUsers() {
        System.out.println("From DataBase");
        List<Users> userList = usersRepoJpa.findAll();
        List<UsersResponseAll> resList = new ArrayList<>();
        for (Users users : userList) {
            if (!"T".equals(users.getFlagDelete())) {
                UsersResponseAll res = new UsersResponseAll();
                res.setUserName(users.getUserName())
                        .setFirstName(users.getFirstName())
                        .setLastName(users.getLastName())
                        .setFirstName(users.getFirstName())
                        .setLastName(users.getLastName())
                        .setEmail(users.getEmail())
                        .setPhoneNumber(users.getPhoneNumber());
                resList.add(res);
            }
        }
        return resList;
    }

    @Override
    public void deleteUser(long id)  {
        Users users = usersRepoJpa.findById(id).orElseThrow(() -> new AlreadyExistsException.DataNotFoundException("User ID : " + id + "not found data!!"));

        users.setFlagDelete("T");
        usersRepoJpa.save(users);
    }

    @Override
    public UsersResponseAll getUserProfile(long id) {
        Optional<Users> userOptional = usersRepoJpa.findById(id);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Users user = userOptional.get();

        if ("T".equals(user.getFlagDelete())) {
            throw new RuntimeException("User is deleted");
        }

        return new UsersResponseAll()
                .setUserName(user.getUserName())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .setPhoneNumber(user.getPhoneNumber());
    }

    @Override
    public void updateUser(long id, UsersUpdateRequest request)  {
        Users users = usersRepoJpa.findById(id).orElseThrow(() -> new AlreadyExistsException.DataNotFoundException("User ID : " + id + "not found data!!"));

        if (request.getUserName() != null) {
            users.setUserName(request.getUserName());
        }
        if (request.getFirstName() != null) {
            users.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            users.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            users.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            users.setPhoneNumber(request.getPhoneNumber());
        }
        usersRepoJpa.save(users);
    }

    @Override
    public String verify(UserLoginRequest user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassWord())
        );

        if (authentication.isAuthenticated()) {
            // ✅ ดึง `id` ของผู้ใช้จาก Database
            Optional<Users> userData = usersRepoJpa.findByUserName(user.getUserName());

            if (userData.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            }

            Long userId = userData.get().getId(); // ✅ ดึง `id`
            String username = userData.get().getUserName(); // ✅ ดึง `username`

            // ✅ สร้าง Token ด้วย `id` และ `username`
            return JWTService.generateToken(userId, username);
        }

        return "";
    }


    @Override
    public UsersResponseSearch getAllUsersSearch(UsersSearchSortReq request) {
        Sort sorting = Sort.by(request.getSortBy().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, request.getSortColumn());
        Pageable pageable = PageRequest.of(0, 10, sorting);
        Page<Users> userList = usersRepoJpa.findAll(getSpecification(request), pageable);
        UsersResponseSearch usersResponseSearch = new UsersResponseSearch();
        List<UsersResponseAll> resList = new ArrayList<>();
        for (Users user : userList.getContent()) {
            UsersResponseAll res = new UsersResponseAll();
            res.setUserName(user.getUserName())
                    .setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setEmail(user.getEmail())
                    .setPhoneNumber(user.getPhoneNumber());
            resList.add(res);
        }

        usersResponseSearch.setData(resList);
        usersResponseSearch.setTotal(userList.getTotalElements());
        usersResponseSearch.setTotalPage(userList.getTotalElements());
        return usersResponseSearch;
    }

    private Specification<Users> getSpecification(UsersSearchSortReq request) {

        return (root, query, Combining) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(request.getFilter())) {
                List<Predicate> predicatesOr = new ArrayList<>();
                predicatesOr.add(Combining.like(root.get("userName"),
                        "%" + request.getFilter() + "%"));
                predicatesOr.add(Combining.like(root.get("lastName"),
                        "%" + request.getFilter() + "%"));
                predicatesOr.add(Combining.like(root.get("firstName"),
                        "%" + request.getFilter() + "%"));
                Predicate OrFilter = Combining.or(predicatesOr.toArray(new Predicate[0]));
                predicates.add(OrFilter);
            }
            return Combining.and(predicates.toArray(new Predicate[0]));
        };
    }

}
