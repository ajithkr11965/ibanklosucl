package com.sib.ibanklosucl.service.doc;

import lombok.Data;

import java.util.List;

@Data
public class ManDoc {
    private String document;
    private List<DocsReqd> docsReqd;

    @Data
      public static class DocsReqd{
          private String name;
          private String description;
      }

}
