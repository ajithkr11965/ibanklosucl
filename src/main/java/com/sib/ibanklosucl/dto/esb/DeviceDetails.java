package com.sib.ibanklosucl.dto.esb;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceDetails {
    private String MobileNumber;
    private String OS;
    private String BrowserType;
    private String DeviceID;
    private GeoLocation GeoLocation;
    private String IMEINumber;
    private String ClientIP;

}
