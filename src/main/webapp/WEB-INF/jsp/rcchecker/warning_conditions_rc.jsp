<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanWarn" %>
<%@ page import="org.aspectj.asm.IRelationship" %>
<%@ page import="java.util.*" %>

<%
    Employee userdt = null;
    if (request.getAttribute("userdata") instanceof Employee) {
        userdt = (Employee) request.getAttribute("userdata");
    }
    VehicleLoanApplicant applicant=null;
    if(request.getAttribute("general")!=null)
        applicant = (VehicleLoanApplicant) request.getAttribute("general");
    String apptype = request.getAttribute("apptype") != null ? request.getAttribute("apptype").toString() : "";
    String sibCustomer="",custID="",residentialStatus="",appid="",rsm_sol="",rsm_solname="";
    String rsm_ppc="",rsm_ppcname="";
    String can_ppc="",can_ppcname="",relationship="";
    String  showFlg = "", accordion_style = "btn-active-light-primary";
    boolean completed=false;
    if(applicant!=null)
    {
        sibCustomer = applicant.getSibCustomer() != null ? applicant.getSibCustomer() : "";
        custID = applicant.getCifId() != null ? applicant.getCifId() : "";
        residentialStatus = applicant.getResidentFlg() != null ? applicant.getResidentFlg() : "";
        completed=true;
        try {
            appid = String.valueOf(applicant.getApplicantId());
        } catch (Exception e) {
            appid = "";
        }


        rsm_sol= applicant.getRsmsol()==null ?"":applicant.getRsmsol();
        relationship = applicant.getRelationWithApplicant()==null ?"":applicant.getRelationWithApplicant();
        rsm_solname= applicant.getRsmsolname()==null ?"":applicant.getRsmsolname();
        rsm_ppc= applicant.getRsmppc()==null ?"":applicant.getRsmppc();
        rsm_ppcname= applicant.getRsmppcname()==null ?"":applicant.getRsmppcname();
        can_ppc= applicant.getCanvassedppc()==null ?"":applicant.getCanvassedppc();
        can_ppcname= applicant.getCanvassedppcname()==null ?"":applicant.getCanvassedppcname();
    }

    String coapponly=request.getParameter("coapponly");
    String apponly=request.getParameter("apponly");
    coapponly=coapponly==null?"N":coapponly;
    apponly=apponly==null?"N":apponly;
    int totalwarns=0;
    List<VehicleLoanWarn> vehicleLoanWarns = new ArrayList<>();
    if (request.getAttribute("warning") instanceof Collection) {
        vehicleLoanWarns.addAll((Collection<VehicleLoanWarn>) request.getAttribute("warning"));
    }
%>
<div class="border rounded px-7 py-1">
    <div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="warningtableDetails" data-bs-target="#warningtable">

        <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2 <%=accordion_style%> <%=showFlg%>" for="warningtable">
            <i class="ki-duotone ki-scroll fs-4x me-4">
                <span class="path1"></span>
                <span class="path2"></span>
                <span class="path3"></span>
            </i>

            <span class="d-block fw-semibold text-start">
                        <span class="text-gray-900 fw-bold d-block fs-3">Warnings</span>
                        <span class="text-muted fw-semibold fs-6">
                          Data mismatch in Work item and finacle
                        </span>
                    </span>
        </label>

    </div>

    <div id="warningtable" class="fs-6 collapse ps-10" data-bs-parent="#vl_checker_int">
        <form   class="form-details fcvcpvcfr" data-code="<%=apptype%>-1" action="#" data-completed="<%=completed%>" >

            <input type="hidden" class="bpmurl" value="<%=request.getAttribute("appurl")%>"/>
            <input type="hidden" name="appid" class="appid" value="<%=appid%>">


            <div class="row mt-3 mb-3 ms-2 me-2">
                <div class="table-responsive">
                    <table class="table table-sm align-middle table-row-dashed table-row-gray-400 fs-8 gy-3">
                        <thead>
                        <tr class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">
                            <th>Appicant</th>
                            <th>Warning</th>
                            <th>Finacle Value (during entry)</th>
                            <th>Entered Value</th>
                            <th>Severity</th>
                            <th>Current Finacle Value</th>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                            List<Map<String, Object>> vehicleLoanWarnsWithAdditionalAttr = (List<Map<String, Object>>) request.getAttribute("warning");
                            if (vehicleLoanWarnsWithAdditionalAttr == null || vehicleLoanWarnsWithAdditionalAttr.isEmpty()) {
                        %>
                        <tr class=" bg-white text-center">
                            <td colspan="6" class="text-center">No Warnings</td>
                        </tr>
                        <%
                        }else{
                            for (Map<String, Object> map : vehicleLoanWarnsWithAdditionalAttr) {
                                VehicleLoanWarn vlw = (VehicleLoanWarn) map.get("vehicleLoanWarn");
                                String currentcbsval = (String) map.get("currentcbsval");
                                if (vlw != null && currentcbsval != null && !currentcbsval.equals(vlw.getWiValue())) {
                                    totalwarns++;
                        %>
                        <tr class="table-row-gray-400">
                            <td class="text-gray-800 text-hover-primary fs-7 fw-bold">
                                <%= vlw.getApplicantType() != null ? vlw.getApplicantType() : "" %>
                            </td>
                            <td class="text-center bg-white"><%= vlw.getWarnDesc() != null ? vlw.getWarnDesc() : "" %></td>
                            <td class="text-center bg-white"><%= vlw.getCbsValue() != null ? vlw.getCbsValue() : "" %></td>
                            <td class="text-center bg-white"><%= vlw.getWiValue() != null ? vlw.getWiValue() : "" %></td>
                            <td class="text-center <%= getSeverityClass(vlw.getSeverity()) %>"><%= vlw.getSeverity() != null ? vlw.getSeverity() : "" %></td>
                            <td class="text-center bg-white"><%=currentcbsval%></td>
                        </tr>
                        <%
                                    }
                                }
                            }
                        %>
                        </tbody>
                    </table>
                    <input type="hidden" id="totalwarnings" class="totalwarnings" value="<%=totalwarns%>"/>
                </div>
            </div>
        </form>
    </div>
</div>


<%!
    private String getSeverityClass(String severity) {
        if (severity == null) {
            return "bg-white text-dark";
        }
        switch (severity.toLowerCase()) {
            case "low":
                return "bg-yellow text-white mt-1";
            case "medium":
                return "bg-warning text-white mt-1";
            case "high":
                return "bg-danger text-white mt-1";
            default:
                return "bg-white text-dark mt-1";
        }
    }
%>
