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
<div class="kt">
<div class="border rounded px-7 py-3 mb-2">
        <div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="materialDetailslink" data-bs-target="#materialDetailsContent">
            <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2" for="materialDetailsContent">
                <i class="ki-duotone ki-enjin-coin  fs-3x me-4">
                    <span class="path1"></span>
                    <span class="path2"></span>
                    <span class="path3"></span>
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
                    //List<MaterialListData> materialListData= request.getAttribute("materialListData")==null? new ArrayList<>() :(List<MaterialListData>) request.getAttribute("materialListData");
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
                            <textarea class="form-control materiallistCondition" name="materiallistCondition" maxlength="300"><%=listcondition%></textarea>
                        </td>
                        <td>
                            <input type="hidden"  class="materiallistID"   name="materiallistID" value="<%=material.getCodevalue()%>">
                            <input type="hidden" class="materiallistDesc"  name="materiallistDesc" value="<%=material.getCodedesc()%>">
                            <input type="date" class="form-control materiallistComDate"  name="materiallistComDate" value="<%=complaincedate%>">
                        </td>
                    </tr>
                    <%

                        }
                    %>
                    </tbody>
                </table>
                <li class="nav-item">
                    <div class="text-end">
                        <%if(!checker){%>
                        <input type="hidden" id="collapseDisable" value="1">
                        <button type="button" id="materialDetailsEdit" class="btn btn-sm btn-warning my-1 me-2" comon><i class="ph-note-pencil ms-2"></i>Edit</button>
                        <button type="button" id="materialDetailsSave" class="btn btn-sm btn-primary comon">Save<i class="ph-paper-plane-tilt ms-2"></i></button>
                        <%}%>
                    </div>
                </li>
            </ul>
        </div>
</div>
</div>
<script>
    $('#materialDetailsSave').on('click', function () {
        const dataArray = [];
        let isValid = true;

        const slno=$('#slno').val();
        const winum=$('#winum').val();

        $('#materialListTable tbody tr').each(function () {
            const materiallistID = $(this).find('.materiallistID').val();
            const materiallistDesc = $(this).find('.materiallistDesc').val();
            const materiallistCondition = $(this).find('.materiallistCondition').val();
            const materiallistComDate = $(this).find('.materiallistComDate').val();

            if (!materiallistID || !materiallistDesc || !materiallistCondition ) {
                isValid = false;
                return false; // break out of .each loop
            }

            dataArray.push({
                listID: materiallistID,
                listDesc: materiallistDesc,
                listCondition: materiallistCondition,
                complainceDate: materiallistComDate,
                winum:winum,
                slno:slno
            });
        });

        if (!isValid) {
            alertmsg("All Material terms and Conditions fields must be filled in every row.");
            return;
        }

        var dataStr=[{
            key:"crtdata",
            value: JSON.stringify(dataArray)
        }]

        const requestData = {
            data: dataStr,
            id: "RBCM", //CRTCHK
            slno: slno,
            winum: winum,
            reqtype: "RBCM",//CRTCHK
            DOC_ARRAY: []
        };
        showLoader();
        $.ajax({
            url: 'api/save-data',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(requestData),
            success: function (response) {
                if(response==='S')
                    notyalt("Data saved successfully.");
                else{
                    alertmsg(response.msg);
                }
                hideLoader();
            },
            error: function () {
                hideLoader();
                alertmsg("Error while saving data.");
            }
        });
    });

    $('#materialDetailslink').click(function (e) {
        e.preventDefault();
        var applicantId = $('[data-code="A-1"]').find('[name="appid"]').val();
        if (applicantId) {
            $('#materialDetailsContent').collapse('show');
            // updateTotalAmount();
        } else {
            alertmsg("Kindly Save General Details of Applicant");
            setTimeout(() => {
                $('#materialDetailsContent').collapse('hide');
            }, 1000); // Defer the collapse to ensure it happens after any UI updates

        }
        //
    });


    function enableFields() {
        $('#materialDetails input, #materialDetails select').prop('disabled', false);
        $('#materialDetailsSave').prop('disabled', false);
        $('#materialDetailsEdit').prop('disabled', true);
    }


    $('#materialDetailsEdit').click(function () {
        enableFields();
        $('#bottomCard').data('state', 0).attr('state', 0);
        //  disableAccordian();
    });



    $('.select').select2({
        templateResult: formatState,
        templateSelection: formatState
    });


</script>