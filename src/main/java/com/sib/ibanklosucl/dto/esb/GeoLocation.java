package com.sib.ibanklosucl.dto.esb;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeoLocation {
    private String Latitude;
    private String Longitude;

    public GeoLocation(String latitude, String longitude) {
        this.Latitude = latitude;
        this.Longitude = longitude;
    }
}
