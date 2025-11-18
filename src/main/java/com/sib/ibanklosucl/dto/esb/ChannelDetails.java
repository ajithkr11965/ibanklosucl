package com.sib.ibanklosucl.dto.esb;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelDetails {
    private String ChannelID;
    private String ChannelType;
    private String ChannelSubClass;
    private String BranchCode;
    private ChannelCusHdr ChannelCusHdr;

}
