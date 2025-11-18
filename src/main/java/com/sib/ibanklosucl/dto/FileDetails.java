package com.sib.ibanklosucl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileDetails {
    private String filecode;
    private String filename;
    private String base64Content;
    private String extention;

    private String filetype;
    private String appid;

}
