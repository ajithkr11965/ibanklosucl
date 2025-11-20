<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="org.aspectj.asm.IRelationship" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>

<%
  Employee userdt= (Employee) request.getAttribute("userdata");
  VehicleLoanApplicant applicant=null;
  if(request.getAttribute("general")!=null)
    applicant = (VehicleLoanApplicant) request.getAttribute("general");
  String apptype= request.getAttribute("apptype").toString();
  String sibCustomer="",custID="",residentialStatus="",appid="",rsm_sol="",rsm_solname="";
  String rsm_ppc="",rsm_ppcname="",rah_sol="",rah_solname="";
  String can_ppc="",can_ppcname="",relationship="",appType="",applName="";
  boolean completed=false;
  if(applicant!=null)
  {
    sibCustomer=applicant.getSibCustomer();
    custID=applicant.getCifId();
    residentialStatus=applicant.getResidentFlg();
    completed=true;
    appid= String.valueOf(applicant.getApplicantId());

    appType=applicant.getApplicantType()==null?applicant.getApplicantType():"A";
    rsm_sol= applicant.getRsmsol()==null ?"":applicant.getRsmsol();
    relationship = applicant.getRelationWithApplicant()==null ?"":applicant.getRelationWithApplicant();
    rsm_solname= applicant.getRsmsolname()==null ?"":applicant.getRsmsolname();
    rsm_ppc= applicant.getRsmppc()==null ?"":applicant.getRsmppc();
    rah_sol= applicant.getRahsol()==null ?"":applicant.getRahsol();
    rah_solname= applicant.getRahsolname()==null ?"":applicant.getRahsolname();
    rsm_ppcname= applicant.getRsmppcname()==null ?"":applicant.getRsmppcname();
    can_ppc= applicant.getCanvassedppc()==null ?"":applicant.getCanvassedppc();
    can_ppcname= applicant.getCanvassedppcname()==null ?"":applicant.getCanvassedppcname();
    applName=applicant.getApplName()==null?"":applicant.getApplName();
  }

  String coapponly=request.getParameter("coapponly");
  String apponly=request.getParameter("apponly");
  coapponly=coapponly==null?"N":coapponly;
  apponly=apponly==null?"N":apponly;
  VehicleLoanMaster vehicleLoanMaster=(VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
  String channel=vehicleLoanMaster.getChannel();
%>
<form   class="det form-details generaldetails" data-code="<%=apptype%>-1" action="#" data-completed="<%=completed%>" >
<%--  <div class="kt d-flex justify-content-end" style="height: 0em;">--%>
<%--    <button class="edit-button btn btn-icon  btn-bg-light btn-color-info btn-sm me-1">--%>
<%--      <i class="ki-duotone  ki-pencil fs-2"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>--%>
<%--    </button>--%>
<%--  </div>--%>
      <input type="hidden" class="bpmurl" value="<%=request.getAttribute("appurl")%>"/>
  <input type="hidden" name="appid" class="appid" value="<%=appid%>">
  <input type="hidden" name="apptype" class="appType" value="<%=appType!=null?appType:""%>">
  <div class="row  mb-2">
    <label class="col-form-label col-lg-5">Residential Status of the Customer  <span class="text-danger">*</span></label>
    <div class="col-lg-7">
      <div class="form-check-horizontal">
        <%if(!"".equals(residentialStatus) && false){
        %>
        <label class="form-check form-check-inline">
          <input type="radio" class="form-check-input residentialStatus nonreadable" name="residentialStatus" value="<%=residentialStatus%>" required="" checked  readonly>
          <span class="form-check-label"><%= "R".equals(residentialStatus)?"Resident":"NRI"%></span>
        </label>
        <%

        }else{%>
        <label class="form-check form-check-inline">
          <input type="radio" class="form-check-input residentialStatus " name="residentialStatus" value="R" required=""  <%= "R".equals(residentialStatus) ? "checked readonly" : "" %>>
          <span class="form-check-label">Resident</span>
        </label>
        <label class="form-check form-check-inline">
          <input type="radio" class="form-check-input residentialStatus " name="residentialStatus"  value="N" required=""  <%= "N".equals(residentialStatus) ? "checked readonly" : "" %>>
          <span class="form-check-label" for="validationFormCheck3">NRI</span>
        </label>
        <%}%>
      </div>
    </div>
  </div>
  <div class="row  mb-2">
    <label class="col-form-label col-lg-5">Whether the customer is existing customer  <span class="text-danger">*</span></label>
    <div class="col-lg-7">
      <div class="form-check-horizontal">
        <%if(!"".equals(sibCustomer) && false){
          %>
        <label class="form-check form-check-inline">
          <input type="radio" class="form-check-input sibCustomer nonreadable" name="sibCustomer" value="<%=sibCustomer%>"   required="" checked readonly  >
          <span class="form-check-label"><%= "Y".equals(sibCustomer)?"Yes":"No"%></span>
        </label>
          <%

         }else{%>
        <label class="form-check form-check-inline">
          <input type="radio" class="form-check-input sibCustomer " name="sibCustomer" value="Y"   required="" <%= "Y".equals(sibCustomer) ? "checked readonly" : "" %> >
          <span class="form-check-label">Yes</span>
        </label>
        <label class="form-check form-check-inline">
          <input type="radio" class="form-check-input sibCustomer " name="sibCustomer" value="N" required=""  <%= "N".equals(sibCustomer) ? "checked readonly" : "" %>>
          <span class="form-check-label" for="validationFormCheck3">No</span>
        </label>
        <%}%>
      </div>
    </div>
  </div>
  <div class="row custidparent  mb-2" style="<%= "N".equals(sibCustomer) ? "display : none":"" %>">
    <label class="col-form-label col-lg-5">Customer ID <span class="text-danger">*</span></label>
    <div class="col-lg-7">
      <div class="form-check-horizontal">
        <%if(!"".equals(sibCustomer) && false){
        %>
        <label class="form-check form-check-inline">
          <input type="hidden" class="custID" name="custID" value="<%= custID==null?"":custID%>"  >
          <span class="form-check-label"><%= custID==null?"":custID%></span>
<%--          <div class="text-success pt-2 userName"><i class="ph ph-user"></i><%=applName%></div>--%>
        </label>
        <%

        }else{%>
        <input type="text"  class="form-control custID "  name="custID" maxlength="9" minlength="9" placeholder="Enter Customer ID" value="<%=custID==null ?  "":custID%>">
        <div class="text-success pt-2 userName"></div>

        <%}%>
      </div>
    </div>
  </div>

  <%if("MARKET DSA PORTAL".equalsIgnoreCase(channel)||"DEALER PORTAL".equalsIgnoreCase(channel)||"DST PORTAL".equalsIgnoreCase(channel)){%>
    <div class="alert alert-secondary border-0 alert-dismissible fade show">
      <span class="fw-semibold">Note: The data has been sourced from DSA. Please reconfirm the pre-filled information before proceeding.</span>
<%--      <span class="fw-semibold">Note: Once saved, the above fields cannot be edited. Please review all information carefully before submitting.</span>--%>
    </div>
  <%}%>
  <%
  if(coapponly.equals("Y")){
  %>
  <div class="row  mb-2">
    <label class="col-form-label col-lg-5">Relationship with Applicant <span class="text-danger">*</span></label>
    <div class="col-lg-7">
      <div class="form-check-horizontal">
        <select class="form-control form-select relation" name="relation">
          <option value="" >Select</option>

          <%

            Map<String, String> options = new HashMap<>();
            options.put("001", "WIFE");
            options.put("002", "HUSBAND");
            options.put("003", "FATHER");
            options.put("004", "MOTHER");
            options.put("006", "SON");
            options.put("007", "DAUGHTER");
            options.put("008", "BROTHER");
            options.put("009", "SISTER");
            options.put("014", "SON IN LAW");
            options.put("015", "DAUGHTER IN LAW");
            options.put("016", "MOTHER IN LAW");
            options.put("017", "FATHER IN LAW");

            // Print the map to verify
            for (Map.Entry<String, String> entry : options.entrySet()) {
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
  <%if(userdt.getJoinedSol().equals("8032") ){%>
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
