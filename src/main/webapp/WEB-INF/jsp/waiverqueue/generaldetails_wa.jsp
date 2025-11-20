<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="org.aspectj.asm.IRelationship" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>

<%
  Employee userdt= (Employee) request.getAttribute("userdata");
  VehicleLoanApplicant applicant=null;
  if(request.getAttribute("general")!=null)
    applicant = (VehicleLoanApplicant) request.getAttribute("general");
  String apptype= request.getAttribute("apptype").toString();
  String sibCustomer="",custID="",residentialStatus="",appid="",rsm_sol="",rsm_solname="";
  String rsm_ppc="",rsm_ppcname="",rah_sol="",rah_solname="";
  String can_ppc="",can_ppcname="",relationship="";
  boolean completed=false;
  if(applicant!=null)
  {
    sibCustomer=applicant.getSibCustomer();
    custID=applicant.getCifId();
    residentialStatus=applicant.getResidentFlg();
    completed=true;
    appid= String.valueOf(applicant.getApplicantId());


    rsm_sol= applicant.getRsmsol()==null ?"":applicant.getRsmsol();
    relationship = applicant.getRelationWithApplicant()==null ?"":applicant.getRelationWithApplicant();
    rsm_solname= applicant.getRsmsolname()==null ?"":applicant.getRsmsolname();
    rsm_ppc= applicant.getRsmppc()==null ?"":applicant.getRsmppc();
    rsm_ppcname= applicant.getRsmppcname()==null ?"":applicant.getRsmppcname();
    rah_sol= applicant.getRahsol()==null ?"":applicant.getRahsol();
    rah_solname= applicant.getRahsolname()==null ?"":applicant.getRahsolname();
    can_ppc= applicant.getCanvassedppc()==null ?"":applicant.getCanvassedppc();
    can_ppcname= applicant.getCanvassedppcname()==null ?"":applicant.getCanvassedppcname();
  }

  String coapponly=request.getParameter("coapponly");
  String apponly=request.getParameter("apponly");
  coapponly=coapponly==null?"N":coapponly;
  apponly=apponly==null?"N":apponly;
