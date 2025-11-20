<%--
  Created by IntelliJ IDEA.
  User: SIBL18202
  Date: 23-08-2024
  Time: 11:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="card message">
    <div class="card-header">
        <h6 class="mb-0">Message
            <a href="#" class="text-body rightmost d-inline-flex align-items-center" data-bs-toggle="dropdown" aria-expanded="false">
                <i class="ph-gear"></i>
            </a>
            <div class="dropdown-menu dropdown-menu-end">
                <a href="#" onclick="return addmessage();" class="dropdown-item">
                    <i class="ph-calendar-plus me-2"></i> Add Message
                </a>
                <a href="#" onclick="return hidemessage();" class="dropdown-item">
                    <i class="ph-trash me-2"></i> Hide Message
                </a>
            </div>
        </h6>
    </div>

    <div class="card-body messagescroll">
        <div class="marquee1" style="animation: 30s linear infinite marquee;">
        <marquee direction="up" class="media-chat-scrollable mb-3" style="max-height:initial !important;">
            <div class="media-chat vstack gap-3">

                <c:forEach var="message" items="${messages}">
                    <div class="${message.clr}">
                        <a href="#" class="d-block">
                            <c:choose>
                                <c:when test="${message.cmuser != null && message.imgExists}">
                                    <img src="https://infobank.sib.co.in:8443/UPLOAD_FILES/IMAGE/{message.cmuser}.xjpg" class="w-40px h-40px rounded-pill" alt="">
                                </c:when>
                                <c:otherwise>
                                    <span style="background: #66bb6a; background-image: url(assets/Mis/images/panel_bg.png); background-size: contain;" class="w-40px h-40px rounded-pill text-white letter-icon">${message.cmuser.substring(0,1)}</span>
                                </c:otherwise>
                            </c:choose>
                        </a>
                        <div>
                            <div class="media-chat-message">${message.msg}</div>
                        </div>
                    </div>
                </c:forEach>

                <c:forEach var="finacleMessage" items="${finacleMessages}">
                    <div class="alert pt-1 pb-1 fade show">
                        <a href="#" class="d-block">
                            <img src="assets/Mis/images/finaclelogo.png" class="w-40px h-40px rounded-pill" alt="">
                            <span class="status-indicator bg-success"></span>
                        </a>
                        <div>
                            <div class="media-chat-message">
                                <span class="badge bg-indigo">Finacle</span>
                                <span class="badge bg-warning">${finacleMessage.clr}</span><br />
                                    ${finacleMessage.msg}
                            </div>
                        </div>
                    </div>
                </c:forEach>

            </div>
        </marquee>
        </div>
    </div>
</div>
