package com.sib.ibanklosucl.dto.esb;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelCusHdr {
    private String ChannelProtocol;

    public ChannelCusHdr(String channelProtocol) {
        this.ChannelProtocol = channelProtocol;
    }

}
