<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="com.sib.ibanklosucl.dto.mssf.MSSFCustomerDTO" %>
<%@ taglib prefix="los" uri="http://www.siblos.com/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en" data-bs-theme="light">
<head>
    <title>MSSF Queue</title>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link href="assets/icons/phosphor/styles.min.css" rel="stylesheet" type="text/css">
    <link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
    <link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
    <link href="assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css"/>
    <link href="assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
    <link href="assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
</head>
<%
    Employee employee = new Employee();
    if (request.getAttribute("employee") != null) {
        employee = (Employee) request.getAttribute("employee");
    }
%>
<body id="kt_body">
<jsp:include page="header.jsp"/>
		<los:loader/>

<div class="page-header">
    <div class="page-header-content d-lg-flex">
        <div class="kt">
            <div class="border border-gray-300 border-dashed rounded min-w-125px py-3 px-4 me-0 ms-3 mb-3 mt-1">
                <div class="d-flex align-items-center mt-2 mb-2">
                    <div class="d-flex flex-center w-25px h-25px rounded-3 bg-light-primary bg-opacity-90 me-3">
                        <i class="ki-duotone ki-abstract-26 text-primary fs-3x">
                            <span class="path1"></span>
                            <span class="path2"></span>
                        </i>
                    </div>
                    <div class="fs-8 fw-bold counted ms-2">MSSF Applications Queue</div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="page-content pt-0">
    <%request.setAttribute("TAB","MSSF");%>
    <jsp:include page="sidebar.jsp"/>

    <div class="content-wrapper">
        <div class="content">
            <div class="row">
                <div class="kt" id="kt_wrapper">
                    <div class="content d-flex flex-column flex-column-fluid" id="kt_content">
                        <div class="col-xl-12">
                            <div class="card card-xl-stretch mb-5 mb-xl-8">
                                <div class="card-header border-0 pt-5">
                                    <div class="card-title">
                                        <div class="d-flex align-items-center position-relative my-1">
                                            <i class="ki-duotone ki-magnifier fs-3 position-absolute ms-4">
                                                <span class="path1"></span>
                                                <span class="path2"></span>
                                            </i>
                                            <input type="text" data-kt-filter="search" class="form-control form-control-solid w-250px ps-12"
                                                   placeholder="Search Application"/>
                                        </div>
                                    </div>
                                </div>

                                <div class="card-body py-3">
                                    <div class="table-responsive">
                                        <form id="mssfForm" method="post" action="mssfdetail">
                                            <input type="hidden" name="refNo" id="refNoInput">
                                            <table class="table align-middle table-row-dashed fs-8 gy-3" id="mssfTable">
                                                <thead>
                                                    <tr class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">
                                                        <th class="min-w-10px"></th>
                                                        <th>Reference No</th>
                                                        <th>Customer Name</th>
                                                        <th>Mobile</th>
                                                        <th>Email</th>
                                                        <th>Dealer Code</th>
                                                        <th>Loan Amount</th>
                                                        <th>Created Date</th>
                                                    </tr>
                                                </thead>
                                                <tbody class="fw-semibold text-gray-600">
                                                    <%
                                                        String userId = employee.getPpcno();
                                                        List<MSSFCustomerDTO> mssfQueue = (List<MSSFCustomerDTO>) request.getAttribute("mssfQueue");
                                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm a");

                                                        for (MSSFCustomerDTO item : mssfQueue) {
                                                            String lockDetails = "";
                                                            if (item.getLockDetails() != null &&
                                                                !userId.equals(item.getLockDetails().getLockedBy()) &&
                                                                "Y".equals(item.getLockDetails().getLockFlag())) {
                                                                lockDetails = "<i class='ki-duotone ki-lock-3'>" +
                                                                        "<span class='path1'></span>" +
                                                                        "<span class='path2'></span>" +
                                                                        "<span class='path3'></span>" +
                                                                        "</i> Locked By - " + item.getLockDetails().getLockedBy();
                                                            }
                                                    %>
                                                    <tr>
                                                        <td>
                                                            <button type="button"
                                                                    class="btn btn-icon btn-bg-light btn-active-color-primary btn-sm me-1 modify-btn pulse pulse-primary"
                                                                    data-ref-no="<%=item.getRefNo()%>"
                                                                    <%=item.getLockDetails() != null && !userId.equals(item.getLockDetails().getLockedBy()) ? "disabled" : ""%>>
                                                                <i class="ki-duotone ki-pencil fs-3">
                                                                    <span class="path1"></span>
                                                                    <span class="path2"></span>
                                                                </i>
                                                            </button>
                                                        </td>
                                                        <td>
                                                            <div class="col">
                                                                <div class="d-flex align-items-center me-2">
                                                                    <div>
                                                                        <div class="fs-7 fw-bold me-2"><%=item.getRefNo()%></div>
                                                                    </div>
                                                                    <%if (item.getLockDetails() != null && !userId.equals(item.getLockDetails().getLockedBy()) && "Y".equals(item.getLockDetails().getLockFlag())) {%>
                                                                        <i class="ki-duotone ki-lock fs-7 text-warning" data-bs-toggle="tooltip"
                                                                           data-bs-html="true" title="<%=lockDetails%>">
                                                                            <span class="path1"></span>
                                                                            <span class="path2"></span>
                                                                            <span class="path3"></span>
                                                                        </i>
                                                                    <%}%>
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="d-flex align-items-center">
                                                                <div class="symbol symbol-circle symbol-50px overflow-hidden me-3">
                                                                    <div class="symbol-label fs-3 bg-light-success text-success">
                                                                        <%=item.getCustomerName().substring(0, 1)%>
                                                                    </div>
                                                                </div>
                                                                <div class="ms-5">
                                                                    <div class="text-gray-800 text-hover-primary fs-8 fw-bold">
                                                                        <%=item.getCustomerName()%>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td><%=item.getMobile()%></td>
                                                        <td><%=item.getEmail()%></td>
                                                        <td><%=item.getDealerCode()%></td>
                                                        <td><%=String.format("%.2f", item.getLoanAmount())%></td>

                                                        <td><%=item.getCreatedDate()%></td>
                                                    </tr>
                                                    <%}%>
                                                </tbody>
                                            </table>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>

<!-- Scripts -->
<script src="assets/js/jquery/jquery.min.js"></script>
<script src="assets/js/bootstrap/bootstrap.bundle.min.js"></script>
<script src="assets/plugins/custom/datatables/datatables.bundle.js"></script>
<script>
$(document).ready(function() {
    // Initialize DataTable
    var table = $('#mssfTable').DataTable({
        pageLength: 150,
        order: [[7, 'desc']], // Sort by created date
        searching: true,
        lengthMenu: [10, 25, 50, 100]
    });

    // Handle search
    $('[data-kt-filter="search"]').on('keyup', function() {
        table.search(this.value).draw();
    });

    // Handle modify button click
    $('.modify-btn').click(function() {
        showLoader();
        var refNo = $(this).data('ref-no');
        $('#refNoInput').val(refNo);
        $('#mssfForm').submit();
    });

    // Initialize tooltips
    $('[data-bs-toggle="tooltip"]').tooltip();
});
</script>
</body>
</html>
