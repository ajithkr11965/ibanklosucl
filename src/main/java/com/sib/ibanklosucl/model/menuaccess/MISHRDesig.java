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
@Table(name = "MIS_HR_DESIG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MISHRDesig {

        @EmbeddedId
        private MISHRDesigId id;

        @Column(name = "DESIG")
        private String desig;

        @Column(name = "CADRE")
        private String cadre;

        @Column(name = "START_DATE")
        private Date startDate;

        @Column(name = "END_DATE")
        private Date endDate;

        @Column(name = "VDATE")
        private Date vdate;

        @Embeddable
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MISHRDesigId implements Serializable {

                @Column(name = "PPCNO")
                private String ppcNo;

                @Column(name = "CATEGORY")
                private String category;

                @Override
                public boolean equals(Object o) {
                        if (this == o) return true;
                        if (o == null || getClass() != o.getClass()) return false;
                        MISHRDesigId that = (MISHRDesigId) o;
                        return Objects.equals(ppcNo, that.ppcNo) &&
                                Objects.equals(category, that.category);
                }

                @Override
                public int hashCode() {
                        return Objects.hash(ppcNo, category);
                }
        }
}
