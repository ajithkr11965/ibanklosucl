<%@ page import="com.sib.ibanklosucl.model.*" %>
<%@ page import="java.util.*" %><%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 09-05-2024
  Time: 15:13
  To change this template use File | Settings | File Templates.
--%>
<%

    String apptype= request.getAttribute("apptype").toString();
    boolean completed=false,mod=false;
    int totalEmpExp=0;
    int totalBusExp=0;
    VehicleLoanApplicant applicant = null;
    VLEmployment employment= null;
    if (request.getAttribute("general") != null) {
        applicant = (VehicleLoanApplicant) request.getAttribute("general");
    } else {
    }


    // VLEmployment employment = employmentList.get(0); // Assuming slno is unique
    // out.println(employment);
    //out.println(employment.getEmployment_type());
    //out.println(employmentEmpList);
    String applicant_age="";
    List<VLEmploymentemp> vlEmploymentempList = new ArrayList<>();
    List<VLEmploymentocc> vlEmploymentoccList = new ArrayList<>();

    if(applicant!=null) {
        employment= applicant.getVlEmployment();
        if(employment!=null) {
            if (employment.getEmployment_type().equals("SALARIED") || employment.getEmployment_type().equals("PENSIONER")) {
                vlEmploymentempList = employment.getVlEmploymentempList();
            } else {
                vlEmploymentoccList = employment.getVlEmploymentoccList();
            }
            completed = true;
            applicant_age =employment.getRetirement_age();
        }

    }
//out.print(vlEmploymentempList);

%>

