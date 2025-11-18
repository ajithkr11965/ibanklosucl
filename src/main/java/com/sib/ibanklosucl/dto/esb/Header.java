package com.sib.ibanklosucl.dto.esb;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Header {
    private DeviceDetails DeviceDetails;
    private ChannelDetails ChannelDetails;
    private String Timestamp;

}
