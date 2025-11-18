package com.sib.ibanklosucl.service.doc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.model.doc.ManDocData;
import com.sib.ibanklosucl.repository.doc.ManDocDataRepositry;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ManDocService {

    @Autowired
    private ManDocDataRepositry repositry;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private UserSessionData usd;

    public List<ManDocData> findAllBySlno(Long slNo){
        Optional<List<ManDocData>> li=repositry.findBySlno(slNo);
        return li.orElse(null);
    }
    public boolean isDocDate(Long slNo,String dateStr){
       return repositry.isDocDateValid(slNo,dateStr)>0;
    }

    @Transactional
    public void updateStatus(Long slNo,String ppcno){
         repositry.updateStatus(slNo,ppcno);
    }
    @Transactional
    public void updateStatus(Long slNo,String ppcno,String docName){
         repositry.updateStatus(slNo,ppcno,docName);
    }

    public boolean isAllDocUploaded(Long slNo){
        return repositry.isAllDocUploaded(slNo)==0;
    }

    @Transactional
    public void saveManDoc(ManDoc manDoc,Long slNo,String wiNum){

        Optional<List<ManDocData>> del=repositry.findBySlno(slNo);
        if (del.isPresent()) {
            repositry.deleteAll(del.get());
        }
        for(ManDoc.DocsReqd md:manDoc.getDocsReqd()){
          ManDocData manDocData=new ManDocData();
          manDocData.setLastModUser(usd.getPPCNo());
          manDocData.setLastModDate(new Date());
          manDocData.setDocName(md.getName());
          manDocData.setDocDesc(md.getDescription());
          manDocData.setSlno(slNo);
          manDocData.setWiNum(wiNum);
          manDocData.setUploadFlg(false);
          manDocData.setDocDesc(md.getDescription());
          repositry.save(manDocData);
        }
    }

    public String generateHTML(ManDoc documentDTO) {
        StringBuilder htmlBuilder = new StringBuilder();


        // Download button for the base64 encoded document
        htmlBuilder.append("<div class='p-3 m-3'>");
        htmlBuilder.append("<button type='button' class='btn btn-sm btn-info' onclick='downloadPDF()'>Download PDF</button>");
        htmlBuilder.append("<input type='hidden' id='sancManpdf' value=\"").append(documentDTO.getDocument()).append("\"/>");
        htmlBuilder.append("</div>");

        // Table structure
        htmlBuilder.append("<table class='table table-sm table-bordered table-hover '>");
        htmlBuilder.append("<thead class='thead-dark'>");
        htmlBuilder.append("<tr>");
        htmlBuilder.append("<th>Description</th>");
        htmlBuilder.append("<th>File</th>");
        htmlBuilder.append("<th>Status</th>");
        htmlBuilder.append("<th>Upload</th>");
        htmlBuilder.append("</tr>");
        htmlBuilder.append("</thead>");
        htmlBuilder.append("<tbody>");

        // Loop through each document required and create a row with upload functionality
        for (ManDoc.DocsReqd doc : documentDTO.getDocsReqd()) {
            htmlBuilder.append("<tr>");
            htmlBuilder.append("<td>").append(doc.getDescription()).append("</td>");
            htmlBuilder.append("<td>");
            htmlBuilder.append("<input type='file' accept=\"application/pdf\" class='form-control fileInput' name='commonFiles' />");
            htmlBuilder.append("<input type='hidden'  name='commonFileNames' value='").append(doc.getDescription().replace(" ","_")).append("' />");
            htmlBuilder.append("<input type='hidden'  name='commonFileCodes' value='").append(doc.getName()).append("' />");
            htmlBuilder.append("</td>");
            htmlBuilder.append("<td class='tdstat'><span class=\"badge badge-secondary\">Pending</span></td>");
            htmlBuilder.append("<td><a href='#' class='btn btn-sm btn-bg-light allUploadBtn btn-icon-success btn-text-succes'><i class='ki-duotone ki-up-square fs-1'><span class='path1'></span><span class='path2'></span></i></a><td>");
            htmlBuilder.append("</tr>");
        }

        htmlBuilder.append("</tbody>");
        htmlBuilder.append("</table>");

        // Submit button
//        htmlBuilder.append("<div>");
//        //htmlBuilder.append("<button class='btn btn-sm btn-danger' id='allUploadBtn' type='submit'>Upload</button>");
//        htmlBuilder.append("<a href='#' class='btn btn-sm btn-danger' id='allUploadBtn'  >Upload</a>");
//        htmlBuilder.append("</div>");


        return htmlBuilder.toString();
    }

}