<form   class="det form-details  employmentdetails" data-code="<%=apptype%>-4"  data-completed="<%=completed%>"  action="#">
    <input type="hidden" name="slno_hid_emp" id="slno_hid_emp"  class="hid_slno_emp" value="<%=(employment !=null) ? employment.getSlno() : ""%>"/>
    <div class="border-bottom pb-2 mb-2">
        <span class="fw-bold">Employment Details</span>
    </div>
    <div class="row mt-3 mb-3 ms-2 me-2">
        <div style="border: 3px dotted #d0e3f9;border-radius: 15px !important;background: #f8f9fb;" class="borders rounded  border-dashed  ">
            <div class="list-feed list-feed-solid ms-3 mt-3 mb-3">
                <div class="list-feed-item">
                    <span class="badge bg- bg-opacity-10 text-secondary" style="font-size: 13px;">Select Employment Type</span>
                </div>
                <div class="list-feed-item mb-4">
                    <select name="selemptype" id="selemptype" class="form-control select selemptype">
                        <option value="SALARIED" <%= (employment != null && "SALARIED".equals(employment.getEmployment_type())) ? "selected" : "" %>>Salaried</option>
                        <option value="SEP" <%=  (employment != null && "SEP".equals(employment.getEmployment_type())) ? "selected" : "" %>>Self-employed professional (SEP)</option>
                        <option value="SENP" <%=  (employment != null && "SENP".equals(employment.getEmployment_type()))? "selected" : "" %>>Self-employed non-professional (SENP)</option>
                        <option value="AGRICULTURIST" <%=  (employment != null && "AGRICULTURIST".equals(employment.getEmployment_type())) ? "selected" : "" %>>Agriculturist</option>
                        <option value="PENSIONER" <%=  (employment != null && "PENSIONER".equals(employment.getEmployment_type())) ? "selected" : "" %>>Pensioner</option>
                        <option value="NONE" <%=  (employment != null && "NONE".equals(employment.getEmployment_type())) ? "selected" : "" %>>None</option>
                    </select>
                </div>
            </div>
        </div>
    </div>


    <div class="row mb-3">
        <%if(!vlEmploymentempList.isEmpty()) {%>
        <div class="emp-sal-pen">
            <div class="card">

                <div class="card-body emp-sal-ret">
                    <div style="border: 3px dotted #eee;border-radius: 15px !important;background: #f8f9fb;" class="borders rounded  border-dashed  ">
                        <div class="list-feed list-feed-solid ms-3 mt-3 mb-3">
                            <div class="list-feed-item border-dark">
                                <span class="badge bg- bg-opacity-10 text-dark" style="font-size: 13px;">What is your retirement age?</span>
                            </div>
                            <div class="list-feed-item mb-4 me-3 border-dark">
                                <input type="text" class="form-control"  name="emp-retage" placeholder="Enter Retirement Age" value="<%=(employment !=null) ? applicant_age : ""%>">
                            </div>
                        </div>
                    </div>
                </div>

                <div id="emptable-sal-div-id" class="table-responsive border-white border-opacity-15 mb-3 emptable-sal-div">
                    <table class="table table-bordered emptable-sal-pen"  >
                        <thead>
                        <tr>
                            <th>Employer Name</th>
                            <th>Employer address</th>
                            <th>Work experience (in months)</th>
                            <th>Current employer</th>
                        </tr>
                        </thead>

                        <tbody class="emptable-sal-pen-body" name="emptable-sal-pen-body">


                        <%
                            for( VLEmploymentemp vlEmployment : vlEmploymentempList) {
                                if(vlEmployment.getDelFlg().equals("N"))
                                {
                                    totalEmpExp= totalEmpExp+Integer.parseInt(vlEmployment.getWorkExperience());
                        %>
                        <tr>
                            <td><input type="text" name="empname" id="empname" class="form-control" value="<%=vlEmployment.getEmployerName()%>"></td>
                            <td><input type="text" name="empaddress" id="empaddress" class="form-control"  value="<%=vlEmployment.getEmployerAddress()%>"></td>
                            <td><input type="text" name="empworkexp" id="empworkexp" class="form-control empworkexp-sal"  value="<%=vlEmployment.getWorkExperience()%>"></td>
                            <td><%=(vlEmployment.getCurrentEmployer().equals("true"))?"Yes":"No"%></td>

                        </tr>
                        <%}}%>

                        </tbody>
                    </table>
                    <div  class="card-footer ">
                        <div class="row mt-3 mb-3 ms-2 me-2">
                            <div style="border: 3px dotted #c8f9ba;border-radius: 15px !important;background: #f6fff5;" class="borders rounded  border-dashed  ">

                                <div class="mt-2 mb-2" style="justify-content: center;display: flex;">
                                    Total Work Experience (in months) : <span class="fw-semibold ms-1 empworkexp-sal-tot"><%=totalEmpExp%></span>
                                </div>

                            </div>
                        </div>
                    </div>

                </div>

            </div>
        </div>
        <%} if(!vlEmploymentoccList.isEmpty()) {%>
        <div class="emp-oth">
            <div class="card">

                <div class="table-responsive border-white border-opacity-15 mb-3 emptable-oth-div">

                    <table class="table table-bordered emptable-oth"  >
                        <thead>
                        <tr>
                            <th>Occupation type</th>
                            <th>Occupation Code</th>
                            <th>Profession</th>
                            <th>Occupation Name</th>
                            <th>Business experience (in months)</th>
                        </tr>
                        </thead>
                        <tbody class="emptable-oth-body">



                        <%
                            for( VLEmploymentocc vlEmployment : vlEmploymentoccList) {
                                if(vlEmployment.getDelFlg().equals("N"))
                                {
                                    totalBusExp= totalBusExp+Integer.parseInt(vlEmployment.getBusinessExperience());
                        %>
                        <tr>
                            <td>
                                <select name="empothtype" id="empothtype" class="form-control select ">
                                    <option value="OTH" <%= (vlEmployment != null && "OTH".equals(vlEmployment.getOccupationType())) ? "selected" : "" %>>Others</option>
                                    <option value="SALARIED" <%= (vlEmployment != null && "SALARIED".equals(vlEmployment.getOccupationType())) ? "selected" : "" %>>Salaried</option>
                                    <option value="SEP" <%= (vlEmployment != null && "SEP".equals(vlEmployment.getOccupationType())) ? "selected" : "" %>>Self-employed professional (SEP)</option>
                                    <option value="SENP" <%= (vlEmployment != null && "SENP".equals(vlEmployment.getOccupationType())) ? "selected" : "" %>>Self-employed non-professional (SENP)</option>
                                </select>
                            </td>
                            <td>
                                <select name="empothcode" id="empothcode" class="form-control select ">
                                    <option value="DRCA" <%= (vlEmployment != null && "DRCA".equals(vlEmployment.getOccupationCode())) ? "selected" : "" %>>Doctors or CAs of reputedCo or MNC</option>
                                    <option value="GOVT" <%= (vlEmployment != null && "GOVT".equals(vlEmployment.getOccupationCode())) ? "selected" : "" %>>Employed in Govt or PSU</option>
                                    <option value="OTH" <%= (vlEmployment != null && "OTH".equals(vlEmployment.getOccupationCode())) ? "selected" : "" %>>Others</option>
                                    <option value="PVT" <%= (vlEmployment != null && "PVT".equals(vlEmployment.getOccupationCode())) ? "selected" : "" %>>Pvt sector companies in India or abroad</option>
                                    <option value="SEPSENP"  <%= (vlEmployment != null && "SEPSENP".equals(vlEmployment.getOccupationCode())) ? "selected" : "" %>>SEP or SENP</option>
                                </select>
                            </td>
                            <td>
                                <select name="empothprof" id="empothprof" class="form-control select ">
                                    <option value="ARCH" <%= (vlEmployment != null && "ARCH".equals(vlEmployment.getProfession())) ? "selected" : "" %>>Architect</option>
                                    <option value="CA" <%= (vlEmployment != null && "CA".equals(vlEmployment.getProfession())) ? "selected" : "" %>>Chartred Accountant</option>
                                    <option value="CS" <%= (vlEmployment != null && "CS".equals(vlEmployment.getProfession())) ? "selected" : "" %>>Company Secretary</option>
                                    <option value="COSTACT" <%= (vlEmployment != null && "COSTACT".equals(vlEmployment.getProfession())) ? "selected" : "" %>>Cost Accountant</option>
                                    <option value="DR" <%= (vlEmployment != null && "DR".equals(vlEmployment.getProfession())) ? "selected" : "" %>>Doctor</option>
                                    <option value="ENG" <%= (vlEmployment != null && "ENG".equals(vlEmployment.getProfession())) ? "selected" : "" %>>Engineer</option>
                                    <option value="LAW" <%= (vlEmployment != null && "LAW".equals(vlEmployment.getProfession())) ? "selected" : "" %>>Lawyer</option>
                                    <option value="OTH" <%= (vlEmployment != null && "OTH".equals(vlEmployment.getProfession())) ? "selected" : "" %>>Others</option>
                                </select>
                            </td>
                            <td><input type="text" name="empothname" id="empothname" class="form-control"  placeholder="Enter Name" value="<%=vlEmployment.getEmployerName()%>"></td>
                            <td><input type="text" name="empothwrk" id="empothwrk" class="form-control empworkexp-oth"  placeholder="Enter Experience" value="<%=vlEmployment.getBusinessExperience()%>"></td>

                        </tr>

                        <% } }   %>




                        </tbody>
                    </table>
                    <div  class="card-footer ">
                        <div class="row">
                            <div class="col-lg-10">
                                <span class="badge bg-secondary bg-opacity-10 text-secondary mt-2">Note : Latest Business experience should be entered on the top</span>
                            </div>

                        </div>
                        <div class="row mt-3 mb-3 ms-2 me-2">
                            <div style="border: 3px dotted #c8f9ba;border-radius: 15px !important;background: #f6fff5;" class="borders rounded  border-dashed  ">

                                <div class="mt-2 mb-2" style="justify-content: center;display: flex;">
                                    Total Business Experience (in months) : <span class="fw-semibold ms-1 empworkexp-oth-tot"><%=totalBusExp%></span>
                                </div>

                            </div>
                        </div>
                    </div>


                </div>

            </div>
        </div><%}%>

    </div>

    <input type="hidden" name="total_experience_emp" class="total_experience_emp" id="total_experience_emp" />
    <input type="hidden" name="hid_ino_emp" class="hid_ino_emp" id="hid_ino_emp" />






</form>