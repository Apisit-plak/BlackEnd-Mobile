package com.example.IOT_HELL.payload.request;

import lombok.Data;


@Data
public class UsersSearchSortReq {
    private String sortColumn;
    private String sortBy;
    private String filter;
}
