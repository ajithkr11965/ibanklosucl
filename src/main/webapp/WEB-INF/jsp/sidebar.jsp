<%@ page import="com.sib.ibanklosucl.dto.MenuList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Comparator" %>
<%@ page import="java.util.stream.Collectors" %><%--
  Created by IntelliJ IDEA.
  User: SIBL17977
  Date: 6/5/2024
  Time: 5:41 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
    String path=request.getAttribute("path")==null?"": request.getAttribute("path").toString();
%>
<!-- Core JS files -->
<script src="<%=path%>assets/demo/demo_configurator.js"></script>
<script src="<%=path%>assets/js/bootstrap/bootstrap.bundle.min.js"></script>
<!-- /core JS files -->
<!-- Theme JS files -->
<script src="<%=path%>assets/js/vendor/visualization/d3/d3.min.js"></script>
<script src="<%=path%>assets/js/vendor/visualization/d3/d3_tooltip.js"></script>
<script src="<%=path%>assets/js/jquery/jquery.min.js"></script>

<script src="<%=path%>assets/js/app.js"></script>
<script src="<%=path%>assets/demo/pages/dashboard.js"></script>
<script src="<%=path%>assets/js/custom/authentication/common_secure.js"></script>
<!-- /theme JS files -->
<style>
.sidebar-style
{
    border-radius: 1rem !important;
    background-color: #F5F6FA !important;
    box-shadow : 0 0 0 0 !important;
}
body{
    background-color: #fff !important;
}
.card{

    box-shadow: rgba(0, 0, 0, 0.05) 0px 6px 24px 0px, rgba(17, 17, 17, 0.05) 0px 0px 0px 1px !important;

}
</style>
<script>
    function openRemarksPage() {
        const slno = prompt("Enter SLNO:");
        console.log(slno +"slno");
        if (slno) {
            window.open('/ibanklos/remarks?slno=219', '_blank');
        }
    }
</script>
<%
        List<MenuList> menuList=new ArrayList<>();

        if (request.getAttribute("menuList") != null) {
              menuList = (List<MenuList>) request.getAttribute("menuList");
        }%>
<head>
    <%
        String tab=request.getAttribute("TAB")==null?"": request.getAttribute("TAB").toString();
    %>
    <div id="dashsidebar" class="sidebar sidebar-main sidebar-expand-lg align-self-start sidebar-style">

        <!-- Sidebar content -->
        <div class="sidebar-content">

            <!-- Sidebar header -->
            <div class="sidebar-section">
                <div class="sidebar-section-body d-flex justify-content-center">
                    <h5 class="sidebar-resize-hide flex-grow-1 my-auto">Navigation</h5>

                    <div>
                        <button type="button"
                                class="btn btn-light btn-icon btn-sm rounded-pill border-transparent sidebar-control sidebar-main-resize d-none d-lg-inline-flex">
                            <span class="ph-arrows-left-right"></span>
                        </button>

                        <button type="button" class="btn btn-light btn-icon btn-sm rounded-pill border-transparent sidebar-mobile-main-toggle d-lg-none">
                            <span class="ph-x"></span>
                        </button>
                    </div>
                </div>
            </div>
            <!-- /sidebar header -->


            <!-- Main navigation -->
            <div class="sidebar-section">
                <ul class="nav nav-sidebar pb-4" data-nav-type="accordion">

                    <!-- Main -->
                    <li class="nav-item-header pt-0">
                        <div class="text-uppercase fs-sm lh-sm opacity-50 sidebar-resize-hide">Main</div>
                        <span class="ph-dots-three sidebar-resize-show"></span>
                    </li>
                    <li class="nav-item">
                        <a href="<%=path%>dashboard" class="nav-link <%="".equals(tab)?"active":""%> ">
                            <i class="ph-house"></i>
                            <span>
									Dashboard
							</span>
                        </a>
                    </li>
                    <%
                        List<MenuList> sortmenuList= menuList.stream().sorted(Comparator.comparingLong(MenuList::getOrderid)).toList();

                        for(MenuList mm: sortmenuList)  {
                            if(!mm.getMenuID().equalsIgnoreCase("RPT") && mm.getMenuID().startsWith("RPT") )
                                continue;
                        %>
                        <li class="nav-item">
                            <a href="<%=path+mm.getMenuUrl()%>" class="nav-link  <%=mm.getMenuID().equals(tab)?"active":""%>">
                                <i class="<%=mm.getIcon()%>"></i>
                                <span><%=mm.getMenuDesc()%></span>
                            </a>
                        </li>
                        <%
                            }
                    %>



