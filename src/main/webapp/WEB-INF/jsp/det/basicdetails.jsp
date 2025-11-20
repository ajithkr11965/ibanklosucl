<%@ page import="java.util.List" %>
<%@ page import="com.sib.ibanklosucl.model.*" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Calendar" %><%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 09-05-2024
  Time: 15:13
  To change this template use File | Settings | File Templates.
--%>
<%

    String apptype= request.getAttribute("apptype").toString();
    boolean completed=false,mod=false;
    VehicleLoanApplicant applicant;
    if(request.getAttribute("general")!=null)
        applicant = (VehicleLoanApplicant) request.getAttribute("general");
    else {
        applicant = null;
    }

    String basic_name ="",basic_dob="",basic_gender="",basic_ftname="",basic_mtname="",basic_spname="",basic_saltutation="",basic_crmlead="",basic_ms="",basic_mob="",basic_email="",basic_mobcode="91",basic_annualincome="";
    String permanentAddress1="",permanentAddress2="",permanentAddress3="",permanentCity="",permanentState="",permanentCountry="",permanentPin="",permanentDurationOfStay="",permanentResidenceType="",presentAddress1="",presentAddress2="",presentAddress3="",presentCity="",presentState="",presentCountry="",presentPin="",presentDurationOfStay="",presentdistanceFromBranch="",presentResidenceType="",preferred_flag="",current_residence_flag="",sameAsper="N",comm_proof_flg="N",permanentdistanceFromBranch="";

    String  presentCitydesc="",presentCountryDesc="",presentStatedesc="",permanentCitydesc="",permanentStatedesc="",permanentCountrydesc="";

    String permanentdisbaled="" ,basic_edu="",basic_pep="",editable="",commEditable="",basic_occupation="";
    String basic_cpoa="" ,basic_cpoadoc="",basic_cpoaext="";
    String basic_age="";
    if(applicant!=null)
    {
        permanentdisbaled= applicant.getSibCustomer().equals("Y")?"disabled":"";
        editable= applicant.getSibCustomer().equals("Y")?"noneditable":"";
        basic_name = applicant.getApplName() != null ?applicant.getApplName() : "";
        basic_dob = applicant.getApplDob() != null ? applicant.getApplDob().toString().substring(0,10) : "";
        basic_age = basic_dob!= null  && !basic_dob.isEmpty() ? getAge(applicant.getApplDob()).toString() : "";
        if(applicant.getBasicapplicants()!=null){
            if(applicant.getBasicComplete()!=null && applicant.getBasicComplete().equals("Y"))
                completed=true;
            VehicleLoanBasic vb=applicant.getBasicapplicants();
            basic_gender = vb.getGender() != null ?vb.getGender() : "";
            basic_ftname = vb.getFatherName() != null ? vb.getFatherName() : "";
            basic_mtname = vb.getMotherName() != null ? vb.getMotherName() : "";
            basic_spname = vb.getSpouseName() != null ? vb.getSpouseName() : "";
            basic_saltutation = vb.getSalutation() != null ?vb.getSalutation() : "";
            basic_occupation = vb.getOccupation() != null ?vb.getOccupation() : "";
          //  basic_crmlead = vb.getApplicantDob() != null ? vb.getApplicantDob().toString() : "";
            basic_ms = vb.getMaritalStatus() != null ? vb.getMaritalStatus() : "";
            basic_mob = vb.getMobileNo() != null ? vb.getMobileNo() : "";
            basic_email = vb.getEmailId() != null ? vb.getEmailId() : "";
            basic_mobcode = vb.getMobileCntryCode() != null ? vb.getMobileCntryCode() : "91";
            basic_annualincome= vb.getAnnualIncome() != null ? vb.getAnnualIncome() : "";
            basic_cpoa= vb.getCpoa() != null ? vb.getCpoa() : "";
            basic_cpoadoc= vb.getCpoaDoc() != null ? vb.getCpoaDoc() : "";
            basic_cpoaext= vb.getCpoaExt() != null ? vb.getCpoaExt() : "";
            permanentAddress1 = vb.getAddr1() != null ? vb.getAddr1() : "";
            permanentAddress2 = vb.getAddr2() != null ? vb.getAddr2() : "";
            permanentAddress3 = vb.getAddr3() != null ? vb.getAddr3() : "";
            permanentCity = vb.getCity() != null ? vb.getCity() : "";
            permanentCitydesc = vb.getCitydesc() != null ? vb.getCitydesc() : "";
            permanentState = vb.getState() != null ? vb.getState() : "";
            permanentStatedesc = vb.getStatedesc() != null ? vb.getStatedesc() : "";
            permanentCountry = vb.getCountry() != null ? vb.getCountry() : "";
            permanentCountrydesc = vb.getCountrydesc() != null ? vb.getCountrydesc() : "";
            permanentPin = vb.getPin() != null ? vb.getPin() : "";
            permanentDurationOfStay = vb.getDurationStay() != null ? String.valueOf(vb.getDurationStay()) : "";
            permanentResidenceType = vb.getResidenceType() != null ? vb.getResidenceType() : "";
            presentAddress1 = vb.getComAddr1() != null ? vb.getComAddr1() : "";
            presentAddress2 = vb.getComAddr2() != null ? vb.getComAddr2() : "";
            presentAddress3 = vb.getComAddr3() != null ? vb.getComAddr3() : "";
            presentCity = vb.getComCity() != null ? vb.getComCity() : "";
            presentCitydesc = vb.getComCityedesc() != null ? vb.getComCityedesc() : "";
            presentState = vb.getComState() != null ? vb.getComState() : "";
            presentStatedesc = vb.getComStatedesc() != null ? vb.getComStatedesc() : "";
            presentCountry = vb.getComCountry() != null ? vb.getComCountry() : "";
            presentCountryDesc = vb.getComCountrydesc() != null ? vb.getComCountrydesc() : "";
            presentPin = vb.getComPin() != null ? vb.getComPin() : "";
            presentDurationOfStay = vb.getComDurationStay() != null ? String.valueOf(vb.getComDurationStay()) : "";
            presentResidenceType = vb.getComResidenceType() != null ? vb.getComResidenceType() : "";
            presentdistanceFromBranch = vb.getCommdistanceFromBranch() != null ? String.valueOf(vb.getCommdistanceFromBranch()) : "";
            permanentdistanceFromBranch = vb.getDistanceFromBranch() != null ? String.valueOf(vb.getDistanceFromBranch()) : "";
            preferred_flag = vb.getPreferredFlag() != null ? vb.getPreferredFlag() : "";
            current_residence_flag = vb.getCurrentResidenceFlag() != null ? vb.getCurrentResidenceFlag() : "";
            sameAsper = vb.getSameAsPer() != null ? vb.getSameAsPer() : "N";
            comm_proof_flg=vb.getAddrdocnameBpm()==null ?"N":"Y";
            basic_edu=vb.getEducation()!=null ?vb.getEducation():"";
            basic_pep=vb.getPoliticallyExposed()!=null ?vb.getPoliticallyExposed():"";
            commEditable=vb.getSameAsPer()!=null && "Y".equalsIgnoreCase(vb.getSameAsPer()) ?"noneditable":"";


        }

    }


