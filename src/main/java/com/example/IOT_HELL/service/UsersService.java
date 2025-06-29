package com.example.IOT_HELL.service;




import com.example.IOT_HELL.payload.request.*;
import com.example.IOT_HELL.payload.response.UsersResponseAll;
import com.example.IOT_HELL.payload.response.UsersResponseSearch;

import java.util.List;

public interface UsersService {

    void createUser(UsersCreateRequest request);

    List<UsersResponseAll> getAllUsers();

    void deleteUser(long id) throws Exception;

    UsersResponseAll getUserProfile(long id);

    void updateUser(long id, UsersUpdateRequest request) throws Exception;

    String verify(UserLoginRequest user);

    UsersResponseSearch getAllUsersSearch(UsersSearchSortReq request);


}
