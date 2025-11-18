package com.sib.ibanklosucl.dto.esb;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UIDBody {
    private String channel_id;
    private String uid;
    private String clientId;
    private String securityToken;
}
