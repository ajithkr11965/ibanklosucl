package com.sib.ibanklosucl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MenuList {
  private String menuID;
  private String menuDesc;
  private String menuUrl;
  private String icon;

  private Long orderid;

}