%>
<form   class="det form-details  basicdetails" data-code="<%=apptype%>-3"  data-completed="<%=completed%>"  action="#">
    <div class="kt d-flex justify-content-end" style="height: 0em;">
        <button class="edit-button btn btn-icon  btn-bg-light btn-color-info btn-sm me-1">
            <i class="ki-duotone  ki-pencil fs-2"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
        </button>
    </div>
    <div class="row mb-3">
        <div class="border-bottom pb-2 mb-2">
            <div class="d-flex">
                <span class="fw-bold">Basic Details</span>
                <button type="button"  class="cbsfetch btn-file kyc me-2 btn btn-outline-dark ms-auto text-end me-5">
                    <i class="ph ph-browsers  ms-2"></i>    Fetch from CBS
                </button>
            </div>
        </div>
        <div class="row">
            <div class="col-lg-3">
                <div class="mb-2">
                    <label class="form-label">SALUTATION</label>
                    <div class="form-control-feedback form-control-feedback-start">
                        <div class="form-control-feedback form-control-feedback-start">
                            <select class="form-control form-select basic_saltutation" name="basic_saltutation">
                                <option value="" >Please Select</option>
                                <%
                                    List<Misrct> titles = (List<Misrct>) request.getAttribute("titles");
                                    for (Misrct title : titles) {

                                        out.println("<option value=\"" + title.getCodevalue()+ "\" "+(title.getCodevalue().equals(basic_saltutation) ? "selected" : "" )+" >" + title.getCodedesc() + "</option>");
                                    }
                                %>
                            </select>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-4">
                <div class="mb-2">
                    <label class="form-label">NAME</label>
                    <div class="form-control-feedback form-control-feedback-start">
                        <input type="text" class="form-control basic_name" placeholder="" name="basic_name" value="<%=basic_name%>" readonly="">
                        <div class="form-control-feedback-icon">
                            <i class="ph-identification-card text-muted"></i>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-3">
                <div class="mb-2">
                    <label class="form-label">DOB</label>
                    <div class="form-control-feedback form-control-feedback-start">
                        <input type="date" class="form-control basic_dob" placeholder="DD-MM-YYYY" name="basic_dob" value="<%=basic_dob%>" readonly="">
                        <div class="form-control-feedback-icon">
                            <i class="ph-calendar-blank text-muted"></i>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-2">
                <div class="mb-2">
                    <label class="form-label">AGE</label>
                    <div class="form-control-feedback form-control-feedback-start">
                        <input type="text" class="form-control basic_age" placeholder="AGE" name="basic_age" value="<%=basic_age%>" readonly="">
                    </div>
                </div>
            </div>


        </div>
        <div class="row">
            <div class="col-lg-3">
                <div class="mb-2">
                    <label class="form-label">GENDER</label>
                    <div class="form-control-feedback form-control-feedback-start">

                        <select class="form-control form-select basic_gender" name="basic_gender">
                            <option value="" >Please Select</option>
                            <option value="M" <%=basic_gender.equals("M")? "selected" :""%>>MALE</option>
                            <option value="F"  <%=basic_gender.equals("F")? "selected" :""%>>FEMALE</option>
                            <%--                            <option value="F"  <%=basic_gender.equals("O")? "selected" :""%>>OTHERS</option>--%>
                        </select>
                    </div>
                </div>
            </div>
            <div class="col-lg-4">
                <div class="mb-2">
                    <label class="form-label">FATHER'S NAME</label>
                    <div class="form-control-feedback form-control-feedback-start">
                        <input type="text" class="form-control basic_ftname" placeholder="" name="basic_ftname" value="<%=basic_ftname%>">
                        <div class="form-control-feedback-icon">
                            <i class="ph-identification-card text-muted"></i>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-lg-5">
                <div class="mb-2">
                    <label class="form-label">MOTHER'S NAME</label>
                    <div class="form-control-feedback form-control-feedback-start">
                        <input type="text" class="form-control basic_mtname" placeholder="" name="basic_mtname" value="<%=basic_mtname%>">
                        <div class="form-control-feedback-icon">
                            <i class="ph-calendar-blank text-muted"></i>
                        </div>
                    </div>
                </div>
            </div>


        </div>
        <div class="row">
            <div class="col-lg-4">
                <div class="mb-2">
                    <label class="form-label">MARITIAL STATUS</label>
                    <div class="form-control-feedback form-control-feedback-start">
                        <select class="form-control form-select basic_ms" name="basic_ms" >
                            <option value="" >Please Select</option>
                            <option value="SINGL"  <%=basic_ms.equals("SINGL")? "selected" :""%>>SINGLE</option>
                            <option value="MARID"  <%=basic_ms.equals("MARID")? "selected" :""%>>MARRIED</option>
                            <option value="OTHERS"  <%=basic_ms.equals("OTHERS")? "selected" :""%>>OTHERS</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="col-lg-4">
                <div class="mb-2">
                    <label class="form-label">SPOUSE NAME</label>
                    <div class="form-control-feedback form-control-feedback-start">
                        <input type="text" class="form-control basic_spname" placeholder="" name="basic_spname" value="<%=basic_spname%>">
                        <div class="form-control-feedback-icon">
                            <i class="ph-calendar-blank text-muted"></i>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-4">
                <div class="mb-2">
                    <label class="form-label">OCCUPATION</label>
                    <div class="form-control-feedback form-control-feedback-start">
                        <div class="form-control-feedback form-control-feedback-start">
                            <select class="form-control form-select basic_occupation" name="basic_occupation">
                                <option value="" >Please Select</option>
                                <%
                                    List<Octdetails> octdetails = (List<Octdetails>) request.getAttribute("octDetails");
                                    for (Octdetails title : octdetails) {

                                        out.println("<option value=\"" + title.getOctValue()+ "\" "+(title.getOctValue().equals(basic_occupation) ? "selected" : "" )+" >" + title.getOctDesc() + "</option>");
                                    }
                                %>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

        </div>
        <div class="row">

            <div class="col-lg-8">
                <div class="mb-2">
                    <label class="form-label">MOBILE NUMBER</label>
                    <div class="d-flex">
                        <div class="col-md-3">
                            <select class="form-control form-select basic_mobcode" name="basic_mobcode" >
                                <%
                                    basic_mobcode=basic_mobcode.replaceAll("\\+","");
                                    List<String> mobCodes= (List<String>) request.getAttribute("mobCodes");
                                    for (String mobCode : mobCodes) {
                                        out.println("<option value=\"" + mobCode + "\" "+(basic_mobcode.equals(mobCode) ? "selected" : "" )+" >+" + mobCode  + "</option>");
                                    }
                                %>
                            </select>
                        </div>
                        <div class="col-md-9">
                            <div class="form-control-feedback form-control-feedback-start">
                                <input type="text" class="form-control basic_mob" placeholder="" name="basic_mob" value="<%=basic_mob%>">
                                <div class="form-control-feedback-icon">
                                    <i class="ph-calendar-blank text-muted"></i>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
            <div class="col-lg-4">
                <div class="mb-2">
                    <label class="form-label">EMAIL</label>
                    <div class="form-control-feedback form-control-feedback-start">
                        <div class="form-control-feedback form-control-feedback-start">
                            <input type="text" class="form-control basic_email" placeholder="" name="basic_email" value="<%=basic_email%>">
                            <div class="form-control-feedback-icon">
                                <i class="ph-calendar-blank text-muted"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>

        <div class="row">

            <div class="col-lg-4">
                <div class="mb-2">
                    <label class="form-label">Education</label>
                    <div class="form-control-feedback form-control-feedback-start">
                        <select class="form-control form-select basic_edu" name="basic_edu" >
                            <option value="" >Please Select</option>
                            <option value="UG"  <%=basic_edu.equals("UG")? "selected" :""%>>Under Graduate</option>
                            <option value="GG"  <%=basic_edu.equals("GG")? "selected" :""%>>Graduate</option>
                            <option value="PG"  <%=basic_edu.equals("PG")? "selected" :""%>>Post Graduate</option>
                            <option value="OT"  <%=basic_edu.equals("OT")? "selected" :""%>>Others</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="col-lg-4">
                <div class="mb-2">
                    <label class="form-label">Politically Exposed </label>
                    <div class="form-control-feedback form-control-feedback-start">
                        <select class="form-control form-select basic_pep" name="basic_pep" >
                            <option value="" >Please Select</option>
                            <option value="NA"  <%=basic_pep.equals("NA")? "selected" :""%>>Not applicable</option>
                            <option value="REP"  <%=basic_pep.equals("REP")? "selected" :""%>>Related to PEP</option>
                            <option value="PEP"  <%=basic_pep.equals("PEP")? "selected" :""%>>PEP (PoliticallyExposed Person)</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="col-lg-4">
                <div class="mb-2">
                    <label class="form-label">Annual Income </label>
                    <div class="form-control-feedback form-control-feedback-start">
                        <select class="form-control form-select basic_annualincome" name="basic_annualincome" >
                            <option value="" >Please Select</option>
                            <%
                                List<Misrct> annualIncome= (List<Misrct>) request.getAttribute("annualIncome");
                                for (Misrct ai : annualIncome) {
                                    out.println("<option value=\"" + ai.getCodevalue() + "\" "+(basic_annualincome.equals(ai.getCodevalue()) ? "selected" : "" )+" >" + ai.getCodedesc()  + "</option>");
                                }
                            %>
                        </select>
                    </div>
                </div>
            </div>
        </div>

        <div class="table-responsive border rounded">
            <table class="table">
                <thead>
                <tr>
                    <th><div class="border-bottom pb-2 mb-2">
                        <span class="fw-bold">Permanent Address<br><code>(will be replaced by Finacle Address for SIB Customer)</code></span>
                    </div></th>
                    <th><div class="border-bottom pb-2 mb-2">
                        <span class="fw-bold">Present Address<br><code class="invisible"></code></span>
                    </div></th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td class="w-50">
                        Address1: <input type="text" name="permanentAddress1"  class="form-control permanentCheck   permanentAddress1 <%=editable%>" value="<%=permanentAddress1%>" <%=permanentdisbaled%> >
                    </td>
                    <td>
                        Address1: <input type="text" class="form-control presentAddress1  <%=commEditable%>" name="presentAddress1"  value="<%=presentAddress1%>">
                    </td>
                </tr>
                <tr>
                    <td class="w-50">
                        Address2: <input type="text" name="permanentAddress2"  class="form-control permanentCheck  permanentAddress2  <%=editable%>" value="<%=permanentAddress2%>" <%=permanentdisbaled%> >
                    </td>
                    <td>
                        Address2: <input type="text" class="form-control presentAddress2 <%=commEditable%>  " name="presentAddress2"  value="<%=presentAddress2%>" >
                    </td>
                </tr>
                <tr>
                    <td class="w-50">
                        Address3: <input type="text" name="permanentAddress3"  class="form-control permanentCheck  permanentAddress3  <%=editable%>" value="<%=permanentAddress3%>"  <%=permanentdisbaled%>>
                    </td>
                    <td>
                        Address3: <input type="text" class="form-control presentAddress3  <%=commEditable%>" name="presentAddress3"  value="<%=presentAddress3%>">
                    </td>
                </tr>

                <tr>
                    <td class="w-50">
                        PIN: <input type="text" name="permanentPin" class="form-control permanentCheck pincode permanentPin  <%=editable%>" value="<%=permanentPin%>" <%=permanentdisbaled%>>
                    </td>
                    <td>
                        PIN: <input type="text" name="presentPin" class="form-control pincode presentPin  <%=commEditable%>" value="<%=presentPin%>"><br>
                    </td>
                </tr>
                <tr>
                    <td class="w-50">

                        City: <select name="permanentCity" class="form-control form-select  permanentCheck <%=editable%> permanentCity basic_city" <%=permanentdisbaled%>>

                        <%
                            if(permanentCity!=null){
                                out.print("<option value=\""+permanentCity+"\" >"+permanentCitydesc+"</option>");
                            }
                        %>

                    </select>
                    </td>
                    <td>
                        City: <select name="presentCity"    class="form-control  <%=commEditable%> form-select presentCity basic_city">

                        <%
                            if(presentCity!=null){
                                out.print("<option value=\""+presentCity+"\" >"+presentCitydesc+"</option>");
                            }
                        %>
                    </select>
                    </td>
                </tr>
                <tr>
                    <td class="w-50">

                        <select name="permanentState" class="form-control form-select permanentState permanentCheck  <%=editable%> basic_state" <%=permanentdisbaled%>>
                            State:

                            <%
                                if(permanentState!=null){
                                    out.print("<option value=\""+permanentState+"\" >"+permanentStatedesc+"</option>");
                                }
                            %>
                        </select>
                    </td>
                    <td>
                        State:
                        <select name="presentState"   class="form-control  <%=commEditable%> form-select presentState basic_state">

                            <%
                                if(presentState!=null){
                                    out.print("<option value=\""+presentState+"\" >"+presentStatedesc+"</option>");
                                }
                            %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td class="w-50">
                        Country:
                        <select name="permanentCountry" class="form-control form-select permanentCountry permanentCheck  <%=editable%> basic_country" <%=permanentdisbaled%>>

                            <%
                                if(permanentCountry!=null){
                                    out.print("<option value=\""+permanentCountry+"\" >"+permanentCountrydesc+"</option>");
                                }
                            %>
                        </select>
                    </td>
                    <td>
                        Country: <select name="presentCountry"   class="form-control form-select <%=commEditable%> presentCountry basic_country">

                        <%
                            if(presentCountry!=null){
                                out.print("<option value=\""+presentCountry+"\" >"+presentCountryDesc+"</option>");
                            }
                        %>
                    </select>
                    </td>
                </tr>
                <tr>
                    <td class="w-50">
                        Duration of stay (in months): <input type="number" name="permanentDurationOfStay" class="permanentCheck form-control permanentDurationOfStay" value="<%=permanentDurationOfStay%>"  min="1" max="999">
                    </td>
                    <td>
                        Duration of stay (in months): <input type="number" class="form-control presentDurationOfStay "  name="presentDurationOfStay" value="<%=presentDurationOfStay%>"  min="1" max="999">
                    </td>
                </tr>
                <tr>
                    <td class="w-50">
                        Distance From Branch (in  km/s): <input type="number" name="permanentdistanceFromBranch" class="permanentCheck form-control permanentdistanceFromBranch" value="<%=permanentdistanceFromBranch%>"  min="1" max="999">
                    </td>
                    <td>
                        Distance From Branch (in km/s): <input type="number" class="form-control presentdistanceFromBranch "  name="presentdistanceFromBranch" value="<%=presentdistanceFromBranch%>"  min="1" max="999">
                    </td>
                </tr>
                <tr>
                    <td class="w-50">

                        Residence type: <select name="permanentResidenceType" class="form-control  permanentCheck form-select permanentResidenceType" >
                        <option value="" >Please Select</option>
                        <%
                            List<Misrct> residenceTypes = (List<Misrct>) request.getAttribute("residenceTypes");
                            for (Misrct residenceType : residenceTypes) {
                                out.println("<option value=\"" + residenceType.getCodevalue() + "\" "+(residenceType.getCodevalue().equals(permanentResidenceType) ? "selected" : "" )+" >" + residenceType.getCodedesc()  + "</option>");
                            }
                        %>
                    </select>
                    </td>
                    <td>
                        Residence type: <select name="presentResidenceType"   class="form-control form-select presentResidenceType">
                        <option value="" >Please Select</option>
                        <%
                    for (Misrct residenceType : residenceTypes) {
                        out.println("<option value=\"" + residenceType.getCodevalue() + "\" "+(residenceType.getCodevalue().equals(presentResidenceType) ? "selected" : "" )+" >" + residenceType.getCodedesc()  + "</option>");
                    }
                            %></select>
                    </td>
                </tr>
                <tr>
                    <td class="w-50">
                        <label>
                            <input type="checkbox" name="sameAsPermanent"  class="form-check-input sameAsPermanent"  <%= "Y".equals(sameAsper) ? "checked" : "" %> > Same as Permanent
                            <input type="hidden" name="sameAsper_flg" class="sameAsper_flg" value="<%=sameAsper%>">
                        </label>
                    </td>
                    <td>
                        <div class="mb-2">
                            <label class="form-label">Communication Address Proof</label>
                            <div class="form-control-feedback form-control-feedback-start">
                                <div class="form-control-feedback form-control-feedback-start">
                                    <select class="form-control form-select basic_cpoa" name="basic_cpoa" value="">
                                        <option value="" >Please Select</option>
                                        <%
                                            List<Misrct> commProof= (List<Misrct>) request.getAttribute("commProof");
                                            for (Misrct cp : commProof) {
                                                out.println("<option value=\"" + cp.getCodevalue() + "\" "+(basic_cpoa.equals(cp.getCodevalue()) ? "selected" : "" )+" >" + cp.getCodedesc()  + "</option>");
                                            }
                                        %>
                                    </select>
                                </div>
                            </div>
                            <div>
                                <input type="file" name="comm_proof"  class="form-control comm_proof">
                                <input type="hidden" name="comm_proof_flg" class="comm_proof_flg" value="<%=comm_proof_flg%>"/>
                            </div>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="col-lg-6 pt-3">
            <div class="mb-2">
                <label class="form-label">Preferred Address:</label>
                <div class="form-control-feedback form-control-feedback-start">
                    <div class="form-control-feedback form-control-feedback-start">
                        <select class="form-control form-select preferred_flag" name="preferred_flag" value="">
                            <option value="" >Please Select</option>
                            <option value="P" <%= "P".equals(preferred_flag) ? "selected" : "" %> >PERMANENT</option>
                            <option value="C" <%= "C".equals(preferred_flag) ? "selected" : "" %> >PRESENT</option>
                        </select>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-lg-6  pt-3">
            <div class="mb-2">
                <label class="form-label">Current residence:</label>
                <div class="form-control-feedback form-control-feedback-start">
                    <div class="form-control-feedback form-control-feedback-start">
                        <select class="form-control form-select current_residence_flag" name="current_residence_flag" value="">
                            <option value="" >Please Select</option>
                            <option value="P" <%= "P".equals(current_residence_flag) ? "selected" : "" %> >PERMANENT</option>
                            <option value="C" <%= "C".equals(current_residence_flag) ? "selected" : "" %> >PRESENT</option>
                        </select>
                    </div>
                </div>
            </div>
        </div>

        <div class="d-flex justify-content-center mt-3 ">
            <button class="btn btn-outline-info btn-file  my-1 me-2 losdedup-button"><i class="ph-file-cloud  ms-2"></i>Los -ve List</button>
        </div>
        <div class="table-responsive  rounded mb-3">
            <table class="table losdedupetable">
                <thead>
                <tr>
                    <th>WI Name</th>
                    <th>Customer Name</th>
                    <th>Loan Type</th>
                    <th>WI Status</th>
                    <th>App Type</th>
                    <th>Reject Reason</th>
                    <th>DO Remarks</th>
                    <th>DOB</th>
                    <th>Aadhaar</th>
                    <th>PAN No</th>
                    <th>Voter ID</th>
                    <th>Passport No</th>
                    <th>Drive Lic</th>
                    <th>GST No</th>
                    <th>Corp ID</th>
                </tr>
                </thead>
                <tbody>
                <%
                    boolean losflag=false;
                    int loscount=0;
                if(applicant!=null){
                    List<LosDedupeEntity> entity = (List<LosDedupeEntity>) request.getAttribute("losdedup");
                    loscount=entity.size();
                    if(entity!=null)
                     entity=entity.stream().filter(f->applicant.getApplicantId().equals(f.getApplicantId())).collect(Collectors.toList());
                    for (int i = 0; i < entity.size(); i++) {
                        //losflag=true;
                        LosDedupeEntity ent = entity.get(i);
                            if ("N".equals(ent.getDedupflag())) {
                                out.print("<tr class=\"bg-light-whitetr\"><td colspan=\"15\"><b>" + ent.getDedupmsg() + "</b></td></tr>");
                            } else {
                                out.print("<tr class=\"bg-light-whitetr\">" +
                                        "<td>" + (ent.getWiName() != null ? ent.getWiName() : "") + "</td>" +
                                        "<td>" + (ent.getCustName() != null ? ent.getCustName() : "") + "</td>" +
                                        "<td>" + (ent.getLoanType() != null ? ent.getLoanType() : "") + "</td>" +
                                        "<td>" + (ent.getWiStatus() != null ? ent.getWiStatus() : "") + "</td>" +
                                        "<td>" + (ent.getAppType() != null ? ent.getAppType() : "") + "</td>" +
                                        "<td>" + (ent.getRejectReason() != null ? ent.getRejectReason() : "") + "</td>" +
                                        "<td>" + (ent.getDoRemarks() != null ? ent.getDoRemarks() : "") + "</td>" +
                                        "<td>" + (ent.getDob() != null ? ent.getDob() : "") + "</td>" +
                                        "<td>" + (ent.getAadhaar() != null ? ent.getAadhaar() : "") + "</td>" +
                                        "<td>" + (ent.getPanNo() != null ? ent.getPanNo() : "") + "</td>" +
                                        "<td>" + (ent.getVoterID() != null ? ent.getVoterID() : "") + "</td>" +
                                        "<td>" + (ent.getPassportNo() != null ? ent.getPassportNo() : "") + "</td>" +
                                        "<td>" + (ent.getDriveLic() != null ? ent.getDriveLic() : "") + "</td>" +
                                        "<td>" + (ent.getGstNo() != null ? ent.getGstNo() : "") + "</td>" +
                                        "<td>" + (ent.getCorpID() != null ? ent.getCorpID() : "") + "</td>" +
                                        "</tr>");
                            }
                        }
                }
                %>

                </tbody>
            </table>
        </div>
        <div class="d-flex justify-content-center">
            <button class="btn btn-outline-pink btn-file my-1 me-2 findedup-button"><i class="ph-file-cloud  ms-2"></i>Finacle Dedup</button>
        </div>
        <div class="table-responsive  rounded  mb-3" >
            <table class="table findedupetable">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Cust ID</th>
                    <th>Customer Name</th>
                    <th>Email</th>
                    <th>Mobile</th>
                    <th>Voter ID</th>
                    <th>Aadhaar Ref.</th>
                    <th>PAN</th>
                    <th>DOB</th>
                    <th>TDS CUSTID</th>
                </tr>
                </thead>
                <tbody>
                <%String checkflg="";boolean check=true;
                    boolean finflag=false;
                    int fincount=0;
                    if(applicant!=null){
                        List<FinDedupEntity> entity_2 = (List<FinDedupEntity>) request.getAttribute("findedup");
                        if(entity_2!=null)
                            entity_2=entity_2.stream().filter(f->applicant.getApplicantId().equals(f.getApplicantId())).collect(Collectors.toList());
                        fincount=entity_2.size();
                        for (int i = 0; i < entity_2.size(); i++) {
                           // finflag=true;
                            FinDedupEntity ent = entity_2.get(i);
                            checkflg="";
                            if(applicant.getCifId()!=null && applicant.getCifId().equals(ent.getCustomerid())) {
                                checkflg = "checked";
                                check=false;
                            }
                            if(i==entity_2.size()-1 && check)
                                checkflg="checked";
                            if ("N".equals(ent.getDedupflag())) {
                                out.print("<tr class=\"bg-light-whitetr\"><td colspan=\"15\"><b>" + ent.getDedupmsg() + "</b></td></tr>");
                            } else {
                                out.print("<tr class=\"bg-light-whitetr\">" +
                                        "<td><input type=\"radio\" name=\"dedupcustid\" class=\"form-check-input dedupcustid\" value=\""+ent.getCustomerid()+"\" "+checkflg+"/></td>" +
                                        "<td>" + (ent.getCustomerid() != null ? ent.getCustomerid() : "") + "</td>" +
                                        "<td>" + (ent.getName() != null ? ent.getName() : "") + "</td>" +
                                        "<td>" + (ent.getEmailid() != null ? ent.getEmailid() : "") + "</td>" +
                                        "<td>" + (ent.getMobilephone() != null ? ent.getMobilephone() : "") + "</td>" +
                                        "<td>" + (ent.getVoterid() != null ? ent.getVoterid() : "") + "</td>" +
                                        "<td>" + (ent.getAadharRefNo() != null ? ent.getAadharRefNo() : "") + "</td>" +
                                        "<td>" + (ent.getPan() != null ? ent.getPan() : "") + "</td>" +
                                        "<td>" + (ent.getDob() != null ? ent.getDob() : "") + "</td>" +
                                        "<td>" + (ent.getTdsCustomerid() != null ? ent.getTdsCustomerid() : "") + "</td>" +
                                        "</tr>");
                            }
                        }

                    }
                %>
                </tbody>
            </table>
        </div>

        <input type="hidden" name="losflag" class="losflag" value="<%=losflag%>">
        <input type="hidden" name="loscount" class="loscount" value="<%=loscount%>">
        <input type="hidden" name="fincount" class="fincount" value="<%=fincount%>">
        <input type="hidden" name="finflag" class="finflag" value="<%=finflag%>">
        <div class="text-end">

<%--            <button class="btn btn-yellow my-1 me-2 edit-button"><i class="ph-note-pencil  ms-2"></i>Edit</button>--%>
            <button href="#kyc"  class="btn btn-primary save-button">Save<i class="ph-paper-plane-tilt ms-2"></i></button>
        </div>

    </div>


</form>
<%!
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
%>