%>
<form   class="det form-details generaldetails" data-code="<%=apptype%>-1" action="#" data-completed="<%=completed%>" >
      <input type="hidden" class="bpmurl" value="<%=request.getAttribute("appurl")%>"/>
  <input type="hidden" name="appid" class="appid" value="<%=appid%>">
  <div class="row  mb-2">
    <label class="col-form-label col-lg-5">Residential Status of the Customer  <span class="text-danger">*</span></label>
    <div class="col-lg-7">
      <div class="form-check-horizontal">
        <label class="form-check form-check-inline">
          <input type="radio" class="form-check-input residentialStatus" name="residentialStatus" value="R" required=""  <%= "R".equals(residentialStatus) ? "checked" : "" %>>
          <span class="form-check-label">Resident</span>
        </label>
        <label class="form-check form-check-inline">
          <input type="radio" class="form-check-input residentialStatus" name="residentialStatus"  value="N" required=""  <%= "N".equals(residentialStatus) ? "checked" : "" %>>
          <span class="form-check-label" for="validationFormCheck3">NRI</span>
        </label>
      </div>
    </div>
  </div>
  <div class="row  mb-2">
    <label class="col-form-label col-lg-5">Whether the customer is existing customer or not?  <span class="text-danger">*</span></label>
    <div class="col-lg-7">
      <div class="form-check-horizontal">
        <label class="form-check form-check-inline">
          <input type="radio" class="form-check-input sibCustomer" name="sibCustomer" value="Y"  required="" <%= "Y".equals(sibCustomer) ? "checked" : "" %> >
          <span class="form-check-label">Yes</span>
        </label>
        <label class="form-check form-check-inline">
          <input type="radio" class="form-check-input sibCustomer" name="sibCustomer" value="N" required=""  <%= "N".equals(sibCustomer) ? "checked" : "" %>>
          <span class="form-check-label" for="validationFormCheck3">No</span>
        </label>
      </div>
    </div>
  </div>
  <div class="row custidparent  mb-2" style="<%= "N".equals(sibCustomer) ? "display : none":"" %>">
    <label class="col-form-label col-lg-5">Customer ID <span class="text-danger">*</span></label>
    <div class="col-lg-7 ">
      <div class="form-check-horizontal">
        <input type="text"  class="form-control custID"  name="custID" maxlength="9" minlength="9" placeholder="Enter Customer ID" value="<%=custID==null ?  "":custID%>">
      </div>
    </div>
  </div>

  <div class="alert alert-secondary border-0 alert-dismissible fade show">
    <span class="fw-semibold">Note: Once saved, the above fields cannot be edited. Please review all information carefully before submitting.</span>
  </div>
  <%
  if(coapponly.equals("Y")){
  %>
  <div class="row  mb-2">
    <label class="col-form-label col-lg-5">Relationship with Applicant <span class="text-danger">*</span></label>
    <div class="col-lg-7">
      <div class="form-check-horizontal">
        <select class="form-control form-select relation" name="relation">
          <%
            Map<Integer, String> options = new HashMap<>();

            options.put(1, "Brother");
            options.put(2, "Sister");
            options.put(3, "Son");
            options.put(4, "Daughter");
            options.put(5, "Wife");
            options.put(6, "Husband");
            options.put(7, "Others");
            options.put(8, "Father");
            options.put(9, "Mother");

            // Print the map to verify
            for (Map.Entry<Integer, String> entry : options.entrySet()) {
              out.print("<option value=\""+ entry.getKey()+"\"   "+(entry.getKey().equals(relationship) ? "selected" : "" )+"  >"+ entry.getValue()+"</option>");
            }


          %>
        </select>
      </div>
    </div>
  </div>
  <%}%>
  <%
  if(apponly.equals("Y")){
  %>
  <div class="row  mb-2">
    <label class="col-form-label col-lg-5">Canvassed PPC<span class="text-danger">*</span></label>
    <div class="col-lg-7">
      <div class="form-check-horizontal">
        <select class="form-control form-select canvassed_ppc" name="canvassed_ppc">
          <%
            if(can_ppc!=null){
              out.print("<option value=\""+can_ppc+"\" >"+can_ppcname+"("+can_ppc+")</option>");
            }
          %>
        </select>
      </div>
    </div>
  </div>
  <div class="row  mb-2">
    <label class="col-form-label col-lg-5">RSM PPC<span class="text-danger">*</span></label>
    <div class="col-lg-7">
      <div class="form-check-horizontal">
        <select class="form-control form-select rsm_ppc" name="rsm_ppc">
          <%
            if(rsm_ppc!=null){
              out.print("<option value=\""+rsm_ppc+"\" >"+rsm_ppcname+"("+rsm_ppc+")</option>");
            }
          %>
        </select>
      </div>
    </div>
  </div>
  <%if(userdt.getJoinedSol().equals("8032")){%>
      <div class="row  mb-2">
        <label class="col-form-label col-lg-5">RSM BRANCH<span class="text-danger">*</span></label>
        <div class="col-lg-7">
          <div class="form-check-horizontal">
            <select class="form-control form-select rsm_sol" name="rsm_sol">
              <%
                if(rsm_sol!=null){
                  out.print("<option value=\""+rsm_sol+"\" >"+rsm_solname+"("+rsm_sol+")</option>");
                }
              %>
            </select>
          </div>
        </div>
      </div>
  <%}%>
  <%if( userdt.getJoinedSol().equals("8063")){%>
  <div class="row  mb-2">
    <label class="col-form-label col-lg-5">RSM BRANCH<span class="text-danger">*</span></label>
    <div class="col-lg-7">
      <div class="form-check-horizontal">
        <select class="form-control form-select rah_sol" name="rah_sol">
          <%
            if(rah_sol!=null){
              out.print("<option value=\""+rah_sol+"\" >"+rah_solname+"("+rsm_sol+")</option>");
            }
          %>
        </select>
      </div>
    </div>
  </div>
  <%}%>
  <%}%>

</form>
