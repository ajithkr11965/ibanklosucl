"use strict";
var KTSignupGeneral = function() {
    var e, t, a, r, s = function() {
        return 100 === r.getScore()
    };
    return {
        init: function() {
            e = document.querySelector("#kt_sign_up_form"), t = document.querySelector("#kt_sign_up_submit"), a = FormValidation.formValidation(e, {
                fields: {
                    "userName": {
                        validators: {
                            notEmpty: {
                                message: "User Name is required"
                            }
                        }
                    },
                    "password": {
                        validators: {
                            notEmpty: {
                                message: "Password is required"
                            },
							regexp:{ 
								regexp:/^.{8,}$/,
								message: "Password must be at least 8 digit long"
							}
                        }
                    },
                    toc: {
                        validators: {
                            notEmpty: {
                                message: "You must accept the terms and conditions"
                            }
                        }
                    }
                },
                plugins: {
                    trigger: new FormValidation.plugins.Trigger,
                    bootstrap: new FormValidation.plugins.Bootstrap5({
                        rowSelector: ".fv-row",
                        eleInvalidClass: "",
                        eleValidClass: ""
                    })
                }
            }), t.addEventListener("click", (function(s) {
                s.preventDefault(), a.validate().then((function(a) {
                    "Valid" == a ? (t.setAttribute("data-kt-indicator", "on"), t.disabled = !0, setTimeout((function() {
                        e.submit();//t.removeAttribute("data-kt-indicator"), t.disabled = !1,
                    }), 1500)) : ""
                }))
            }))
        }
    }
}();
KTUtil.onDOMContentLoaded((function() {
    KTSignupGeneral.init();
}));