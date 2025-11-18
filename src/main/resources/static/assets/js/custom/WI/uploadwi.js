

let AppidMap=new Map();


var FileUpload = function() {


    //
    // Setup module components
    //

    // Bootstrap file upload
    var _componentFileUpload = function () {
        if (!$().fileinput) {
            console.warn('Warning - fileinput.min.js is not loaded.');
            return;
        }

        //
        // Define variables
        //

        // Modal template
        var modalTemplate = '<div class="modal-dialog modal-lg" role="document">\n' +
            '  <div class="modal-content">\n' +
            '    <div class="modal-header align-items-center">\n' +
            '      <h6 class="modal-title">{heading} <small><span class="kv-zoom-title"></span></small></h6>\n' +
            '      <div class="kv-zoom-actions btn-group">{toggleheader}{fullscreen}{borderless}{close}</div>\n' +
            '    </div>\n' +
            '    <div class="modal-body">\n' +
            '      <div class="floating-buttons btn-group"></div>\n' +
            '      <div class="kv-zoom-body file-zoom-content"></div>\n' + '{prev} {next}\n' +
            '    </div>\n' +
            '  </div>\n' +
            '</div>\n';

        // Buttons inside zoom modal
        var previewZoomButtonClasses = {
            toggleheader: 'btn btn-light btn-icon btn-header-toggle btn-sm',
            fullscreen: 'btn btn-light btn-icon btn-sm',
            borderless: 'btn btn-light btn-icon btn-sm',
            close: 'btn btn-light btn-icon btn-sm'
        };

        // Icons inside zoom modal classes
        var previewZoomButtonIcons = {
            prev: '<i class="icon-arrow-left32"></i>',
            next: '<i class="icon-arrow-right32"></i>',
            toggleheader: '<i class="icon-menu-open"></i>',
            fullscreen: '<i class="icon-screen-full"></i>',
            borderless: '<i class="icon-alignment-unalign"></i>',
            close: '<i class="icon-cross2 font-size-base"></i>'
        };

        // File actions
        var fileActionSettings = {
            zoomClass: '',
            zoomIcon: '<i class="icon-zoomin3"></i>',
            dragClass: 'p-2',
            dragIcon: '<i class="icon-three-bars"></i>',
            removeClass: '',
            removeErrorClass: 'text-danger',
            removeIcon: '<i class="icon-bin"></i>',
            indicatorNew: '<i class="icon-file-plus text-success"></i>',
            indicatorSuccess: '<i class="icon-checkmark3 file-icon-large text-success"></i>',
            indicatorError: '<i class="icon-cross2 text-danger"></i>',
            indicatorLoading: '<i class="icon-spinner2 spinner text-muted"></i>'
        };

        //
        // $('.file-input').fileinput({
        //     browseLabel: 'Browse',
        //     browseIcon: '<i class="icon-file-plus mr-2"></i>',
        //     removeIcon: '<i class="icon-cross2 font-size-base mr-2"></i>',
        //     layoutTemplates: {
        //         icon: '<i class="icon-file-check"></i>',
        //         modal: modalTemplate
        //     },
        //     initialCaption: "No file selected",
        //     maxFileSize: 5000,
        //     maxFilesNum: 1,
        //     allowedFileExtensions: ["jpg", "gif", "png", "txt","pdf","jpeg"],
        //     previewZoomButtonClasses: previewZoomButtonClasses,
        //     previewZoomButtonIcons: previewZoomButtonIcons,
        //     fileActionSettings: fileActionSettings,
        //     showCancel: false
        // });
        $('.file-input').each(function() {
            var base64Image = $(this).data('filebase64');
            var ext = $(this).data('filebase64ext');
            var filedesc = $(this).data('filedesc');
            var appid=$(this).closest('.tab-pane').find('.generaldetails').find('.appid').val()

            var fileInputConfig = {
                browseLabel: 'Browse',
                browseIcon: '<i class="icon-file-plus mr-2"></i>',
                removeIcon: '<i class="icon-cross2 font-size-base mr-2"></i>',
                layoutTemplates: {
                    icon: '<i class="icon-file-check"></i>',
                    modal: modalTemplate
                },
                initialCaption: "No file selected",
                maxFileSize: 5000,
                maxFilesNum: 1,
                allowedFileExtensions: ["jpg", "tiff", "png","pdf","jpeg"],
                previewZoomButtonClasses: previewZoomButtonClasses,
                previewZoomButtonIcons: previewZoomButtonIcons,
                fileActionSettings: fileActionSettings,
                msgPlaceholder: filedesc,
                dropZoneTitle: filedesc,
                dropZoneClickTitle: ' ',
                showCancel: false
            };

            if (base64Image) {
                if(ext!=='pdf') {
                    fileInputConfig.initialPreview = [
                        'data:image/' + ext + ';base64,' + base64Image
                    ];
                    fileInputConfig.initialPreviewFileType = 'image';
                }
                else{
                    fileInputConfig.initialPreview = [
                        'data:application/pdf;base64,' + base64Image
                    ];
                    fileInputConfig.initialPreviewFileType = 'pdf';
                }
                fileInputConfig.initialPreviewAsData = true;
                fileInputConfig.initialPreviewConfig = [
                    {caption: "Previously uploaded image", key: 1}
                ];
                fileInputConfig.overwriteInitial = true;

                var fileType = null,fileClass=null;
                if($(this).hasClass('uidfile')) {
                    fileType='AADHAAR' ;
                    fileClass='uidfile';
                } else if($(this).hasClass('panfile')) {
                    fileType = 'PAN';
                    fileClass='panfile';
                } else if($(this).hasClass('passportfile')) {
                    fileType = 'PASSPORT';
                    fileClass='passportfile';
                }
                else if($(this).hasClass('visafile')) {
                    fileType = 'VISA_OCI';
                    fileClass='visafile';
                }
                else if($(this).hasClass('photofile')) {
                    fileType = 'PHOTO';
                    fileClass='photofile';
                }
                else if($(this).hasClass('consentfile')) {
                    fileType = 'CONSENT_FORM';
                    fileClass='consentfile';
                }
                else if($(this).hasClass('originalfile')) {
                    fileType = 'ORIGINAL_SEEN_VERIFIED';
                    fileClass='originalfile';
                }
                else if($(this).hasClass('custsig')) {
                    fileType = 'CUSTOMER_SIGNATURE';
                    fileClass='custsig';
                }
                else if($(this).hasClass('invoiceDoc')) {
                    fileType = 'INVOICE_DOC';
                    fileClass='invoiceDoc';
                }
                let fileMap;
                if(AppidMap.get(appid)){
                    fileMap=AppidMap.get(appid);
                }
                else {
                    fileMap= new Map();
                }
                fileMap.set(fileClass,{
                        docName:fileType,
                        base64String:base64Image,
                        fileExtension:ext
                    }
                )
                AppidMap.set(appid,fileMap);
            }

            $(this).fileinput(fileInputConfig).on('filebatchselected', function(event, files) {
                // Get the file extension
                var file = files[0];
                var th_=$(this);
                var appid=th_.closest('.tab-pane').find('.generaldetails').find('.appid').val()
                fileExtension = file.name.split('.').pop();
                // Get the base64 content from the preview
                var previewElement = $(this).closest('.col-lg-6').find('.file-preview-frame') .first().find('img, embed, object');
                if (previewElement.is('img')) {
                    base64String = previewElement.attr('src').split(',')[1];
                    saveFileToMap(base64String, fileExtension,appid);
                } else if (previewElement.is('embed') || previewElement.is('object')) {
                    var blobUrl = previewElement.attr('data');
                    if (blobUrl && blobUrl.startsWith('blob:')) {
                        fetch(blobUrl)
                            .then(response => response.blob())
                            .then(blob => {
                                var reader = new FileReader();
                                reader.onloadend = function() {
                                    base64String = reader.result.split(',')[1];
                                    saveFileToMap(base64String, fileExtension,appid);
                                };
                                reader.readAsDataURL(blob);
                            });
                    }
                } else {
                    base64String = previewElement.attr('src').split(',')[1];
                    saveFileToMap(base64String, fileExtension,appid);
                }
                function saveFileToMap(base64String, fileExtension,appid) {
                    var fileType = null, fileClass = null;
                    if (th_.hasClass('uidfile')) {
                        fileType = 'AADHAAR';
                        fileClass = 'uidfile';
                    } else if (th_.hasClass('panfile')) {
                        fileType = 'PAN';
                        fileClass = 'panfile';
                    } else if (th_.hasClass('passportfile')) {
                        fileType = 'PASSPORT';
                        fileClass = 'passportfile';
                    } else if (th_.hasClass('visafile')) {
                        fileType = 'VISA_OCI';
                        fileClass = 'visafile';
                    } else if (th_.hasClass('photofile')) {
                        fileType = 'PHOTO';
                        fileClass = 'photofile';
                    } else if (th_.hasClass('consentfile')) {
                        fileType = 'CONSENT_FORM';
                        fileClass = 'consentfile';
                    } else if (th_.hasClass('originalfile')) {
                        fileType = 'ORIGINAL_SEEN_VERIFIED';
                        fileClass = 'originalfile';
                    } else if (th_.hasClass('custsig')) {
                        fileType = 'CUSTOMER_SIGNATURE';
                        fileClass = 'custsig';
                    } else if (th_.hasClass('invoiceDoc')) {
                        fileType = 'INVOICE_DOC';
                        fileClass = 'invoiceDoc';
                    }
                    let fileMap=AppidMap.get(appid);
                    if(fileMap) {
                        fileMap.set(fileClass, {
                                docName: fileType,
                                base64String: base64String,
                                fileExtension: fileExtension
                            }
                        )
                    }
                    else{
                        fileMap=new Map();
                        fileMap.set(fileClass, {
                                docName: fileType,
                                base64String: base64String,
                                fileExtension: fileExtension
                            }
                        )
                    }
                    AppidMap.set(appid,fileMap);
                }

            });
        });


    };
return {
    init: function () {
        _componentFileUpload();
      //  console.log(AppidMap);
    }
}
}();



// Initialize module
// ------------------------------

document.addEventListener('DOMContentLoaded', function() {
 //   FileUpload.init();
});
