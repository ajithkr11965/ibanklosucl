package com.sib.ibanklosucl.model.menuaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Immutable
@Table(name = "MISMATLOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MISMenuAccessNtLos {

    @EmbeddedId
    private MISMenuAccessNtLosId id;

    @Column(name = "SCALE1")
    private String scale1;

    @Column(name = "SCALE2")
    private String scale2;

    @Column(name = "SCALE3")
    private String scale3;

    @Column(name = "SCALE4")
    private String scale4;

    @Column(name = "SCALE5")
    private String scale5;

    @Column(name = "SCALE6")
    private String scale6;

    @Column(name = "SCALE7")
    private String scale7;

    @Column(name = "SCALE8")
    private String scale8;

    @Column(name = "SCALE9")
    private String scale9;

    @Column(name = "SCALE10")
    private String scale10;

    @Column(name = "SCALE11")
    private String scale11;

    @Column(name = "SCALE12")
    private String scale12;

    @Column(name = "SCALE13")
    private String scale13;

    @Column(name = "BRANCHHEAD")
    private String branchHead;

    @Column(name = "ROHEAD")
    private String roHead;

    @Column(name = "CLKSCALE")
    private String clkScale;

    @Column(name = "NEWSCALE1")
    private String newScale1;

    @Column(name = "NEWSCALE2")
    private String newScale2;

    @Column(name = "NEWSCALE3")
    private String newScale3;

    @Column(name = "NEWSCALE4")
    private String newScale4;

    @Column(name = "NEWSCALE5")
    private String newScale5;

    @Column(name = "NEWSCALE6")
    private String newScale6;

    @Column(name = "NEWSCALE7")
    private String newScale7;

    @Column(name = "DELFLAG")
    private String delFlag;

    @Column(name = "CMUSER")
    private String cmuser;

    @Column(name = "CMDATE")
    private Date cmdate;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MISMenuAccessNtLosId implements Serializable {

        @Column(name = "ACCESSID")
        private String accessId;

        @Column(name = "MENUID")
        private String menuId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MISMenuAccessNtLosId that = (MISMenuAccessNtLosId) o;
            return Objects.equals(accessId, that.accessId) &&
                    Objects.equals(menuId, that.menuId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(accessId, menuId);
        }
    }
}
