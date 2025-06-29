package com.example.IOT_HELL.payload.response;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class UsersResponseSearch {
    private List<UsersResponseAll> data;
    private Long totalPage;
    private Long total;
}
