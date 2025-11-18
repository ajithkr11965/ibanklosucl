package com.sib.ibanklosucl.model;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_VKYC")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Audited
public class VehicleLoanVkyc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SLNO", nullable = false)
    private Long slno;
    @Column(name = "SESSION_ID", length = 100)
    private String sessionId;
    @Column(name = "MOBILE_NO", length = 10)
    private String mobileNo;
    @Column(name = "LOS_AADHAAR_REF_KEY", length = 50)
    private String losAadhaarRefKey;
    @Column(name = "WI_NUM", length = 13)
    private String wiNum;
    @Column(name = "EXPIRY", length = 50)
    private String expiry;
    @Column(name = "APP_ID")
    private Long appId;
    @Column(name = "LOS_SLNO")
    private Long losSlno;
    @Column(name = "LAT", length = 10)
    private String lat;
    @Column(name = "LONGT", length = 10)
    private String longt;
    @Column(name = "REQUEST_DT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDt;
    @Column(name = "AGENT_AVAIL", length = 1)
    private String agentAvail;
    @Column(name = "HOLIDAY_CHECK", length = 1)
    private String holidayCheck;
    @Column(name = "OTP_COUNT")
    private Integer otpCount;
    @Column(name = "OTPTIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date otpTime;
    @Column(name = "OTPRETRY")
    private Integer otpRetry;
    @Column(name = "OTP_VALIDATE", length = 1)
    private String otpValidate;
    @Column(name = "AADHAAR_NO", length = 30)
    private String aadhaarNo;
    @Column(name = "AADHAAR_OTP_SENT", length = 1)
    private String aadhaarOtpSent;
    @Column(name = "AADHAAR_OTP_VALIDATE", length = 1)
    private String aadhaarOtpValidate;
    @Column(name = "LOS_AADHAAR_CHECK", length = 1)
    private String losAadhaarCheck;
    @Column(name = "PAN", length = 10)
    private String pan;
    @Column(name = "PAN_VALIDATED", length = 1)
    private String panValidated;
    @Column(name = "AGENT_AVAIL1", length = 1)
    private String agentAvail1;
    @Column(name = "HOLIDAY_CHECK1", length = 1)
    private String holidayCheck1;
    @Column(name = "AADHAAR_MASK", length = 12)
    private String aadhaarMask;
    @Column(name = "UIDTIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uidTime;
    @Column(name = "UIDRETRY")
    private Integer uidRetry;
    @Column(name = "AADHAAR_REF_KEY", length = 20)
    private String aadhaarRefKey;
    @Column(name = "AADHAAR_TRANID", length = 30)
    private String aadhaarTranId;
    @Column(name = "CUST_TITLE", length = 20)
    private String custTitle;
    @Column(name = "CUST_NAME", length = 100)
    private String custName;
    @Column(name = "PREIVIEW", length = 1)
    private String preview;
    @Column(name = "VKC_INITIATE", length = 1)
    private String vkcInitiate;
    @Column(name = "VKYC_SUBMIT_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date vkycSubmitTime;
    @Column(name = "VKYC_REDIRECTURL", length = 300)
    private String vkycRedirectUrl;
    @Column(name = "VKYC_AADHAAR_REF_KEY", length = 50)
    private String vkycAadhaarRefKey;
    @Column(name = "VKYC_NAME", length = 100)
    private String vkycName;
    @Column(name = "VKYC_GENDER", length = 10)
    private String vkycGender;
    @Column(name = "VKYC_DOB", length = 10)
    private String vkycDob;
    @Column(name = "VKYC_VCIP_STATUS", length = 20)
    private String vkycVcipStatus;
    @Column(name = "VKYC_STATUS_DECR", length = 100)
    private String vkycStatusDecr;
    @Column(name = "VKYC_LATITUDE", length = 10)
    private String vkycLatitude;
    @Column(name = "VKYC_LONGITUDE", length = 10)
    private String vkycLongitude;
    @Column(name = "VKYC_PHOTO_MATCH", length = 10)
    private String vkycPhotoMatch;
    @Column(name = "VKYC_FAILURE_REASON", length = 100)
    private String vkycFailureReason;
    @Column(name = "VKYC_CUSTOMER_ID", length = 100)
    private String vkycCustomerId;
    @Lob
    @Column(name = "VKYC_PHOTO")
    private String vkycPhoto;
    @Column(name = "VKYC_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date vkycTime;
    @Column(name = "VKYC_UNIQUEID", length = 50)
    private String vkycUniqueId;
    @Column(name = "CHANNEL", length = 10)
    private String channel;
    @Column(name = "CUST_ID", length = 20)
    private String custId;


}
