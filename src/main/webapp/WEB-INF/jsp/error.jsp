<!DOCTYPE html>
<html lang="en">
<!--begin::Head-->
<head>
    <title>SIB</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <meta property="og:locale" content="en_US" />
    <meta property="og:type" content="article" />
    <meta property="og:title" content="SIB" />
    <meta property="og:site_name" content="SIB" />
    <!--begin::Global Stylesheets Bundle(mandatory for all pages)-->
    <link href="assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
    <link href="assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
    <!--end::Global Stylesheets Bundle-->
</head>
<!--end::Head-->
<!--begin::Body-->
<body id="kt_body" class="body-img" style="background-image: url(assets/media/misc/5img.jpeg);background-size: cover;">
<!--begin::Root-->
<div class="d-flex flex-column flex-root" id="kt_app_root">
    <!--begin::Authentication - Sign-up -->
    <div class="d-flex flex-column flex-lg-row flex-column-fluid">

        <!--begin::Aside-->


        <!--begin::Body-->
        <div id="#kt_app_body_content" style=" font-family:Arial,Helvetica,sans-serif; line-height: 1.5; min-height: 100%; font-weight: normal; font-size: 15px; color: #2F3044; margin:0; padding:0; width:100%;justify-content: center;display: flex;align-items: center;">
            <div style="background-color:#ffffff; padding: 45px 0 34px 0; border-radius: 24px; margin:40px auto; max-width: 600px;min-width: 600px;">
                <table align="center" border="0" cellpadding="0" cellspacing="0" width="100%" height="auto" style="border-collapse:collapse">
                    <tbody>
                    <tr>
                        <td align="center" valign="center" style="text-align:center; padding-bottom: 10px">
                            <!--begin:Email content-->
                            <div style="text-align:center; margin:0 15px 34px 15px">
                                <!--begin:Logo-->

                                <!--end:Logo-->
                                <!--begin:Media-->
                                <div style="margin-bottom: 15px">
                                    <img alt="Logo" src="assets/media/misc/carrepair.gif" style="height: 280px;border-radius: 30px;">
                                </div>
                                <!--end:Media-->
                                <!--begin:Text-->
                                <div style="font-size: 14px; font-weight: 500; margin-bottom: 27px; font-family:Arial,Helvetica,sans-serif;">
                                    <p style="margin-bottom:9px; color:#181C32; font-size: 22px; font-weight:700">Oops, We hit a speed bump</p>



                                </div>
                                <!--end:Text-->
                                <!--begin:Action-->






                                <!--begin:Action-->
                            </div>
                            <!--end:Email content-->
                        </td>
                    </tr>
                    <tr style="display: flex; justify-content: center; margin:0 60px 35px 60px">
                        <td align="start" valign="start" style="padding-bottom: 10px;">

                            <!--begin::Wrapper-->
                            <div style="background: #F9F9F9 !important; border-radius: 12px; padding:15px 15px" class="bg-light-info ">
                                <!--begin::Item-->
                                <div style="display:flex">
                                    <!--begin::Media-->

                                    <!--end::Media-->
                                    <!--begin::Block-->
                                    <div>
                                        <!--begin::Content-->
                                        <div class="text-center">

                                            <!--begin::Title-->

                                            <!--end::Title-->
                                            <!--begin::Desc-->
                                            <p style="color:#5E6278 !important; font-size: 13px; font-weight: 500; padding-top:3px; margin:0;font-family:Arial,Helvetica,sans-serif">
                                                <%
                                                String error="Your request couldn't be processed by the server. Try refreshing the page or contact support";
                                                if(request.getAttribute("error")!=null && !request.getAttribute("error").toString().isBlank()){
                                                    error=request.getAttribute("error").toString();
                                                }
                                                %>
                                                <%= error %>
                                                </p>
                                            <!--end::Desc-->
                                        </div>
                                        <!--end::Content-->
                                        <!--begin::Separator-->

                                        <!--end::Separator-->
                                    </div>
                                    <!--end::Block-->
                                </div>
                                <!--end::Item-->
                                <!--begin::Item-->

                                <!--end::Item-->
                                <!--begin::Item-->

                                <!--end::Item-->
                            </div>
                            <!--end::Wrapper-->
                        </td>
                    </tr>
                    <tr>
                        <td align="center" valign="center" style="font-size: 13px; text-align:center; padding: 0 10px 10px 10px; font-weight: 500; color: #A1A5B7; font-family:Arial,Helvetica,sans-serif">


                            <p style="margin-bottom:4px">  <a href="dashboard" class="btn">Back to Home</a>.</p>
                            <p style="margin-bottom:4px">You may reach us at
                                <a  rel="noopener" href="mailto:swd@sib.co.in" target="_blank" style="font-weight: 600;color: #42b1f4;">swd@sib.co.in</a>.</p>

                        </td>
                    </tr>
                    <tr>

                    </tr>
                    <tr>
                        <td align="center" valign="center" style="font-size: 13px; padding:0 15px; text-align:center; font-weight: 500; color: #A1A5B7;font-family:Arial,Helvetica,sans-serif">
                            <p> Copyright South Indian Bank.
                            </p>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
        <!--end::Body-->

    </div>
    <!--end::Authentication - Sign-up-->
</div>
<script>
</script>

</body>
<!--end::Body-->
</html>