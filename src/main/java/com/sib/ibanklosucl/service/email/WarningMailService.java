package com.sib.ibanklosucl.service.email;

import com.sib.ibanklosucl.model.VehicleLoanWarn;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WarningMailService {

        private static final String TEMPLATE = """
        <div align="center">
            <table style="width: 100%%; background-color: gray;" width="100%%" cellpadding="0" border="0">
                <tr>
                    <td style="padding: 30pt 0.75pt;">
                        <div align="center">
                            <table style="width: 563pt; background-color: white;" width="600" cellspacing="0" cellpadding="0" border="0">
                                <tr>
                                    <td style="background-color: #B20000; padding: 15pt 11.25pt 15pt 48.75pt;">
                                        <h1 style="margin: 0; font-size: 18pt; font-family: Arial, sans-serif; color: white; font-weight: normal;">
                                            SIB - PowerDrive
                                        </h1>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="background-color: #EEF2F3; padding: 0 0 90pt;">
                                        <div align="center">
                                            <table style="width: 450pt; background-color: #EEF2F3; max-width: 600px;" width="600" cellspacing="0" cellpadding="0" border="0">
                                                <tr>
                                                    <td style="padding: 18.75pt 8.75pt 7.5pt;">
                                                        <h2 style="margin: 0 0 15pt; font-size: 22.5pt; font-family: Arial, sans-serif; color: #c52f33; border-bottom: solid #e0e1e3 1.5pt; padding-bottom: 15pt;">
                                                           Mismatch Detected!!
                                                        </h2>
                                                        <p style="font-family: Arial, sans-serif; color: #444444;">
                                                            Dear <b>Team</b>,
                                                        </p>
                                                        <p style="font-family: Arial, sans-serif; color: #444444;">
                                                            The following mismatches have been detected between the entries made by the branch and the existing records in Finacle for Work Item <b>#%s</b>:
                                                        </p>
                                                        %s
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 3.75pt 48.75pt 7.5pt;">
                                                        <p style="font-family: Arial, sans-serif; color: #660000; margin-top: 15pt;">
                                                            Note: This is an automated mail
                                                        </p>
                                                    </td>
                                                </tr>
                                            </table>
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="background-color: #AF0000; padding: 18.75pt 0;">
                                        <p style="margin: 0; font-size: 11pt; font-family: Arial, sans-serif; color: white; text-align: center;">
                                            <b>Application Team</b>
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    """;

private static final String CUSTOMER_MAIL_TEMPLATE= """
        <!DOCTYPE html>
        <html>
        <head>
                <meta http-equiv='Content-Type' content='text/html; charset=utf-8' />
        </head>
        <body>
             <table  width='700' align='center' summary=' Change' style="border: 1px solid ;border-color: #cb1619;" border-radius:0;cellpadding='0'>     <tbody>
                        <tr>
                                <td  scope='col' colspan='2' >
                                <img src='cid:siblogo.png' width='325' height='168.4' align="right">
                                </td> 
                        </tr>
                        <tr style='text-align:justify;font-size:15px' >
                                <td  scope='col' colspan="2">
                                        <div style="padding:10px" >
                                        <b>Dear  %s ,<br><br></b>
                
                                        <b>Greetings from South Indian Bank.<br><br></b>
                                        %s <br><br><br>
               
                                        Regards, <br><br>
                                        South Indian Bank<br><br>
                                        </div>
                                </td>
                        </tr> 
                        <tr scope='col' style='text-align:centre;font-size:10px;color:#ffffff;background:#cb1619;height:80px; border-radius: 0px;cellpadding: 2px'>
                                <td width='70%%' style='text-align:left;style=color:#ffffff'>
                                CUSTOMER CARE CENTRE (24/7)<br>
                                1800 425 1809 (Toll Free India), 1800 102 9408 (Toll Free India),<br> 91-484-2388555 (For NREs)<br>
                                <a href="mailto:customercare@sib.bank.in" style='color:#ffffff'>customercare@sib.bank.in</a>&nbsp; &nbsp;
                                <a href="https://www.southindianbank.com" target="_blank" style='color:#ffffff'>www.southindianbank.com</a>
                                </td>
                                
                
                        </tr>
                        </tbody>
                </table>
        </body>
        </html>""";


//    private static final  String CUSTOMER_MAIL_TEMPLATE= """
//            <html><head>
//                    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
//            </head>
//            <body>
//                 <table width="700" align="center" summary=" Change" style="font-family: arial,'helvetica neue',helvetica,sans-serif;border: 1px solid #cccccc;border-collapse: collapse;border-spacing: 0px;" border-radius:0;cellpadding="0">
//                 <tbody>
//                            <tr>
//                                    <td scope="col" colspan="2">
//                                    <img src="cid:siblogo.png" width="325" height="168.4" align="right">
//                                    </td>
//                            </tr>
//                            <tr style="text-align:justify;font-size:15px">
//                                    <td scope="col" colspan="2">
//                                            <div style="padding:13px">
//                                            <b>Dear %s,<br><br></b>
//
//                                            <b>Greetings from South Indian Bank.<br><br></b>
//                                            %s <br><br><br>
//                                            Regards, <br><br>
//                                            South Indian Bank<br><br>
//                                            </div>
//                                    </td>
//                            </tr>
//                            <tr scope="col" style="text-align:centre;font-size:10px;color:#ffffff;/*! background:#cb1619; */height:80px; border-radius: 0px;cellpadding: 2px;background-color: #c9070a;">
//                                    <td width="70%%" style="text-align:left;padding-top: 20px;padding-bottom: 20px;padding-left: 10px;padding-right: 40px;text-decoration: none;color: #ffffff;font-size: 11px;style-color: #ffffff;">
//                                    CUSTOMER CARE CENTRE (24/7)<br>
//                                    1800 425 1809 (Toll Free India), 1800 102 9408 (Toll Free India),<br> 91-484-2388555 (For NREs)<br>
//                                    <a href="mailto:customercare@sib.bank.in" style="color:#ffffff">customercare@sib.bank.in</a>&nbsp; &nbsp;\s
//                                    <a href="https://www.southindianbank.com" target="_blank" style="color:#ffffff">www.southindianbank.com</a>
//                                    </td>
//                                    <td width="30%%" style="text-align:left">
//                                            FOLLOW US &nbsp;&nbsp;<a href="https://www.facebook.com/thesouthindianbank" target="new"><img src="cid:fb.png"></a>  &nbsp;
//                                            <a href="https://www.instagram.com/southindianbank" target="_new"><img src="cid:insta.png"></a> &nbsp;
//                                            <a href="https://www.youtube.com/c/OfficialSIBLtd/videos" target="new"><img src="cid:youtube_icon.png"></a>  &nbsp;
//                                            <a href="https://twitter.com/OfficialSIBLtd" target="new"><img src="cid:twitter.png"></a>
//                                    </td>
//
//                            </tr>
//                            </tbody>
//                    </table>
//            </body></html>""";

    private static final String BRANCH_MAIL_TEMPLATE = """
        <!DOCTYPE html>
        <html>
        <head>
                <meta http-equiv='Content-Type' content='text/html; charset=utf-8' />
        </head>
        <body>
             <table width='700' align='center' summary='Branch Communication' style="border: 1px solid ;border-color: #cb1619;" border-radius:0;cellpadding='0'>     
                <tbody>
                        <tr>
                                <td scope='col' colspan='2'>
                                <img src='cid:siblogo.png' width='325' height='168.4' align="right">
                                </td> 
                        </tr>
                        <tr style='text-align:justify;font-size:15px'>
                                <td scope='col' colspan="2">
                                        <div style="padding:10px">
                                        <b>Dear %s,<br><br></b>
                
                                        <b>Greetings from South Indian Bank - ALBG Team.<br><br></b>
                                        %s <br><br><br>
               
                                        Best Regards, <br><br>
                                        <b>ALBG Team</b><br>
                                        South Indian Bank<br><br>
                                        </div>
                                </td>
                        </tr> 
                        <tr scope='col' style='text-align:centre;font-size:10px;color:#ffffff;background:#cb1619;height:80px; border-radius: 0px;cellpadding: 2px'>
                                <td width='70%%' style='text-align:left;style=color:#ffffff'>
                                CUSTOMER CARE CENTRE (24/7)<br>
                                1800 425 1809 (Toll Free India), 1800 102 9408 (Toll Free India),<br> 91-484-2388555 (For NREs)<br>
                                <a href="mailto:customercare@sib.bank.in" style='color:#ffffff'>customercare@sib.bank.in</a>&nbsp; &nbsp;
                                <a href="https://www.southindianbank.com" target="_blank" style='color:#ffffff'>www.southindianbank.com</a>
                                </td>
                                
                        </tr>
                </tbody>
        </table>
        </body>
        </html>""";

    // MSSF-specific professional template
    private static final String MSSF_BRANCH_TEMPLATE = """
        <!DOCTYPE html>
        <html>
        <head>
                <meta http-equiv='Content-Type' content='text/html; charset=utf-8' />
                <style>
                    .alert-header { background-color: #2e7d32; color: white; padding: 15px; }
                    .urgent-header { background-color: #d32f2f; color: white; padding: 15px; }
                    .pending-header { background-color: #f57600; color: white; padding: 15px; }
                    .content-body { padding: 20px; font-family: Arial, sans-serif; }
                    .data-table { width: 100%%; border-collapse: collapse; margin: 15px 0; }
                    .data-table th, .data-table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                    .data-table th { background-color: #f2f2f2; font-weight: bold; }
                    .highlight { background-color: #fff3e0; }
                    .urgent { background-color: #ffebee; }
                </style>
        </head>
        <body>
             <table width='700' align='center' style="border: 1px solid #cb1619; font-family: Arial, sans-serif;" cellpadding='0' cellspacing='0'>     
                <tbody>
                        <tr>
                                <td colspan='2'>
                                <img src='cid:siblogo.png' width='325' height='168.4' align="right" style="padding: 10px;">
                                </td> 
                        </tr>
                        <tr>
                                <td colspan="2" class="%s">
                                        <h2 style="margin: 0; font-size: 18px;">%s</h2>
                                </td>
                        </tr>
                        <tr>
                                <td colspan="2" class="content-body">
                                        <p><b>Dear Branch Team,</b></p>
                                        <p><b>Greetings from South Indian Bank - ALBG Operations.</b></p>
                                        %s
                                        <br><br>
                                        <p><b>Action Required:</b></p>
                                        <ul>
                                            %s
                                        </ul>
                                        <br>
                                        <p>For any queries, please contact the ALBG Team.</p>
                                        <br>
                                        <p>Best Regards,<br>
                                        <b>ALBG Team</b><br>
                                        South Indian Bank</p>
                                </td>
                        </tr> 
                        <tr style='background:#cb1619; color:#ffffff; font-size:10px; height:60px;'>
                                <td width='70%%' style='text-align:left; padding: 15px;'>
                                CUSTOMER CARE CENTRE (24/7)<br>
                                1800 425 1809 (Toll Free India)<br>
                                <a href="mailto:customercare@sib.bank.in" style='color:#ffffff'>customercare@sib.bank.in</a> | 
                                <a href="https://www.southindianbank.com" target="_blank" style='color:#ffffff'>www.southindianbank.com</a>
                                </td>
                                <td width='30%%' style='text-align:center; padding: 15px;'>
                                    <b>ALBG Operations</b><br>
                                    South Indian Bank
                                </td>
                        </tr>
                </tbody>
        </table>
        </body>
        </html>""";


    public String generateCustEmailBody(String body,String custName){
        custName=custName==null||custName.isBlank()?"Customer":custName;
        return String.format(CUSTOMER_MAIL_TEMPLATE,custName,body);
    }

        public  String generateEmailBody(List<VehicleLoanWarn> vw,String winum) throws Exception {
            Map<Long, List<VehicleLoanWarn>> groupedData = vw.stream()
                    .collect(Collectors.groupingBy(VehicleLoanWarn::getApplicantId));
            // Define severity order
            List<String> severityOrder = Arrays.asList("High", "Medium", "Low");
            // Comparator for VehicleLoanWarn by severity
            Comparator<VehicleLoanWarn> severityComparator = Comparator.comparingInt(vw1 -> severityOrder.indexOf(vw1.getSeverity()));
            // Sort the lists within the grouped data by severity
            groupedData.forEach((applicantId, list) -> list.sort(severityComparator));

            StringBuilder htmlBuilder = new StringBuilder();
            groupedData.forEach((appType, dataGroup) -> {
                htmlBuilder.append("<table style='width: 100%; border-collapse: collapse; margin-top: 15pt;margin-bottom: 15pt;'>");
                htmlBuilder.append("<tr><td align=\"center\"  style='border: 1px solid #ddd; padding: 8px;background-color: #e6e6e6;'  colspan='5'>").append(dataGroup.get(0).getApplicantType()).append("</td></tr>");
                //<td  style='border: 1px solid #ddd; padding: 8px; background-color: #e6e6e6;' colspan='2' align="center">TOM DC</td>
                htmlBuilder.append(" <tr>" +
                        "    <th style='border: 1px solid #ddd; padding: 8px; background-color: #f2f2f2;'>No</th>" +
                        "    <th style='border: 1px solid #ddd; padding: 8px; background-color: #f2f2f2;'>Desc.</th>" +
                     //   "    <th colspan='2' style='border: 1px solid #ddd; padding: 8px; background-color: #f2f2f2;'>Existing Data in</th>" +
                        "    <th style='border: 1px solid #ddd; padding: 8px; background-color: #f2f2f2;'>Finacle</th>" +
                        "    <th style='border: 1px solid #ddd; padding: 8px; background-color: #f2f2f2;'>Branch Maker</th>" +
                        "    <th style='border: 1px solid #ddd; padding: 8px; background-color: #f2f2f2;'>Type</th>" +
                        "  </tr>");
//                +
//                        "  <tr>" +
//                        "    <th style='border: 1px solid #ddd; padding: 8px; background-color: #f2f2f2;'>Finacle</th>" +
//                        "    <th style='border: 1px solid #ddd; padding: 8px; background-color: #f2f2f2;'>Branch</th>" +
//                        "  </tr>");
                int slno=1;
                for (VehicleLoanWarn data : dataGroup) {
                    String color=data.getSeverity().equals("High")?"color:#f44336;font-weight: bold;":("Medium".equals(data.getSeverity())?"color:#ff9800;font-weight: bold;":"");
                    htmlBuilder.append("<tr>")
                            .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(slno++).append("</td>")
                            .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(data.getWarnDesc()).append("</td>")
                            .append("<td  style='border: 1px solid #ddd; padding: 8px;color:#04351a;font-weight: bold;'>").append(data.getCbsValue()).append("</td>")
                            .append("<td style='border: 1px solid #ddd; padding: 8px;color: #194879;font-weight: bold;'>").append(data.getWiValue()).append("</td>")
                            .append("<td style='border: 1px solid #ddd; padding: 8px;").append(color).append("'>").append(data.getSeverity()).append("</td>")
                            .append("</tr>");
                }

                htmlBuilder.append("</table>");
            });
            return String.format(TEMPLATE, winum,htmlBuilder);
        }

    public String generateBranchEmailBody(String body, String recipientName) {
        // Better addressing for branches
        String addressee = determineBranchAddressee(recipientName);
        return String.format(BRANCH_MAIL_TEMPLATE, addressee, body);
    }
    private String determineBranchAddressee(String recipientName) {
        if (recipientName == null || recipientName.isBlank()) {
            return "Branch Team";
        }

        // Smart addressing based on email pattern
        String lowerName = recipientName.toLowerCase();
        if (lowerName.contains("manager")) {
            return "Branch Manager";
        } else if (lowerName.contains("br") && lowerName.contains("@")) {
            return "Branch Team";
        } else if (lowerName.contains("supervisor")) {
            return "Branch Supervisor";
        } else {
            return "Branch Team";
        }
    }

    /**
     * Generate professional MSSF email body for branch alerts
     * Use this specifically for MSSF email alerts
     */
    public String generateMSSFBranchEmailBody(String alertType, String title, String body, String actionItems) {
        String headerClass = switch (alertType.toUpperCase()) {
            case "NEW_LEAD" -> "alert-header";
            case "PENDING" -> "pending-header";
            case "URGENT" -> "urgent-header";
            default -> "alert-header";
        };

        return String.format(MSSF_BRANCH_TEMPLATE, headerClass, title, body, actionItems);
    }


}
