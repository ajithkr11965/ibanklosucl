package com.sib.ibanklosucl.utilies;

import com.sib.ibanklosucl.dto.TypeCount;
import com.sib.ibanklosucl.model.VehicleLoanWarnMaster;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class CommonUtils {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private static final int LENGTH = 15;

    public Date getDate(String str) {
        try {
            if(str==null || str.isBlank())
                return null;
            return dateFormat.parse(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss");
        return "_"+sdf.format(new Date());
    }


    public static Date getDateFr(String str,String format) {
        try {
            if(str==null || str.isBlank())
                return null;
            SimpleDateFormat dateFormat1 = new SimpleDateFormat(format);
            return dateFormat1.parse(str);
        } catch (Exception e) {
            return null;
        }
    }
    public Date getDateIns(String str) {
        try {
            if(str==null || str.isBlank())
                return null;
            Instant instant = Instant.parse(str);
            return Date.from(instant);
        } catch (Exception e) {
            return null;
        }


    }

    public static TypeCount parseString(String input) {
        String[] parts = input.split("-");

        String type = Arrays.stream(parts)
                .findFirst()
                .orElse("");

        int count = Arrays.stream(parts)
                .skip(1) // Skip the first element (the type)
                .limit(1) // Limit to the next element (the count)
                .mapToInt(Integer::parseInt) // Convert to int
                .findFirst()
                .orElse(0);

        return new TypeCount(type, count);
    }

    public static  String expandReq(TypeCount tc){
        if(tc.getType()==null){
            return "NA";
        }
        else if(tc.getType().startsWith("A")){
            return "APPLICANT";
        }
        else if(tc.getType().startsWith("G")){
            return "GUARANTOR";
        }
        else if(tc.getType().startsWith("C")){
            return "CO_APPLICANT_"+tc.getCount();
        }
        else return "NA";
    }

    public static BigDecimal min(BigDecimal a, BigDecimal b) {
        if (a == null && b == null) {
            return null; // or throw an exception, depending on your requirement
        } else if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            return a.min(b);
        }
    }
    public static Date DateFormat(String dateStr) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            Date date = formatter.parse(dateStr);
         return date;
        } catch (Exception e) {
           throw e;
        }
    }


    public static String DateFormat(String inputDate,String inf,String of) {
        try {
             DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern(inf);
             DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern(of);
            LocalDate date = LocalDate.parse(inputDate, INPUT_FORMATTER);
            return date.format(OUTPUT_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getYob(String datetime){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(datetime);
            SimpleDateFormat df = new SimpleDateFormat("yyyy");
            return df.format(date);
        }
        catch (Exception e){
            return null;
        }

    }
    public static String getDob(String datetime){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(datetime);
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            return df.format(date);
        }
        catch (Exception e){
            return null;
        }

    }

    public static String ConvertDate(Date str,String formattype){
        SimpleDateFormat formatter = new SimpleDateFormat(formattype);
        String format = formatter.format(str);
        return format;
    }
    public static java.sql.Date DateConvert(String dateStr, String inf) {
        try {
            if(dateStr==null || dateStr.isBlank())return null;
            SimpleDateFormat dateFormat = new SimpleDateFormat(inf);
            Date df=dateFormat.parse(dateStr);
            return new java.sql.Date(df.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String createMessageString( List<String> messageList) {

        StringBuilder stringBuilder = new StringBuilder();
        // Append initial messages with the specified format
        for (int i = 0; i < messageList.size(); i++) {
            stringBuilder.append(i + 1).append(".").append(messageList.get(i));
            if (i < messageList.size() - 1) {
                stringBuilder.append(", ");
            } else {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
    public static String getIP(String ipAddress)
    {
        if (ipAddress != null && ipAddress.contains(":")) {
            ipAddress = ipAddress.split(":")[0]; // removing the port if the header contains
        } else if (ipAddress==null) {
            ipAddress="";
        }
        return ipAddress;
    }

    public static int calculateAge(String dobString) {
        // Define the date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Parse the date string to LocalDate
        LocalDate birthDate = LocalDate.parse(dobString, formatter);

        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Calculate the age
        return Period.between(birthDate, currentDate).getYears();
    }

    public  Map<String, List<String>> assignWarn(Map<String, List<String>> messages, VehicleLoanWarnMaster mas){
        List<String> temp=null;
        temp=messages.get(mas.getSeverity());
        if (temp == null) {
            temp = new ArrayList<>();
        }
        temp.add(mas.getWarnDesc());
        messages.put(mas.getSeverity(),temp);
        return messages;
    }


    public static String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress != null && ipAddress.contains(":")) {
            ipAddress = ipAddress.split(":")[0]; // removing the port if the header contains
        } else if (ipAddress==null) {
            ipAddress="";
        }
        return ipAddress;
    }

    public boolean isEmpty(String str){
        if(str==null)
            return true;
        else if (str.isBlank()) {
            return true;
        }
        else
            return false;
    }
    public  Long getAge(Date dob) {
        // Get the current date
        Calendar today = Calendar.getInstance();

        // Get the date of birth
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dob);

        // Calculate the age
        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // Adjust if the birthday hasn't occurred yet this year
        if (today.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH) ||
                (today.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH) &&
                        today.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return (long) age;
    }


    public static  String uidgenerate() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        // Append current time in milliseconds in a shortened form
        long timeComponent = System.currentTimeMillis() % 1000000; // Limiting to 6 digits
        sb.append(Long.toString(timeComponent, 36)); // Convert to base-36 to compress

        // Fill the rest with random characters to ensure uniqueness
        while (sb.length() < LENGTH) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return sb.toString();
    }
        public static boolean isToday(String dateString){
            //String dateString = "31-08-2024"; // Your date string in dd-MM-yyyy format

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");//("dd-MM-yyyy");

            try {
                // Parse the string to a LocalDate
                LocalDate dateToCheck = LocalDate.parse(dateString, formatter);

                // Get today's date
                LocalDate today = LocalDate.now();

                // Compare the dates
                if (dateToCheck.equals(today)) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                throw e;
            }
        }

}
