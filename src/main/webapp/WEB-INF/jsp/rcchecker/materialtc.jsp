<%@ page import="com.sib.ibanklosucl.model.Misrct" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.sib.ibanklosucl.model.MaterialListData" %>
<%--
  Created by IntelliJ IDEA.
  User: SIBL17977
  Date: 22-05-2025
  Time: 11:28
  To change this template use File | Settings | File Templates.
--%>
<%--<script src="assets/js/custom/WI/materialtc.js"></script>--%>

<div class="border rounded px-7 py-3 mb-2">
    <div class="">
        <div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="materialDetailslink" data-bs-target="#materialDetailsContent">
            <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2" for="materialDetailsContent">
                <i class="ki-duotone ki-enjin-coin  fs-3x me-4">
                    <span class="path1"></span>
                    <span class="path2"></span>
                    <span class="path3"></span>
                    <span class="path4"></span>
                    <span class="path5"></span>
                    <span class="path6"></span>
                </i>
                <span class="d-block fw-semibold text-start">
                        <span class="text-gray-900 fw-bold d-block fs-4">Non-Financial Material Terms and Conditions</span>
                        <span class="text-muted fw-semibold fs-7">
                         Enter non-financial material terms and conditions on which non-compliance may attract penal charges
                        </span>
                    </span>
            </label>
        </div>


        <div id="materialDetailsContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">
            <ul class="nav-group-sub  p-3" id="materialDetails"  >
                <!-- Save Button -->

                <%
                    Boolean checker = request.getAttribute("checker")!=null? request.getAttribute("checker").toString().equals("Y"):false;
                    List<Misrct> materialList= request.getAttribute("RBCM")==null? new ArrayList<>() :(List<Misrct>) request.getAttribute("RBCM");
                    List<MaterialListData> materialListData= request.getAttribute("materialListData")==null? new ArrayList<>() :(List<MaterialListData>) request.getAttribute("materialListData");
                %>
                <table id="materialListTable" class="table table-sm align-middle table-row-dashed table-row-gray-400 fs-8 gy-3">
                    <thead>
                    <tr class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">
                        <th width="10">Slno</th>
                        <th width="40">Material terms and Conditions </th>
                        <th width="40">Condition in Detail</th>
                        <th width="20">Due Date for compliance</th>
                    </tr>
                    </thead>
                    <tbody >
                    <%
                        String listcondition="",complaincedate="";
                        int count=0;
                        for(Misrct material:materialList){
                            listcondition="";complaincedate="";
                            count++;
                            if(materialListData.size()>0){
                                listcondition=materialListData.stream().filter(t->material.getCodevalue().equals(t.getListID())).findFirst().get().getListCondition();
                                complaincedate=materialListData.stream().filter(t->material.getCodevalue().equals(t.getListID())).findFirst().get().getComplainceDate();

                            }
                    %>
                    <tr>
                        <td><%=count%></td>
                        <td><%=material.getCodedesc()%></td>
                        <td>
                            <input type="hidden"  class="materiallistID"   name="materiallistID" value="<%=material.getCodevalue()%>">
                            <input type="hidden" class="materiallistDesc"  name="materiallistDesc" value="<%=material.getCodedesc()%>">
                            <textarea class="form-control materiallistCondition" name="materiallistCondition" disabled maxlength="500"><%=listcondition%></textarea>
                        </td>
                        <td>
                            <input type="hidden"  class="materiallistID"   name="materiallistID" value="<%=material.getCodevalue()%>">
                            <input type="hidden" class="materiallistDesc"  name="materiallistDesc" value="<%=material.getCodedesc()%>">
                            <input type="date" class="form-control materiallistComDate"  name="materiallistComDate" disabled value="<%=complaincedate%>">
                        </td>
                    </tr>
                    <%

                        }
                    %>
                    </tbody>
                </table>
                <li class="nav-item">
                </li>
            </ul>
        </div>
    </div>
</div>

<script>

    // $('#materialDetailslink').click(function (e) {
    //     e.preventDefault();
    //     var applicantId = $('[data-code="A-1"]').find('[name="appid"]').val();
    //     if (applicantId) {
    //         $('#materialDetailsContent').collapse('show');
    //         // updateTotalAmount();
    //     } else {
    //         alertmsg("Kindly Save General Details of Applicant");
    //         setTimeout(() => {
    //             $('#materialDetailsContent').collapse('hide');
    //         }, 1000); // Defer the collapse to ensure it happens after any UI updates
    //
    //     }
    //     //
    // });









    $('.select').select2({
        templateResult: formatState,
        templateSelection: formatState
    });


</script>