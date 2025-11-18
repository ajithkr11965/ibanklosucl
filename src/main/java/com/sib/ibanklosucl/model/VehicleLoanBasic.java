package com.sib.ibanklosucl.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_BASIC")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanBasic {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vlbasic_seq")
    @SequenceGenerator(name = "vlbasic_seq", sequenceName = "IBANKLOSUCL.VLBASIC_SEQ", allocationSize = 1)
    private Long ino;

    @Column(name = "wi_num", length = 13)
    private String wiNum;

    @Column(name = "slno")
    private Long slno;

    @Column(name = "applicant_id")
    private Long applicantId;

    @OneToOne
    @JoinColumn(name = "applicant_id", insertable = false, updatable = false)
    private VehicleLoanApplicant bsapplicant;
    @Column(name = "req_ip_addr", length = 19)
    private String reqIpAddr;

    @Column(name = "applicant_name", length = 80)
    private String applicantName;

    @Column(name = "applicant_dob")
    private Date applicantDob;
    @Column(name = "age")
    private Long age;

    @Column(name = "father_name", length = 80)
    private String fatherName;

    @Column(name = "mother_name", length = 80)
    private String motherName;

    @Column(name = "marital_status", length = 15)
    private String maritalStatus;

    @Column(name = "spouse_name", length = 80)
    private String spouseName;

    @Column(name = "gender")
    private String gender;

    @Column(name = "salutation", length = 5)
    private String salutation;
    @Column(name = "occupation", length = 5)
    private String occupation;

    @Column(name = "mobile_cntry_code", length = 5)
    private String mobileCntryCode;

    @Column(name = "mobile_no", length = 15)
    private String mobileNo;

    @Column(name = "email_id", length = 60)
    private String emailId;

    @Column(name = "cmuser", length = 10)
    private String cmuser;

    @Column(name = "cmdate")
    private Date cmdate;

    @Column(name = "del_flg", length = 1)
    private String delFlg;

    @Column(name = "deluser", length = 10)
    private String deluser;

    @Column(name = "deldate")
    @Temporal(TemporalType.DATE)
    private Date deldate;

    @Column(name = "delhomesol", length = 5)
    private String delhomesol;

    @Column(name = "home_sol", length = 5)
    private String homeSol;

    @Column(name = "addr1", length = 100)
    private String addr1;

    @Column(name = "addr2", length = 100)
    private String addr2;

    @Column(name = "addr3", length = 100)
    private String addr3;

    @Column(name = "city", length = 10)
    private String city;

    @Column(name = "city_desc", length = 50)
    private String citydesc;

    @Column(name = "state", length = 10)
    private String state;
    @Column(name = "state_desc", length = 50)
    private String statedesc;
    @Column(name = "country", length = 10)
    private String country;
    @Column(name = "country_desc", length = 50)
    private String countrydesc;
    @Column(name = "pin", length = 10)
    private String pin;

    @Column(name = "duration_stay")
    private Integer durationStay;

    @Column(name = "residence_type", length = 20)
    private String residenceType;


    @Column(name = "same_as_per", length = 1)
    private String sameAsPer;


    @Column(name = "com_addr1", length = 100)
    private String comAddr1;


    @Column(name = "com_addr2", length = 100)
    private String comAddr2;

    @Column(name = "com_addr3", length = 100)
    private String comAddr3;

    @Column(name = "com_city", length = 10)
    private String comCity;
    @Column(name = "com_city_desc", length = 50)
    private String comCityedesc;
    @Column(name = "com_state", length = 10)
    private String comState;
    @Column(name = "com_state_desc", length = 50)
    private String comStatedesc;
    @Column(name = "com_country", length = 10)
    private String comCountry;
    @Column(name = "com_country_desc", length = 50)
    private String comCountrydesc;
    @Column(name = "com_pin", length = 10)
    private String comPin;

    @Column(name = "com_duration_stay")
    private Integer comDurationStay;
    @Column(name = "comm_Distance_from_Branch")
    private Integer commdistanceFromBranch;
    @Column(name = "Distance_from_Branch")
    private Integer distanceFromBranch;

    @Column(name = "com_residence_type", length = 20)
    private String comResidenceType;

    @Column(name = "addrdocname_bpm", length = 100)
    private String addrdocnameBpm;

    @Column(name = "preferred_flag", length = 1)
    private String preferredFlag;

    @Column(name = "current_residence_flag", length = 1)
    private String currentResidenceFlag;
    @Column(name = "los_dedup_count")
    private int losDedupCount;
    @Column(name = "finacle_dedup_count")
    private int finacleDedupCount;
    @Column(name = "politically_exposed")
    private String  politicallyExposed;
    @Column(name = "education")
    private String  education;

    @Column(name = "annual_income", length = 10)
    private String annualIncome;


    @Column(name = "cpoa", length = 30)
    private String cpoa;
    @Column(name = "cpoa_doc",columnDefinition = "CLOB")
    private String cpoaDoc;
    @Column(name = "cpoa_ext", length = 5)
    private String cpoaExt;


}
