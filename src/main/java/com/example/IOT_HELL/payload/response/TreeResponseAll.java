package com.example.IOT_HELL.payload.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TreeResponseAll {

    private Long Id;
    private double latitude;
    private double longitude;
}
