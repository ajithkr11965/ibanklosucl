package com.sib.ibanklosucl.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "MIS_REFERENCE_CODE")
@IdClass(MisrctKey.class)
public class Misrct {
    @Id
    private String codevalue;
    @Id
    private String codetype;
    private String codedesc;
    private String delflag;

}
