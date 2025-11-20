<%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 10-05-2024
  Time: 11:26
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="java.util.List" %>
<%@ page import="com.sib.ibanklosucl.model.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.math.BigDecimal" %>

<%

  String apptype= request.getAttribute("apptype").toString();
  String crchk="";
  boolean completed=false,mod=false;
  VehicleLoanApplicant applicant = null;
  VLCredit credit= (VLCredit) request.getAttribute("credit");
  List<VLFinliab> vlFinliabList = new ArrayList<>();
  List<VLFinasset> VLFinastlist = new ArrayList<>();

String score_="",rotate="rotate(-90) ,scale(0.9999999832349832,0.9999999832349832)";
  if (request.getAttribute("general") != null) {
    applicant = (VehicleLoanApplicant) request.getAttribute("general");
  } else {
  }

  String expTenure="",time="";
  String expLoanAmt="";


  if(applicant!=null) {
    credit= applicant.getVlcredit();
    if (credit != null) {
      vlFinliabList = credit.getVlLiabList().stream()
							.filter(lb -> "N".equals(lb.getDelFlg()))
							.toList();
      VLFinastlist = credit.getVlAstList().stream()
							.filter(ast -> "N".equals(ast.getDelFlg()))
							.toList();
      score_=credit.getBureauScore()==null?"": String.valueOf(credit.getBureauScore());
      time=credit.getExpFetchDate()!=null?credit.getExpFetchDate().toString():"";
      completed = true;
      crchk = "done";
      Double maxScore = 999.00;
      Double maxRotation = 180.00; // degrees for a semicircle
      Double score= Double.valueOf(credit.getBureauScore());
      if(score<=560){
        maxRotation=36.00;
        maxScore=560.00;
      }
      else if(score<=720){
        maxRotation=72.00;
        maxScore=720.00;
      }
      else if(score<=880){
        maxRotation=108.00;
        maxScore=880.00;
      }
      else if(score<=960){
        maxRotation=144.00;
        maxScore=960.00;
      }
      else{
        maxRotation=180.00;
        maxScore=999.00;
      }
      Double rotationAngle = (score / maxScore) * maxRotation;
      // Apply the rotation transform
      //  targetElement.css('transform', 'rotate(' + rotationAngle + 'deg) scale(0.9999999832349832, 0.9999999832349832)');
      rotationAngle=rotationAngle-90.00;
      rotate="rotate("+rotationAngle+") ,scale(0.9999999832349832,0.9999999832349832)";

    }
  }
 // out.println(credit);
 // out.println(vlFinliabList);
 // out.println(VLFinastlist);




%>
<form   class="det form-details creditCheck" data-code="<%=apptype%>-6"  data-completed="<%=completed%>"  action="#">
  <div class="kt d-flex justify-content-end" style="height: 0em;">
    <button class="edit-button btn btn-icon  btn-bg-light btn-color-info btn-sm me-1">
      <i class="ki-duotone  ki-pencil fs-2"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
    </button>
  </div>
  <div class="row ">
    <div class="row  mt-3">
      <div class="col-lg-6 d-flex justify-content-center">
        <div class="svg-center" id="segmented_gauge"><svg width="200" height="120"><g class="d3-slice-border" transform="translate(100,100)"><path d="M-85,-1.0409497792752502e-14A85,85 0 0,1 -68.76644452187053,-49.961746444860225L-52.58610463437158,-38.20604139901076A65,65 0 0,0 -65,-7.960204194457796e-15Z" fill="#ef5350"></path><path d="M-68.76644452187053,-49.961746444860225A85,85 0 0,1 -26.266444521870525,-80.83980388508806L-20.086104634371576,-61.81867355918499A65,65 0 0,0 -52.58610463437158,-38.20604139901076Z" fill="#e39f54 "></path><path fill="#d3d759" d="M-26.266444521870525,-80.83980388508806A85,85 0 0,1 26.266444521870554,-80.83980388508805L20.086104634371598,-61.81867355918498A65,65 0 0,0 -20.086104634371576,-61.81867355918499Z"></path><path d="M26.266444521870554,-80.83980388508805A85,85 0 0,1 68.76644452187054,-49.96174644486022L52.58610463437159,-38.20604139901075A65,65 0 0,0 20.086104634371598,-61.81867355918498Z" fill="#90c95f"></path><path d="M68.76644452187054,-49.96174644486022A85,85 0 0,1 85,0L65,0A65,65 0 0,0 52.58610463437159,-38.20604139901075Z" fill="#66bb6a"></path></g><g transform="translate(100,100)"><text class="d3-text opacity-50" transform="rotate(-90) translate(0,-90)" style="text-anchor: middle;">0</text><text class="d3-text opacity-50" transform="rotate(-54) translate(0,-90)" style="text-anchor: middle;">560</text><text class="d3-text opacity-50" transform="rotate(-18) translate(0,-90)" style="text-anchor: middle;">720</text><text class="d3-text opacity-50" transform="rotate(18) translate(0,-90)" style="text-anchor: middle;">880</text><text class="d3-text opacity-50" transform="rotate(54) translate(0,-90)" style="text-anchor: middle;">960</text><text class="d3-text opacity-50" transform="rotate(90) translate(0,-90)" style="text-anchor: middle;">999</text></g><g transform="translate(100,100)"><path d="M5,0C4.9963126843657815,-0.05530973451327434,1.6666666666666667,-75,0,-75S-5,0,-5,0S-1.6666666666666667,5,0,5S4.583333333333333,0.4166666666666667,5,0" fill="var(--body-color)" transform="<%=rotate%>" name="crchk-score"></path></g></svg></div>
      </div>

      <div class="col-lg-6 d-flex justify-content-center" style="align-items: center;">
        <div class="row">


          <div class="col-lg-4">
