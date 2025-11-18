package com.sib.ibanklosucl.dto.esb;

import lombok.Data;

@Data
public class UIDDemographicBody {
    private String uid;
    private String clientId;
    private String securityToken;
    private String name;
    private String yob;
    private String channel_id;
}
