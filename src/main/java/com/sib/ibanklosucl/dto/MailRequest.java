package com.sib.ibanklosucl.dto;

import lombok.Data;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Data
public class MailRequest {


    private String from;
    private String[] to;
    private String[] cc;
    private String[] bcc;

    private String subject;

    private String content;

    private List<String> imagePaths;

    private boolean iscontentHtml=true;

    public List<String> getImages(){
        List<String> images=new ArrayList<>();
        images.add("static/images/siblogo.png");
        images.add("static/images/fb.png");
        images.add("static/images/insta.png");
        images.add("static/images/youtube_icon.png");
        images.add("static/images/twitter.png");
                return images;
    }
    public  void addToMailArray(String  mail, MailRequest target) {
        String[] originalArray = target.getTo()==null? new String[0]:target.getTo();

        String[] newArray = Stream.concat(Arrays.stream(originalArray), Stream.of(mail))
                .toArray(String[]::new);
        target.setTo(newArray);
    }
     public  void addCcMailArray(String  mail, MailRequest target) {
        String[] originalArray = target.getCc()==null? new String[0]:target.getCc();

        String[] newArray = Stream.concat(Arrays.stream(originalArray), Stream.of(mail))
                .toArray(String[]::new);
        target.setCc(newArray);
    }

}