<%--                    <li class="nav-item">--%>
<%--                        <a href="bclist" class="nav-link  <%="BC".equals(tab)?"active":""%>">--%>
<%--                            <img class="rounded-circle" alt="" width="25" height="25" src="assets/images/dashboard1.png">--%>
<%--                            <span class="ms-2 fw-semibold">Checker Queue</span>--%>
<%--                        </a>--%>

<%--                    </li>--%>
<%--                    <li class="nav-item">--%>
<%--                        <a href="crtlist" class="nav-link  <%="CRTC".equals(tab)?"active":""%> ">--%>
<%--                            <i class="ph-check"></i>--%>
<%--                            <span>CRT Checker</span>--%>
<%--                        </a>--%>

<%--                    </li>--%>
<%--                    <li class="nav-item">--%>
<%--                        <a href="#" class="nav-link  <%="DQ".equals(tab)?"active":""%> ">--%>
<%--                            <i class="ph-swatches"></i>--%>
<%--                            <span>Document Queue</span>--%>
<%--                        </a>--%>

<%--                    </li>--%>

<%--                    <li class="nav-item">--%>
<%--                        <a href="cpcmakerlist" class="nav-link  <%="RBCM".equals(tab)?"active":""%> ">--%>
<%--                            <i class="ph-list-numbers"></i>--%>
<%--                            <span>RBCPC Maker</span>--%>
<%--                        </a>--%>
<%--                    </li>--%>

<%--                    <li class="nav-item">--%>
<%--                        <a href="rbcpcchecker" class="nav-link  <%="RBCC".equals(tab)?"active":""%> ">--%>
<%--                            <i class="ph-flag-checkered"></i>--%>
<%--                            <span>RBCPC Checker</span>--%>
<%--                        </a>--%>
<%--                    </li>--%>


<%--                    <li class="nav-item">--%>
<%--                        <a href="allotment" class="nav-link  <%="ALT".equals(tab)?"active":""%> ">--%>
<%--                            <i class="ph-list-numbers"></i>--%>
<%--                            <span>Allotment</span>--%>
<%--                        </a>--%>
<%--                    </li>--%>
<%--                    <li class="nav-item">--%>
<%--                        <a href="bslist" class="nav-link  <%="BS".equals(tab)?"active":""%> ">--%>
<%--                            <i class="ph-arrow-clockwise"></i>--%>
<%--                            <span>Branch Sendback</span>--%>
<%--                        </a>--%>
<%--                    </li>--%>
<%--                    <li class="nav-item">--%>
<%--                        <a href="calist" class="nav-link  <%="CA".equals(tab)?"active":""%> ">--%>
<%--                            <i class="ph-magnifying-glass"></i>--%>
<%--                            <span>CRT Amber</span>--%>
<%--                        </a>--%>
<%--                    </li>--%>


<%--                    <li class="nav-item">--%>
<%--                        <a href="#" onclick="openRemarksPage()" class="nav-link  <%="BS".equals(tab)?"active":""%> ">Open Remarks</a>--%>
<%--                    </li>--%>



                    <!-- Layout -->

<%--                    <li class="nav-item-header">--%>
<%--                        <div class="text-uppercase fs-sm lh-sm opacity-50 sidebar-resize-hide">Layout</div>--%>
<%--                        <i class="ph-dots-three sidebar-resize-show"></i>--%>
<%--                    </li>--%>
<%--                    <li class="nav-item">--%>
<%--                        <a href="recallwi" class="nav-link" <%="RECALLWI".equals(tab)?"active":""%>>--%>
<%--                            <i class="ph-layout"></i>--%>
<%--                            <span>Recall WI</span>--%>
<%--                        </a>--%>


<%--                    </li>--%>

                    <!-- /layout -->
                </ul>
            </div>
            <!-- /main navigation -->
        </div>
        <!-- /sidebar content -->
    </div>
</head>
<body>

</body>
</html>
