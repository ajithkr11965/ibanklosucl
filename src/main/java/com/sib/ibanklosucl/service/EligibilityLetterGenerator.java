package com.sib.ibanklosucl.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.sib.ibanklosucl.dto.SanctionChargeDTO;
import com.sib.ibanklosucl.model.InPrincData;
import com.sib.ibanklosucl.model.VehicleLoanVehicle;
import com.sib.ibanklosucl.repository.*;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Service
public class EligibilityLetterGenerator {

    @Autowired
    private InPrincDataRepository inPrincDataRepository;
    @Autowired
    private CustomerDetailsRepository customerDetailsRepository;
    @Autowired
    private VehicleLoanMasterRepository vlmasRepository;

    @Autowired
    private FetchRepository fetchRepository;

    @Autowired
    private InPrincDataRepositoryFetch chargefetch;
    @Autowired
    private VehicleLoanProgramService vlprogramservice;

    @Autowired
    private VehicleLoanVehicleRepository vlvehicle;

    @Autowired
    private iBankService ibank;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private String wiNum;
    private Long slno;
    @Value("${path.curvelogo}")
    private String logoPath;

    public InPrincData getInPrincData(String wiNum, Long slno) {
        InPrincData inPrincData = inPrincDataRepository.findByWiNumAndSlno(wiNum, slno).get(0);
        if (inPrincData != null) {
            List<Map<String, String>> NameList = findNames(wiNum, slno);
            inPrincData.setNames(NameList);
        }
        return inPrincData;
    }
    public List<Map<String, String>> findNames(String wiNum, Long slno) {
        String sql = "SELECT appl_name,decode (applicant_type, 'A', 'Applicant', 'C','Co Applicant','G','Gurantor') applicant_type FROM vehicle_loan_applicants where wi_num=? and slno= ? and del_flg='N'";
        return jdbcTemplate.query(sql, new Object[]{wiNum,slno}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("applicant_name", rs.getString("appl_name"));
            map.put("applicant_type", rs.getString("applicant_type"));
            return map;
        });
    }

    private static final Font regularFont = new Font(Font.FontFamily.HELVETICA, 10);
    private static final Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static final Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font subHeaderFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
    private static final Font footerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC);
    private String base64;

    private Document document;

    private InPrincData data;

    public EligibilityLetterGenerator() throws DocumentException, IOException {

    }

    @SneakyThrows
    public String generatePDF(String wiNum,Long slno) {
        this.wiNum = wiNum;
        this.slno = slno;

        this.data = getInPrincData(wiNum,slno);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document = new Document(PageSize.A4, 36, 36, 36, 60);
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
        writer.setPageEvent(event);
        document.open();

        addFirstPageContent();
        addSecondPageContent();
        document.close();

        //  byte[] pdfBytes = baos.toByteArray();
        byte[] pdfBytes = addPageNumbers(baos.toByteArray());
        this.base64 = Base64.getEncoder().encodeToString(pdfBytes);
        vlmasRepository.updateDsasacdocBywiNumAndSlno(wiNum,slno,this.base64);
        try (FileOutputStream fileos = new FileOutputStream("eligibility_letter.pdf")) {
            fileos.write(pdfBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this.base64;
    }

    private void addFirstPageContent() throws DocumentException {
        addTitle();
        addSpacing(5f);
        addFirstPageBody();
    }

    private void addSecondPageContent() throws DocumentException {
        addSecondPageBody();
    }

    private void addTitle() throws DocumentException {
        Paragraph title = new Paragraph("THE SOUTH INDIAN BANK LIMITED", headerFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph subTitle = new Paragraph("REGD. OFFICE: THRISSUR", subHeaderFont);
        subTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subTitle);

        addSpacing(20f);

        Paragraph mainTitle = new Paragraph("Indicative Eligibility Letter", headerFont);
        mainTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(mainTitle);
    }

    private void addFirstPageBody() throws DocumentException {
        Paragraph dateParagraph = new Paragraph("Date:"+data.getDateOfForm(), regularFont);
        dateParagraph.setAlignment(Element.ALIGN_RIGHT);
        document.add(dateParagraph);

        Paragraph dearSirMadam = new Paragraph("Dear Sir/ Madam\n", regularFont);
        document.add(dearSirMadam);

        addSpacing(10f);
        addCustomerDetailsTable0();
        addSpacing(10f);
        addCustomerDetailsTable();
        addSpacing(10f);
        addLoanDetailsTable();
        addSpacing(10f);
    }

    private void addCustomerDetailsTable0() throws DocumentException {
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setWidths(new float[]{1, 2});

        addTableCell(detailsTable, "Subject", true);
        addTableCell(detailsTable, "Vehicle Loan", false);
        addTableCell(detailsTable, "Application No", true);
        addTableCell(detailsTable, data.getApplicationNumber(), false);
        addTableCell(detailsTable, "Branch", true);
        addTableCell(detailsTable, fetchRepository.getSolName(data.getSolid()), false);

        document.add(detailsTable);
    }

    private void addCustomerDetailsTable() throws DocumentException {
        PdfPTable detailsTable1 = new PdfPTable(2);
        detailsTable1.setWidthPercentage(100);
        detailsTable1.setWidths(new float[]{1, 2});
        for(Map<String, String> customerDetails : data.getNames()){
            addTableCell(detailsTable1, "Name of the customer", true);
            addTableCell(detailsTable1, customerDetails.get("applicant_name"), false);
            addTableCell(detailsTable1, "Applicant Type", true);
            addTableCell(detailsTable1, customerDetails.get("applicant_type") , false);
        }

        document.add(detailsTable1);
//
//        addSpacing(30f);

        PdfPTable detailsTable2 = new PdfPTable(2);
        detailsTable2.setWidthPercentage(100);
        detailsTable2.setWidths(new float[]{1, 2});

//        PdfPCell overseasAddressHeaderCell = new PdfPCell(new Phrase("Overseas Address", boldFont));
//        overseasAddressHeaderCell.setColspan(2);
//        overseasAddressHeaderCell.setPadding(5f);
//        overseasAddressHeaderCell.setBorder(Rectangle.BOX);
//        detailsTable2.addCell(overseasAddressHeaderCell);
//        detailsTable2.addCell("");
//        detailsTable2.addCell("");
//
//        addTableCell(detailsTable2, "Name", true);
//        addTableCell(detailsTable2, "Address", true);
//        addTableCell(detailsTable2, "A", false);
//        addTableCell(detailsTable2, "Sample Overseas Address of B", false);

        document.add(detailsTable2);
    }

    private void addLoanDetailsTable() throws DocumentException {
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setWidths(new float[]{1, 2});

        addTableCell(detailsTable, "Facility", true);
        addTableCell(detailsTable, "Vehicle Loan", false);
        addTableCell(detailsTable, "Loan Amount \n(Incl Insurance)", true);
        addTableCell(detailsTable, data.getLoanAmount(), false);
        addTableCell(detailsTable, "Insurance Funding Amount", true);
        addTableCell(detailsTable, data.getInsurance(), false);
        addTableCell(detailsTable, "ROI Applicable", true);
        PdfPCell roiCell = new PdfPCell(new Phrase(ibank.getMisPRM("INPRINROI").getPVALUE()+" % Fixed ", regularFont));
        //roiCell.setColspan(3);
        roiCell.setPadding(5f);
        //roiCell.setBorder(Rectangle.BOX);
        detailsTable.addCell(roiCell);

        addTableCell(detailsTable, "Tenor in months", true);
        addTableCell(detailsTable, data.getTenor()+" months", false);
        addTableCell(detailsTable, "Repayment EMI", true);
        addTableCell(detailsTable, data.getEMI(), false);

        document.add(detailsTable);
        addSpacing(10f);
        Paragraph chargesInfo = new Paragraph("Charges applicable:Concession in Processing fee may be taken up with ALBG Team", regularFont);
        document.add(chargesInfo);
        addSpacing(10f);

        PdfPTable chargesTable = new PdfPTable(4);
        chargesTable.setWidthPercentage(100);
        chargesTable.setWidths(new float[]{2, 2, 2, 2});
        addTableCell(chargesTable, "Item", true);
        addTableCell(chargesTable, "Actual amount", true);
        addTableCell(chargesTable, "Consession allowed", true);
        addTableCell(chargesTable, "Chargeable amount", true);
//        addTableCell(chargesTable, "Admission charge", false);
//        addTableCell(chargesTable, "", false);
//        addTableCell(chargesTable, "", false);
//        addTableCell(chargesTable, "", false);
       // String program = vlprogramservice.getProgramName(wiNum,slno);
        String program = "INCOME";
        SanctionChargeDTO chargedto = chargefetch.getSanctionCharge(program,"CHG1000");
        double fee=0;
        if(chargedto.getStaticFixed().equals("Y")){
            fee=chargedto.getValue();
        }else{
            double value=chargedto.getValue();
            double amount=0;
            if(chargedto.getLoanAmountPercentage().equals("Y")){
                amount= Double.parseDouble(data.getLoanAmount());
            }else{
                VehicleLoanVehicle vl = vlvehicle.findExistingByWiNumAndSlno(wiNum,slno);
                amount= Double.parseDouble(vl.getExshowroomPrice());
            }
            fee= (value/100)*amount;
            if(chargedto.getMaximumLimit().equals("Y")){
                if(chargedto.getMaximumValue()<fee){
                    fee=chargedto.getMaximumValue();
                }
            }

        }

        addTableCell(chargesTable, "Processing fee", false);
        //addTableCell(chargesTable, data.getActualAmount(), false);
        addTableCell(chargesTable, String.valueOf(fee), false);
        addTableCell(chargesTable, data.getConcessionAllowed(), false);


        //addTableCell(chargesTable, data.getChargebleAmount(), false);
        addTableCell(chargesTable, String.valueOf(fee), false);
        document.add(chargesTable);

        addSpacing(30f);
    }

    private void addSecondPageBody() throws DocumentException {
        addSpacing(10f);

        Paragraph additionalInfoBlank = new Paragraph("\n\n\n\n\n\n\n\n\n");
        document.add(additionalInfoBlank);

        Paragraph additionalInfo = new Paragraph();
        additionalInfo.add(new Phrase("All taxes, duties and levies, including but not limited to GST and any other tax/levy applicable as per law and as may be amended from time to time would be additionally charged.\n\n", regularFont));
        additionalInfo.add(new Phrase("We would like to inform you that you are eligible for a Loan of Rs. " + data.getLoanAmount() + " subject to the following conditions:\n\n", regularFont));

// Create a list for bullet points
        com.itextpdf.text.List bulletPoints = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
        bulletPoints.setListSymbol("\u2022 ");  // Unicode for bullet point

// First bullet point
        ListItem item1 = new ListItem("Satisfactory clearance/ verification of documents submitted as being acceptable to the Bank.", regularFont);
        item1.setIndentationLeft(15f);  // Indent bullet points
        bulletPoints.add(item1);

// Second bullet point
        ListItem item2 = new ListItem("In the event of deterioration “or if it is noted that the documents submitted/ covenants/ representations made by you is untrue/ not correct of Borrower's and/or any security provider's credit worthiness”, Bank at its sole discretion will withdraw the above Indicative eligibility letter.", regularFont);
        item2.setIndentationLeft(15f);  // Indent bullet points
        bulletPoints.add(item2);

// Third bullet point
        ListItem item3 = new ListItem("This initial in-principle eligibility letter shall not be construed as a binding obligation on the part of the SIB to process any proposal that may be submitted later and shall not in any way apply to, or supersede/ prevail over the processing /non processing of any proposal or its sanction/ rejection thereof and the initial in-principle eligibility letter shall be deemed to have been automatically lapsed at the end of its validity period. Further, Bank may also be guided by then applicable Regulatory guidelines, Policies and its feasibility consideration. Any loan, if and when sanctioned as per then applicable policy, shall be governed by its Terms and Conditions and in the Sanction Intimation Letter/s or Credit Arrangement Letter/s and relevant documents and those shall be final and binding.", regularFont);
        item3.setIndentationLeft(15f);  // Indent bullet points
        bulletPoints.add(item3);

// Fourth bullet point
        ListItem item4 = new ListItem("All other terms and conditions as per Bank’s policy stands applicable.", regularFont);
        item4.setIndentationLeft(15f);  // Indent bullet points
        bulletPoints.add(item4);

// Fifth bullet point
        ListItem item5 = new ListItem("Processing charges, as applicable will be collected upfront.", regularFont);
        item5.setIndentationLeft(15f);  // Indent bullet points
        bulletPoints.add(item5);

// Sixth bullet point
//        ListItem item6 = new ListItem("This letter is valid for 30 days.", regularFont);
//        item6.setIndentationLeft(15f);  // Indent bullet points
//        bulletPoints.add(item6);

// Add the list to the paragraph
        additionalInfo.add(bulletPoints);

// Add paragraph to the document

        document.add(additionalInfo);


        addSpacing(40f);
    }

    private void addTableCell(PdfPTable table, String text, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(text, isHeader ? boldFont : regularFont));
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(5f);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
    }

    private void addSpacing(float spacing) throws DocumentException {
        Paragraph spacingParagraph = new Paragraph("  ");
        spacingParagraph.setSpacingAfter(spacing);
        document.add(spacingParagraph);
    }

    private class HeaderFooterPageEvent extends PdfPageEventHelper {
        private Image logo;

        public HeaderFooterPageEvent() throws IOException, BadElementException {
            String imagePath = logoPath;
            logo = Image.getInstance(imagePath);
            logo.scaleToFit(200, 200);
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                // Add logo to the top right corner
                logo.setAbsolutePosition(document.right() - logo.getScaledWidth()+35, document.top()-55);
                writer.getDirectContent().addImage(logo);

                // Add red line separator
                PdfContentByte cb = writer.getDirectContent();
                cb.setColorStroke(BaseColor.RED);
                cb.moveTo(document.left(), document.bottom()+35);
                cb.lineTo(document.right(), document.bottom()+35);
                cb.stroke();

                // Add footer text
                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("The South Indian Bank Ltd., Regd. Office: Thrissur, Kerala", footerFont),
                        (document.right() + document.left()) / 2, document.bottom() +20, 0);
                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Head Office: S.I.B. House, T.B. Road, P.B. No: 28, Thrissur - 680001, Kerala", footerFont),
                        (document.right() + document.left()) / 2, document.bottom() +10, 0);
                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("(Tel) 0487-2420 020, (Fax) 91 487-244 2021, e-mail: sibcorporate@sib.bank.in", footerFont),
                        (document.right() + document.left()) / 2, document.bottom() , 0);
                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("CIN: L65191KL 1929PLC001017, Toll Free (India) 1800-102-9408, 1800-425-1809 (BSNL)", footerFont),
                        (document.right() + document.left()) / 2, document.bottom()-10, 0);
                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("www.southindianbank.com", footerFont),
                        (document.right() + document.left()) / 2, document.bottom()-20, 0);


            } catch (DocumentException e) {
                throw new ExceptionConverter(e);
            }
        }
    }

    byte[] addPageNumbers(byte[] pdfBytes) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(pdfBytes);
        int n = reader.getNumberOfPages();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, baos);
        for (int i = 1; i <= n; i++) {
            PdfContentByte cb = stamper.getOverContent(i);
            cb.beginText();
            BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            cb.setFontAndSize(bf, 8);
            cb.setTextMatrix((reader.getPageSize(i).getWidth()) - 55, 30);
            cb.showText("Page " + i + " of " + n);
            cb.endText();
        }
        stamper.close();
        reader.close();

        return baos.toByteArray();
    }
}
