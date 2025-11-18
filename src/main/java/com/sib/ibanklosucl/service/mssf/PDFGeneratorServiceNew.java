package com.sib.ibanklosucl.service.mssf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.sib.ibanklosucl.model.mssf.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PDFGeneratorServiceNew {
    // Document Constants
    private static final float MARGIN = 36f;
    private static final Rectangle PAGE_SIZE = PageSize.A4;

    // Colors
    private static final BaseColor CORPORATE_RED = new BaseColor(199, 34, 42);
    private static final BaseColor LIGHT_GRAY = new BaseColor(248, 248, 248);
    private static final BaseColor BORDER_GRAY = new BaseColor(224, 224, 224);
    private static final BaseColor TEXT_DARK = new BaseColor(51, 51, 51);
    private static final BaseColor TEXT_GRAY = new BaseColor(102, 102, 102);

    // Typography
    private static final Font HEADER_TITLE = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, TEXT_DARK);
    private static final Font SECTION_TITLE = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, TEXT_DARK);
    private static final Font LABEL_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, TEXT_GRAY);
    private static final Font VALUE_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, TEXT_DARK);
    private static final Font REFERENCE_VALUE = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, TEXT_DARK);
    private static final Font FOOTER_FONT = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, TEXT_GRAY);
//src/main/resources/static/assets/images/manufacturer/maruti-logo.png
    private static final String LOGO_PATH = "classpath:static/assets/images/manufacturer/maruti-logo.png";

    // Layout Constants
    private static final float SECTION_SPACING = 8f;
    private static final float CONTENT_PADDING = 5f;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final String NOT_AVAILABLE = " ";
    private final ResourceLoader resourceLoader;

    public PDFGeneratorServiceNew(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    // Page Event Handler for Headers and Footers
    private class PageEventHandler extends PdfPageEventHelper {
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            // Add footer
            float footerY = document.bottom() - 20;

            // Add footer line
            cb.setLineWidth(0.5f);
            cb.setColorStroke(BORDER_GRAY);
            cb.moveTo(document.left(), document.bottom());
            cb.lineTo(document.right(), document.bottom());
            cb.stroke();

            // Add footer text
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                new Phrase("CONFIDENTIAL DOCUMENT ", FOOTER_FONT),
                document.left(), footerY, 0);

            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                new Phrase(" This document is electronically generated and does not require signature", FOOTER_FONT),
                (document.right() + document.left())/2, footerY, 0);

            // Page numbering
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                new Phrase(String.format("Page %d", writer.getPageNumber()), FOOTER_FONT),
                document.right(), footerY, 0);
        }
    }

    public byte[] generateMSSFLeadPDF(MSSFCustomerData data) throws DocumentException {
        Document document = new Document(PAGE_SIZE, MARGIN, MARGIN, MARGIN, MARGIN);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(new PageEventHandler());
            document.open();

            addHeader(document, data);
            addPersonalDetails(document, data);
            addEmploymentBusinessDetails(document, data);
            addAddressSection(document, data);
            addReferenceDetails(document, data);
            addLoanDetails(document, data);
            addDealerDetails(document, data);
            addAuditDetails(document, data);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new DocumentException("Error generating PDF: " + e.getMessage());
        }
    }

    private void addHeader(Document document, MSSFCustomerData data) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{3, 5, 4});
        headerTable.setSpacingAfter(10f);

        // Logo Cell
        PdfPCell logoCell = new PdfPCell();
        try {
            Resource resource = resourceLoader.getResource(LOGO_PATH);
            byte[] logoBytes = StreamUtils.copyToByteArray(resource.getInputStream());
            Image logo = Image.getInstance(logoBytes);
            logo.scaleToFit(100, 25);
            logoCell.addElement(logo);

        } catch (Exception e) {
            logoCell.addElement(new Phrase("[Logo]", LABEL_FONT));
        }
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setPadding(CONTENT_PADDING);
        headerTable.addCell(logoCell);

        // Title Cell
        PdfPCell titleCell = new PdfPCell(new Phrase("Maruti Suzuki Smart Finance Lead Details", HEADER_TITLE));
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        titleCell.setBorder(Rectangle.NO_BORDER);
        headerTable.addCell(titleCell);

        // Reference Information Cell
        PdfPCell refCell = new PdfPCell();
        refCell.addElement(new Paragraph("Reference Number", LABEL_FONT));
        refCell.addElement(new Paragraph(data.getRefNo(), REFERENCE_VALUE));
        refCell.addElement(new Paragraph("Generated: " + formatDateTime(LocalDateTime.now()), LABEL_FONT));
        refCell.setBorder(Rectangle.NO_BORDER);
        refCell.setPadding(CONTENT_PADDING);
        headerTable.addCell(refCell);

        document.add(headerTable);
        addSeparatorLine(document);
    }

    private void addSectionHeader(Document document, String title) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingBefore(SECTION_SPACING);
        headerTable.setSpacingAfter(CONTENT_PADDING);

        PdfPCell headerCell = new PdfPCell(new Phrase(title.toUpperCase(), SECTION_TITLE));
        headerCell.setBackgroundColor(LIGHT_GRAY);
        headerCell.setPadding(CONTENT_PADDING);
        headerCell.setBorderColor(BORDER_GRAY);
        headerCell.setBorderWidth(0.5f);
        headerTable.addCell(headerCell);

        document.add(headerTable);
        addAccentLine(document);
    }

    private void addSeparatorLine(Document document) throws DocumentException {
        PdfPTable lineTable = new PdfPTable(1);
        lineTable.setWidthPercentage(100);
        PdfPCell lineCell = new PdfPCell();
        lineCell.setFixedHeight(0.5f);
        lineCell.setBackgroundColor(BORDER_GRAY);
        lineCell.setBorder(Rectangle.NO_BORDER);
        lineTable.addCell(lineCell);
        document.add(lineTable);
    }

    private void addAccentLine(Document document) throws DocumentException {
        PdfPTable accentTable = new PdfPTable(1);
        accentTable.setWidthPercentage(100);
        PdfPCell accentCell = new PdfPCell();
        accentCell.setFixedHeight(1f);
        accentCell.setBackgroundColor(CORPORATE_RED);
        accentCell.setBorder(Rectangle.NO_BORDER);
        accentTable.addCell(accentCell);
        document.add(accentTable);
    }

    private PdfPCell createLabelCell(String text) {
    PdfPCell cell = new PdfPCell(new Phrase(text, LABEL_FONT));
    cell.setPadding(8f);
    cell.setBorderColor(BORDER_GRAY);
    cell.setBackgroundColor(LIGHT_GRAY);
    cell.setMinimumHeight(25f); // Ensure consistent height
    return cell;
}


    private PdfPCell createValueCell(String text) {
    String displayText = (text == null || text.trim().isEmpty()) ? NOT_AVAILABLE : text;
    PdfPCell cell = new PdfPCell(new Phrase(displayText, VALUE_FONT));
    cell.setPadding(8f);
    cell.setBorderColor(BORDER_GRAY);
    cell.setMinimumHeight(25f); // Ensure consistent height
    return cell;
}

    private void addDataTable(Document document, Map<String, String> data) throws DocumentException {
    PdfPTable table = new PdfPTable(2);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{1, 2});
    table.setSpacingBefore(10f);
    table.setKeepTogether(true);



    // Add alternating row colors for better readability
    BaseColor altRowColor = new BaseColor(252, 252, 252);
    int rowCount = 0;

    for (Map.Entry<String, String> entry : data.entrySet()) {
        PdfPCell labelCell = createLabelCell(entry.getKey());
        PdfPCell valueCell = createValueCell(entry.getValue());

        // Add subtle alternating row background
        if (rowCount % 2 == 1) {
            labelCell.setBackgroundColor(altRowColor);
            valueCell.setBackgroundColor(altRowColor);
        }

        table.addCell(labelCell);
        table.addCell(valueCell);
        rowCount++;
    }

    document.add(table);
}



    private void addPersonalDetails(Document document, MSSFCustomerData data) throws DocumentException {
        addSectionHeader(document, "Personal Information");
        Map<String, String> details = new LinkedHashMap<>();
        details.put("Salutation", data.getPdSalutation());
        details.put("First Name", data.getPdFirstName());
        details.put("Middle Name", data.getPdMiddleName());
        details.put("Last Name", data.getPdLastName());
        details.put("Gender", data.getPdGender());
        details.put("Date of Birth", data.getPdDob());
        details.put("Employment Type", data.getPdEmploymentType());
        details.put("Educational Qualification", data.getPdEducationalQualification());
        details.put("Resident Type", data.getPdResidentType());
        details.put("Resident Since", data.getPdResidentSince());
        details.put("PAN", data.getPdPan());
        details.put("Mobile", String.valueOf(data.getPdMobile() != null ? data.getPdMobile() : " "));
        details.put("Email", data.getPdEmail());
        details.put("Marital Status", data.getPdMaritalStatus());
        details.put("Mother's Name", data.getPdMotherName());
        details.put("Father's Name", data.getPdFatherName());
        details.put("Number of Dependents", String.valueOf(data.getPdNumDependent() != null ? data.getPdNumDependent() : " "));
        addDataTable(document, details);
    }

    private void addEmploymentBusinessDetails(Document document, MSSFCustomerData data) throws DocumentException {
        addSectionHeader(document, "Employment/Business Information");
        Map<String, String> details = new LinkedHashMap<>();
        details.put("Employer", data.getPdEmployer());
        details.put("Gross Annual Salary", formatAmount(data.getPdGrossSalAnnum()));
        details.put("Net Annual Salary", formatAmount(data.getPdNetSalAnnum()));
        details.put("Work Experience (Months)", String.valueOf(data.getPdWordExpMnth() != null ? data.getPdWordExpMnth() : " "));
        details.put("Average Monthly Income", formatAmount(data.getPdAvgMonInc()));
        details.put("Business Name", data.getPdBusinessName());
        details.put("Product Work Experience", formatYearsMonths(data.getPdProdWorkExpYr(), data.getPdProdWorkExpMnth()));
        details.put("Business Tenure", formatYearsMonths(data.getPdTenureBusinessYr(), data.getPdTenureBusinessMnth()));
        addDataTable(document, details);
    }

    private void addAddressSection(Document document, MSSFCustomerData data) throws DocumentException {
        addSectionHeader(document, "Address Information");

        // Present Address
        Map<String, String> presentAddress = new LinkedHashMap<>();
        presentAddress.put("Present Address Line 1", data.getPaAdd1());
        presentAddress.put("Present Address Line 2", data.getPaAdd2());
        presentAddress.put("Landmark", data.getPaLandmark());
        presentAddress.put("City", String.valueOf(data.getPaCity() != null ? data.getPaCity() : " "));
        presentAddress.put("PIN", String.valueOf(data.getPaPin() != null ? data.getPaPin() : " "));
        presentAddress.put("State", String.valueOf(data.getPaState() != null ? data.getPaState() : " "));
        presentAddress.put("Residing Since", String.valueOf(data.getPaResidingSince() != null ? data.getPaResidingSince() : " "));
        addDataTable(document, presentAddress);

        // Permanent Address
        Map<String, String> permanentAddress = new LinkedHashMap<>();
        permanentAddress.put("Permanent Address Line 1", data.getPerAdd1());
        permanentAddress.put("Permanent Address Line 2", data.getPerAdd2());
        permanentAddress.put("Landmark", data.getPerLandmark());
        permanentAddress.put("City", String.valueOf(data.getPerCity() != null ? data.getPerCity() : " "));
        permanentAddress.put("PIN", String.valueOf(data.getPerPin() != null ? data.getPerPin() : " "));
        permanentAddress.put("State", String.valueOf(data.getPerState() != null ? data.getPerState() : " "));
        permanentAddress.put("Residing Since", String.valueOf(data.getPerResidingSince() != null ? data.getPerResidingSince() : " "));
        addDataTable(document, permanentAddress);
    }

    private void addReferenceDetails(Document document, MSSFCustomerData data) throws DocumentException {
        addSectionHeader(document, "Reference Information");

        // Reference 1
        Map<String, String> ref1Details = new LinkedHashMap<>();
        ref1Details.put("Reference 1 Salutation", String.valueOf(data.getRf1Salutation() != null ? data.getRf1Salutation() : " "));
        ref1Details.put("First Name", data.getRf1FirstName());
        ref1Details.put("Last Name", data.getRf1LastName());
        ref1Details.put("Mobile", String.valueOf(data.getRf1Mobile() != null ? data.getRf1Mobile() : " "));
        ref1Details.put("Relation", data.getRf1Relation());
        addDataTable(document, ref1Details);

        // Reference 2
        Map<String, String> ref2Details = new LinkedHashMap<>();
        ref2Details.put("Reference 2 Salutation", String.valueOf(data.getRf2Salutation() != null ? data.getRf2Salutation() : " "));
        ref2Details.put("First Name", data.getRf2FirstName());
        ref2Details.put("Last Name", data.getRf2LastName());
        ref2Details.put("Mobile", String.valueOf(data.getRf2Mobile() != null ? data.getRf2Mobile() : " "));
        ref2Details.put("Relation", data.getRf2Relation());
        addDataTable(document, ref2Details);
    }

    private void addLoanDetails(Document document, MSSFCustomerData data) throws DocumentException {
        addSectionHeader(document, "Loan Information");
        Map<String, String> details = new LinkedHashMap<>();
        details.put("Loan Amount", formatAmount(data.getLaLoanAmt()));
        details.put("Rate of Interest (%)", formatPercentage(data.getLaRoi()));
        details.put("Tenure (months)", String.valueOf(data.getLaTenure()));
        details.put("Processing Fee", formatAmount(data.getLaFeeCharge()));
        details.put("Down Payment", formatAmount(data.getLaDownpayment()));
        details.put("Estimated EMI", formatAmount(data.getLaEstEmi()));
        addDataTable(document, details);
    }

    private void addDealerDetails(Document document, MSSFCustomerData data) throws DocumentException {
        addSectionHeader(document, "Dealer Information");
        Map<String, String> details = new LinkedHashMap<>();
        details.put("Dealer Code", data.getDlrCode());
        details.put("City", data.getDlrCity());
        details.put("State", data.getDlrState());
        details.put("PIN", String.valueOf(data.getDlrPin() != null ? data.getDlrPin() : " "));
        addDataTable(document, details);
    }

    private void addAuditDetails(Document document, MSSFCustomerData data) throws DocumentException {
        addSectionHeader(document, "Audit Information");
        Map<String, String> details = new LinkedHashMap<>();
        details.put("Created By", data.getCreatedBy());
        details.put("Created Date", formatDateTime(data.getCreatedDate()));
//        details.put("Modified By", data.getModifiedBy());
//        details.put("Modified Date", formatDateTime(data.getModifiedDate()));
//        details.put("Status", getStatusDescription(data.getStatus()));
        addDataTable(document, details);
    }

    // Helper methods for formatting and data conversion
    private String formatAmount(Double amount) {
    if (amount == null || amount == 0) return NOT_AVAILABLE;
    return String.format("₹ %,.2f", amount);
}

