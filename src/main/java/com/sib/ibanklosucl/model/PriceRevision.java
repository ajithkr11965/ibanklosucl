package com.sib.ibanklosucl.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VLPRICEREVISION")
@ToString
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class PriceRevision {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "price_revision_seq")
    @SequenceGenerator(name = "price_revision_seq", sequenceName = "IBANKLOSUCL.VLPRICEREVISION_SEQ", allocationSize = 1)
    private Long id;

   // @JsonProperty(value = "wiNum")
    private String wiNum;
  //  @JsonProperty(value = "slno")
    private Long slno;
 //   @JsonProperty(value = "applicantId")
    private Long applicantId;
   // @JsonProperty(value = "slno")
    private String cmuser;
  //  @JsonProperty(value = "cmdate")
    private Date cmdate;
  //  @JsonProperty(value = "exshowroomPrice")
    private String exshowroomPrice;
 //   @JsonProperty(value = "insurancePrice")
    private String insurancePrice;
    private String extendedWarranty;
  //  @JsonProperty(value = "rtoPrice")
    private String rtoPrice;
  //  @JsonProperty(value = "otherPrice")
    private String otherPrice;
  //  @JsonProperty(value = "slno")
    private String onroadPrice;
  //  @JsonProperty(value = "remarks")
    private String remarks;
    // Getters and Setters
}
