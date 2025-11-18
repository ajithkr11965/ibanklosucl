package com.sib.ibanklosucl.model.menuaccess;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class DashboardItem {
    private Long id;
    private String menuId;
    private String logoClass;
    private String iconClass;
    private String title;
    private String valueId;
    private String iconName;
    @Transient
    private int count;
    private int spanCount;
    @Transient
    private String countDirection;
    @Transient
    private int variation;

}