private String formatPercentage(Double value) {
    if (value == null || value == 0) return NOT_AVAILABLE;
    return String.format("%.2f%%", value);
}

private String formatDateTime(LocalDateTime dateTime) {
    if (dateTime == null) return NOT_AVAILABLE;
    return dateTime.format(DATE_FORMATTER);
}

private String formatYearsMonths(Integer years, Integer months) {
    if ((years == null || years == 0) && (months == null || months == 0)) {
        return NOT_AVAILABLE;
    }

    StringBuilder result = new StringBuilder();
    if (years != null && years > 0) {
        result.append(years).append(years == 1 ? " year" : " years");
    }
    if (months != null && months > 0) {
        if (result.length() > 0) result.append(" ");
        result.append(months).append(months == 1 ? " month" : " months");
    }
    return result.toString();
}

private String formatContactNumber(String number) {
    if (number == null || number.trim().isEmpty()) return NOT_AVAILABLE;
    return number;
}


    private String getEmploymentType(Integer type) {
        if (type == null) return "";
        try {
            return EmploymentType.values()[type - 1].name();
        } catch (ArrayIndexOutOfBoundsException e) {
            return "Unknown";
        }
    }

    private String getResidentType(Integer type) {
        if (type == null) return "";
        try {
            return ResidentType.values()[type - 1].name();
        } catch (ArrayIndexOutOfBoundsException e) {
            return "Unknown";
        }
    }

    private String getMaritalStatus(Integer status) {
        if (status == null) return "";
        try {
            return MaritalStatus.values()[status - 1].name();
        } catch (ArrayIndexOutOfBoundsException e) {
            return "Unknown";
        }
    }

    private String getStatusDescription(String status) {
        if (status == null) return "";
        return status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
    }
}
