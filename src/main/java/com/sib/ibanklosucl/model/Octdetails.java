package com.sib.ibanklosucl.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "OCCUPATION_CODE_DETAILS")
public class Octdetails {
    @Id
    @Column(name = "OCT_VALUE")
    String octValue;
    @Column(name = "OCT_DESC")
    String octDesc;
    @Column(name = "CATEGORY")
    String category;
    @Column(name = "DEL_FLAG")
    String delFlag;
}