<%--            <div class="mb-2">--%>
<%--              <button type="button" style="/*! height: fit-content; *//*! padding: 1em; */margin-top: 2em;" class="btn-file btn btn-flat-danger border-transparent crchk-runexp">Run Experian</button>--%>
<%--            </div>--%>
          </div>
        </div>


      </div>
    </div>
  </div>

  <div class="row mt-1 d-flex justify-content-center crchk-experionscore" style="<%= "done".equals(crchk) ? "" : "display:none !important" %>" name="crchk-experionscore">
    <div class="col-lg-4">
      <div class="text-center badge bg-light text-body experian_fetchTime  p-2 mb-1 w-100 "><%=time%></div>
      <div class="alert alert-primary d-flex justify-content-center">
        Experion Score is : <a href="#" class="alert-link experian_score" ><%=score_%></a>

      </div>
    </div>
  </div>



  <div class="row d-flex justify-content-center crchk-inc" style="<%= "done".equals(crchk) ? "" : "display:none !important" %>" name="crchk-inc-lia">
    <div class="card-header">
      <h6>Liabilities</h6>
    </div>
    <div class="row mb-6">
      <div class="col-lg-6">
        <label class="form-label">Liability as per payslip</label>
        <div class="form-control-feedback form-control-feedback-start">
          <input type="number" class="form-control" name="liabilityAsPerPayslip" id="liabilityAsPerPayslip"
                 value="<%=credit != null && credit.getPayslipLiablity() != null ? credit.getPayslipLiablity() : ""%>">
        </div>
      </div>
    </div>
    <div id="emptable-sal-div-id" class="table-responsive border-white border-opacity-15  crchk-inc-lia-div">

      <table class="table table-bordered crchk-inc-lia-table"  >
        <thead>
        <tr>
          <th>Bank Name</th>
          <th>Nature of Limit</th>
          <th>Limit</th>
          <th>Outstanding</th>
          <th>EMI</th>
          <th>Modified EMI</th>
        </tr>
        </thead>
        <tbody class="crchk-inc-lia-table-body" name="crchk-inc-lia-table-body">

          <%if(vlFinliabList.isEmpty()) {%>
          <tr>
          <td><input type="text" name="liabankname" id="liabankname" class="form-control" placeholder="Bank Name"></td>
          <td><input type="text" name="lianaturelimit" id="lianaturelimit" class="form-control"  placeholder="Enter Nature of Limit"></td>
          <td><input type="text" name="lialimit" id="lialimit" class="form-control"  placeholder="Enter Limit"></td>
          <td><input type="text" name="liaoutstanding" id="liaoutstanding" class="form-control"  placeholder="Enter Outstanding"></td>
          <td><input type="text" name="liaemi" id="liaemi" class="form-control"  placeholder="Enter EMI"></td>
            <td><input type="text" name="liamodifiedemi" id="liamodifiedemi" class="form-control" placeholder="Enter Modified EMI"></td>
<%--          <td>--%>
<%--              <button type="button" onclick="deleteRowCrchk(this,'LIA')" class="btn   btn-file  btn-flat-danger border-transparent btn-sm emptable-addbtn">--%>
<%--              <i class="ph-trash"></i>--%>
<%--            </button>--%>
<%--          </td>--%>
          </tr>
          <%
          }
          else {
            for( VLFinliab vlliab : vlFinliabList) {
              if(vlliab.getDelFlg().equals("N"))
              {
              %>
          <tr>
              <td><input type="text" name="liabankname" id="liabankname" class="form-control" placeholder="Bank Name" value="<%=vlliab.getBankName()%>" readonly></td>
              <td><input type="text" name="lianaturelimit" id="lianaturelimit" class="form-control"  placeholder="Enter Nature of Limit" value="<%=vlliab.getNatureLim()%>" readonly></td>
              <td><input type="text" name="lialimit" id="lialimit" class="form-control"  placeholder="Enter Limit" value="<%=vlliab.getLimit()%>" readonly></td>
              <td><input type="text" name="liaoutstanding" id="liaoutstanding" class="form-control"  placeholder="Enter Outstanding" value="<%=vlliab.getOutStanding()%>" readonly></td>
              <td><input type="text" name="liaemi" id="liaemi" class="form-control"  placeholder="Enter EMI" value="<%=vlliab.getEmi()%>" readonly></td>
              <td><input type="number" onchange="validateDecimal(this)" name="liamodifiedemi" id="liamodifiedemi" class="form-control" placeholder="Enter Modified EMI" value="<%=vlliab.getModifiedEmi()%>"></td>

<%--              <td>--%>
<%--                <button type="button" onclick="deleteRowCrchk(this,'LIA')" class="btn   btn-file btn-flat-danger border-transparent btn-sm emptable-addbtn">--%>
<%--                  <i class="ph-trash"></i>--%>
<%--                </button>--%>
<%--              </td>--%>

          </tr>
              <%
            }}}
          %>

        </tbody>
      </table>
      <div  class="card-footer ">
        <div class="row">
          <div class="col-lg-10">

          </div>
          <div class="col-lg-2" style="justify-content: end;display: flex;">
            <button type="button" onclick="addRowCrchk(this,'LIA')" class="btn    btn-file btn-flat-primary btn-sm emptable-addbtn" name="emptable-addbtn">
              <i class="ph-plus-circle  me-2"></i>
              Add
            </button>
          </div>
        </div>
      </div>

    </div>
  </div>


  <div class="row d-flex justify-content-center crchk-inc" style="<%= "done".equals(crchk) ? "" : "display:none !important" %>" name="crchk-inc-lia">

    <div class="card-header">
      <h6>Asset</h6>
    </div>
    <div id="emptable-sal-div-id" class="table-responsive border-white border-opacity-15  crchk-inc-ast-div">
      <table class="table table-bordered crchk-inc-ast-table"  >
        <thead>
        <tr>
          <th>Type of Asset</th>
          <th>Value</th>
          <th></th>
        </tr>
        </thead>


        <tbody class="crchk-inc-ast-table-body" name="crchk-inc-ast-table-body">


          <%if(VLFinastlist.isEmpty()) {%>
          <tr>
          <td><input type="text" name="asttypeasset" id="asttypeasset" class="form-control" placeholder="Enter type of Asset"></td>
          <td><input type="text" name="astvalue" id="astvalue" class="form-control"  placeholder="Enter Value"></td>
          <td>
            <button type="button" onclick="deleteRowCrchk(this,'AST')" class="btn  btn-file btn-flat-danger border-transparent btn-sm emptable-addbtn">
              <i class="ph-trash"></i>
            </button>
          </td>
          </tr>
          <% }
          else {
            for( VLFinasset vlast : VLFinastlist) {
              if(vlast.getDelFlg().equals("N"))
              {
          %>
          <tr>
          <td><input type="text" name="asttypeasset" id="asttypeasset" class="form-control" placeholder="Enter type of Asset" value="<%=vlast.getAssetType()%>"></td>
          <td><input type="text" name="astvalue" id="astvalue" class="form-control"  placeholder="Enter Value" value="<%=vlast.getAssetVal()%>"></td>
          <td>
            <button type="button" onclick="deleteRowCrchk(this,'AST')" class="btn  btn-file btn-flat-danger border-transparent btn-sm emptable-addbtn">
              <i class="ph-trash"></i>
            </button>
          </td>
          </tr>
          <% }}} %>



        </tbody>
      </table>
      <div  class="card-footer ">
        <div class="row">
          <div class="col-lg-10">

          </div>
          <div class="col-lg-2" style="justify-content: end;display: flex;">
            <button type="button" onclick="addRowCrchk(this,'AST')" class="btn  btn-file btn-flat-primary btn-sm emptable-addbtn" name="emptable-addbtn">
              <i class="ph-plus-circle  me-2"></i>
              Add
            </button>
          </div>
        </div>
      </div>

    </div>
  </div>

  <div class="text-end mt-1">
<%--    <button class="btn btn-yellow my-1 me-2 edit-button"><i class="ph-note-pencil  ms-2"></i>Edit</button>--%>
    <button type="submit" class="btn btn-primary save-button">Save <i class="ph-paper-plane-tilt ms-2"></i></button>
  </div>
</form>