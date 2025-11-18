package com.sib.ibanklosucl.dto.mssf;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
public class MSSFCustomerSummaryDTO {
    private String refNo;
    private String customerName;
    private Long mobile;
    private String email;
    private Double loanAmount;
    private LocalDateTime createdDate;
    private String dealerCode;
    private String dealerNameCode;
    private String dealerName;        // Separate dealer name
    private String dealerLocation;    // Separate dealer location
    private String dealerNameAndLocation; // Combined dealer name and location
    private Integer daysSinceCreated;

    // Constructor for basic queries (6 parameters) - matches original repository query
    public MSSFCustomerSummaryDTO(String refNo, String customerName, Long mobile,
                                 Double loanAmount, LocalDateTime createdDate, String dealerCode) {
        this.refNo = refNo;
        this.customerName = customerName;
        this.mobile = mobile;
        this.loanAmount = loanAmount;
        this.createdDate = createdDate;
        this.dealerCode = dealerCode;
        calculateDaysSinceCreated();
    }

    // Constructor for queries with dealer name and location combined (7 parameters)
    public MSSFCustomerSummaryDTO(String refNo, String customerName, Long mobile,
                                 Double loanAmount, LocalDateTime createdDate, String dealerCode,
                                 String dealerNameAndLocation) {
        this.refNo = refNo;
        this.customerName = customerName;
        this.mobile = mobile;
        this.loanAmount = loanAmount;
        this.createdDate = createdDate;
        this.dealerCode = dealerCode;
        this.dealerNameAndLocation = dealerNameAndLocation;
        this.dealerNameCode = dealerNameAndLocation; // For backward compatibility
        parseDealerNameAndLocation(dealerNameAndLocation);
        calculateDaysSinceCreated();
    }

    // Constructor for queries with separate dealer name and location (8 parameters)
    public MSSFCustomerSummaryDTO(String refNo, String customerName, Long mobile,
                                 Double loanAmount, LocalDateTime createdDate, String dealerCode,
                                 String dealerName, String dealerLocation) {
        this.refNo = refNo;
        this.customerName = customerName;
        this.mobile = mobile;
        this.loanAmount = loanAmount;
        this.createdDate = createdDate;
        this.dealerCode = dealerCode;
        this.dealerName = dealerName;
        this.dealerLocation = dealerLocation;
        this.dealerNameAndLocation = (dealerName != null ? dealerName : "") +
                                   (dealerLocation != null ? " - " + dealerLocation : "");
        this.dealerNameCode = this.dealerNameAndLocation; // For backward compatibility
        calculateDaysSinceCreated();
    }

    // Constructor for extended queries with email (9 parameters)
    public MSSFCustomerSummaryDTO(String refNo, String customerName, Long mobile, String email,
                                 Double loanAmount, LocalDateTime createdDate, String dealerCode,
                                 String dealerName, String dealerLocation) {
        this.refNo = refNo;
        this.customerName = customerName;
        this.mobile = mobile;
        this.email = email;
        this.loanAmount = loanAmount;
        this.createdDate = createdDate;
        this.dealerCode = dealerCode;
        this.dealerName = dealerName;
        this.dealerLocation = dealerLocation;
        this.dealerNameAndLocation = (dealerName != null ? dealerName : "") +
                                   (dealerLocation != null ? " - " + dealerLocation : "");
        this.dealerNameCode = this.dealerNameAndLocation; // For backward compatibility
        calculateDaysSinceCreated();
    }

    // All args constructor
    public MSSFCustomerSummaryDTO(String refNo, String customerName, Long mobile, String email,
                                 Double loanAmount, LocalDateTime createdDate, String dealerCode,
                                 String dealerName, String dealerLocation, String dealerNameAndLocation,
                                 Integer daysSinceCreated) {
        this.refNo = refNo;
        this.customerName = customerName;
        this.mobile = mobile;
        this.email = email;
        this.loanAmount = loanAmount;
        this.createdDate = createdDate;
        this.dealerCode = dealerCode;
        this.dealerName = dealerName;
        this.dealerLocation = dealerLocation;
        this.dealerNameAndLocation = dealerNameAndLocation;
        this.dealerNameCode = dealerNameAndLocation; // For backward compatibility
        this.daysSinceCreated = daysSinceCreated;
    }

    /**
     * Parse combined dealer name and location string
     */
    private void parseDealerNameAndLocation(String combined) {
        if (combined != null && combined.contains(" - ")) {
            String[] parts = combined.split(" - ", 2);
            this.dealerName = parts[0].trim();
            this.dealerLocation = parts.length > 1 ? parts[1].trim() : "";
        } else {
            this.dealerName = combined;
            this.dealerLocation = "";
        }
    }

    /**
     * Calculate days since the application was created
     */
    private void calculateDaysSinceCreated() {
        if (this.createdDate != null) {
            long daysBetween = ChronoUnit.DAYS.between(this.createdDate.toLocalDate(), LocalDate.now());
            this.daysSinceCreated = (int) daysBetween;
        }
    }

    /**
     * Get formatted loan amount for display
     */
    public String getFormattedLoanAmount() {
        if (loanAmount != null) {
            return String.format("₹ %,.2f", loanAmount);
        }
        return "₹ 0.00";
    }

    /**
     * Get formatted creation date for display
     */
    public String getFormattedCreatedDate() {
        if (createdDate != null) {
            return createdDate.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        }
        return "";
    }

    /**
     * Get dealer name safely
     */
    public String getDealerNameSafe() {
        return dealerName != null ? dealerName : "N/A";
    }

    /**
     * Get dealer location safely
     */
    public String getDealerLocationSafe() {
        return dealerLocation != null ? dealerLocation : "N/A";
    }

    /**
     * Get combined dealer name and location safely
     */
    public String getDealerNameAndLocationSafe() {
        if (dealerNameAndLocation != null && !dealerNameAndLocation.trim().isEmpty()) {
            return dealerNameAndLocation;
        }
        return getDealerNameSafe() + " - " + getDealerLocationSafe();
    }

    /**
     * Check if application is urgent (>7 days old)
     */
    public boolean isUrgent() {
        return daysSinceCreated != null && daysSinceCreated > 7;
    }

    /**
     * Check if application is pending for long time (>3 days old)
     */
    public boolean isLongPending() {
        return daysSinceCreated != null && daysSinceCreated > 3;
    }
}
