package com.sib.ibanklosucl.dto.dashboard;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Message {
    private String msg;
    private String homeSol;
    private String cmuser;
    private String clr;
    private boolean imgExists;

    // Constructor, getters, and setters

    public Message(String msg, String homeSol, String cmuser, String clr, boolean imgExists) {
        this.msg = msg;
        this.homeSol = homeSol;
        this.cmuser = cmuser;
        this.clr = clr;
        this.imgExists = imgExists;
    }

    // Getters and Setters
}
