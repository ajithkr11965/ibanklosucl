<%@ page import="com.sib.ibanklosucl.model.Misrct" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 10-05-2024
  Time: 11:28
  To change this template use File | Settings | File Templates.
--%>
<%
    Boolean checker = request.getAttribute("checker")!=null? request.getAttribute("checker").toString().equals("Y"):false;

%>
<script src="assets/js/vendor/forms/selects/select2.min.js"></script>
<script src="assets/demo/pages/form_select2.js"></script>
<script src="assets/js/custom/WI/loandetails.js"></script>

<%--<ul class="nav nav-sidebar  " id="loanDetails" data-nav-type="collapsible">--%>
<%--    <li class="nav-item nav-item-submenu" >--%>
<%--        <a href="#" class="nav-link vehicleTab" id="loanDetailslink">--%>
<%--            <i class="ph-circles-three-plus"></i>--%>
<%--            Loan Details--%>
<%--        </a>--%>
        <ul class="nav-group-sub p-3">

            <!-- Vehicle Amount -->
            <li class="nav-item">
                <div class="row">
                    <div class="col-lg-6">
                        <div class="mb-3 mt-3">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="fw-semibold">Vehicle Amount (in Rs/-)</div>
                                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                                        <input type="number"  id="vehicle-amount" name="vehicleAmount" class="form-control" readonly>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-6">
                        <div class="mb-3 mt-3">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="fw-semibold">Requested Loan Amount (in Rs/-)</div>
                                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                                        <input type="number" disabled id="loan-amount" name="loanAmount" class="form-control" placeholder="Enter loan amount">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </li>

            <!-- Tenor in Months -->
            <li class="nav-item">
                <div class="row">
                    <div class="col-lg-6">
                        <div class="mb-3 mt-3">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="fw-semibold">Tenor in Months</div>
                                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                                        <input type="number" disabled id="tenor"  name="tenor" class="form-control" placeholder="Enter tenor in months">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-3">
                        <div class="mb-3 mt-3">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="fw-semibold">ROI Type</div>
                                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                                        <div class="form-check">
                                            <input class="form-check-input" type="radio" name="roiType" id="roi-fixed"  value="fixed" checked>
                                            <label class="form-check-label" for="roi-fixed">Fixed</label>
                                        </div>
<%--                                        <div class="form-check">--%>
<%--                                            <input class="form-check-input" type="radio" name="roiType" id="roi-floating" value="floating">--%>
<%--                                            <label class="form-check-label" for="roi-floating">Floating</label>--%>
<%--                                        </div>--%>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-3">
                        <div class="mb-3 mt-3">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="fw-semibold">FOIR Type</div>
                                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                                        <div class="form-check">
                                            <input class="form-check-input" type="radio" name="foirType" id="foir-foir" value="foir" disabled>
                                            <label class="form-check-label" for="foir-foir">FOIR</label>
                                        </div>
                                        <div class="form-check">
                                            <input class="form-check-input" type="radio" name="foirType" id="foir-non-foir" value="non-foir" disabled>
                                            <label class="form-check-label" for="foir-non-foir">Non FOIR</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-lg-4">
                        <div class="mb-3 mt-3">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="fw-semibold">Loan Protection Insurance</div>
                                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                                        <div class="form-check mb-2">
                                            <input class="form-check-input"  type="radio" name="insVal" id="ins-Val-Y" value="Y" checked disabled >
                                            <label class="form-check-label">Yes</label>
                                        </div>
                                        <div class="form-check">
                                            <input class="form-check-input"  type="radio" name="insVal" id="ins-Val-N" value="N" disabled>
                                            <label class="form-check-label" >No</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-4 insVal  d-none">
                        <div class="mb-2 mt-2">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="fw-semibold">Insurance Partner</div>
                                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                                        <select class="form-control form-select insType" name="insType" id="insType" disabled>
                                            <option value="" >Please Select</option>
                                            <%
                                                List<Misrct> titles = (List<Misrct>) request.getAttribute("tppData");
                                                for (Misrct title : titles) {
                                                    out.println("<option value=\"" + title.getCodevalue()+ "\"  >" + title.getCodedesc() + "</option>");
                                                }
                                            %>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-4 insVal d-none">
                        <div class="mb-2 mt-2">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="fw-semibold">Insurance Premium Amount</div>
                                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                                        <input type="number" id="insAmt" class="form-control" value="" placeholder="Enter Premium amount" disabled readonly>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </li>


            <!-- Save Button -->
            <li class="nav-item">
                <div class="text-end">
                    <%if(!checker){%>
                    <input type="hidden" id="collapseDisable" value="1">
                    <button type="button" id="loanDetailsEdit" class="btn btn-yellow my-1 me-2" comon><i class="ph-note-pencil ms-2"></i>Edit</button>
                    <button type="button" id="loanDetailsSave" class="btn btn-primary comon">Save<i class="ph-paper-plane-tilt ms-2"></i></button>
                    <%}%>
                </div>
            </li>
        </ul>


<%--    </li>--%>
<%--</ul>--%>
<%--</form>--%>