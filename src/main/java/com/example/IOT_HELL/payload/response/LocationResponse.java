package com.example.IOT_HELL.payload.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LocationResponse {
    private Number latitude;
    private Number longitude;
}
