<%--
  Created by IntelliJ IDEA.
  User: SIBL15719
  Date: 17-07-2024
  Time: 15:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanWarn" %>
<%@ page import="org.aspectj.asm.IRelationship" %>
<%@ page import="java.util.*" %>

<%
    Employee userdt= (Employee) request.getAttribute("userdata");
    VehicleLoanApplicant applicant=null;
    if(request.getAttribute("general")!=null)
        applicant = (VehicleLoanApplicant) request.getAttribute("general");
    String apptype= request.getAttribute("apptype").toString();
    String sibCustomer="",custID="",residentialStatus="",appid="",rsm_sol="",rsm_solname="";
    String rsm_ppc="",rsm_ppcname="";
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
        can_ppc= applicant.getCanvassedppc()==null ?"":applicant.getCanvassedppc();
        can_ppcname= applicant.getCanvassedppcname()==null ?"":applicant.getCanvassedppcname();
    }

    String coapponly=request.getParameter("coapponly");
    String apponly=request.getParameter("apponly");
    coapponly=coapponly==null?"N":coapponly;
    apponly=apponly==null?"N":apponly;
    int totalwarns=0;
    List<VehicleLoanWarn> vehicleLoanWarns=new ArrayList<>((Collection) request.getAttribute("warning"));
    String accordion_style="btn-active-light-primary";
    String showFlg="";
%>
<div class="kt">
<div class="border rounded px-7 py-1">
    <div class="accordion-header py-1 d-flex collapsed" data-bs-toggle="collapse" id="warningtableDetails" data-bs-target="#warningtable">

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
                    <table class="table table-bordered">
                        <thead>
                        <tr class="bg-white">
                            <th>
                                Appicant Type
                            </th>
                            <%--                                <th>--%>
                            <%--                                    Appicant ID--%>
                            <%--                                </th>--%>
                            <th>
                                Warning
                            </th>
                            <th>
                                Finacle Value (during entry)
                            </th>
                            <th>
                                Entered Value
                            </th>
                            <th>
                                Severity
                            </th>
                            <th>
                                Current Finacle Value
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                            int counter=0;
                            List<Map<String, Object>> vehicleLoanWarnsWithAdditionalAttr = (List<Map<String, Object>>) request.getAttribute("warning");
                            if (vehicleLoanWarnsWithAdditionalAttr == null || vehicleLoanWarnsWithAdditionalAttr.isEmpty()) {
                        %>
                        <tr class=" bg-white text-center">
                            <td colspan="6" class="text-center">
                                No Warnings
                            </td>
                        </tr>
                        <%
                        }else{
                            for (Map<String, Object> map : vehicleLoanWarnsWithAdditionalAttr) {
                                VehicleLoanWarn vlw = (VehicleLoanWarn) map.get("vehicleLoanWarn");
                                String currentcbsval = (String) map.get("currentcbsval");
                                if(!currentcbsval.equals(vlw.getWiValue())){
                                    if(vlw.getSeverity().equals("High") || vlw.getSeverity().equals("Medium"))
                                    totalwarns=totalwarns+1;
                                }
                        %>
                        <tr class=" text-center">
                            <td class="text-center bg-white">
                                <%=vlw.getApplicantType()%>
                            </td>
                            <%--                                <td class="text-center bg-white">--%>
                            <%--                                    <%=vlw.getApplicantId()%>--%>
                            <%--                                </td>--%>
                            <td class="text-center bg-white">
                                <%=vlw.getWarnDesc()%>
                            </td>
                            <td class="text-center bg-white">
                                <%=vlw.getCbsValue()%>
                            </td>
                            <td class="text-center bg-white">
                                <%=vlw.getWiValue()%>
                            </td>
                            <td class="text-center <%if(vlw.getSeverity()== null){out.println(" bg-white text-dark");}else if(vlw.getSeverity().equals("Low")){out.println(" bg-yellow text-white");}else if(vlw.getSeverity().equals("Medium")){out.println(" bg-warning text-white");}else if(vlw.getSeverity().equals("High")){out.println(" bg-danger text-white");}else{out.println(" bg-white text-dark");} %>">
                                <%=vlw.getSeverity()%>
                            </td>
                            <td class="text-center bg-white">
                                <%=currentcbsval%>
                            </td>
                        </tr>
                        <%
                                    counter++;
                                }
                            }
                        %>
                        </tbody>
                    </table>
                    <input type="hidden" id="totalwarnings" class="totalwarnings" value="<%=totalwarns%>"/>
                    <input type="hidden" id="counter" class="counter" value="<%=counter%>"/>
                </div>
            </div>



            <div class="col-lg-12 form-control-feedback form-control-feedback-start ms-3 mb-5">
                <label class="form-check form-check-inline form-check-custom form-check-light "><!--form-check-solid-->
                    <input type="checkbox" name="consent" id="consent" value="1" class="form-check-input border border-dark">
                    <span class="fs-base ms-1">I hereby declare that the values given are matching with CBS values and ensured the correctness </span>
                    <!--form-check-label text-muted -->
                </label>
            </div>



        </form>
    </div>
</div>


</div>