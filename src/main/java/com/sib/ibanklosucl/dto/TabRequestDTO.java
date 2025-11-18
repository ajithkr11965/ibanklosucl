package com.sib.ibanklosucl.dto;

import com.sib.ibanklosucl.dto.ocr.FileRequest;
import com.sib.ibanklosucl.dto.ocr.OcrParsed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TabRequestDTO {

    private String appid;
    private String slno;
    private String winum;

    private OcrParsed.PanDoc panDoc;
    private UIDData uidDoc;

    private FileRequest fileRequest;


}
