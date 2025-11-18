package com.sib.ibanklosucl.service.esbsr;

import com.sib.ibanklosucl.dto.esb.Header;
import com.sib.ibanklosucl.dto.esb.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EsbRequestBuilder {

    @Value("${esb.ChannelID}")
    private String  ChannelID;
        public EsbRequestWrapper.PanValidateRequestWrapper PanValRequestWrapper(PanNsdlBody body) {
            EsbRequestWrapper.PanValidateRequestWrapper requestWrapper = new EsbRequestWrapper.PanValidateRequestWrapper();
            EsbRequest.PanValidateEsbRequest request = new EsbRequest.PanValidateEsbRequest();
            request.setHeader(createHeader());
            request.setBody(body);
            requestWrapper.setRequest(request);
            return requestWrapper;
        }
        public EsbRequestWrapper.UidDemoRequestWrapper UidDemoRequestWrapper(UIDDemographicBody body) {
            EsbRequestWrapper.UidDemoRequestWrapper requestWrapper = new EsbRequestWrapper.UidDemoRequestWrapper();
            EsbRequest.UidDemoEsbRequest request = new EsbRequest.UidDemoEsbRequest();
            request.setHeader(createHeader());
            request.setBody(body);
            requestWrapper.setRequest(request);
            return requestWrapper;
        }


        public EsbRequestWrapper.UIDRequestWrapper UIDRequestWrapper(UIDBody body) {
            EsbRequestWrapper.UIDRequestWrapper requestWrapper = new EsbRequestWrapper.UIDRequestWrapper();
            EsbRequest.UIDEsbRequest request = new EsbRequest.UIDEsbRequest();
            request.setHeader(createHeader());
            request.setBody(body);
            requestWrapper.setRequest(request);
            return requestWrapper;
        }
        public EsbRequestWrapper.UIDValidateRequestWrapper UIDValRequestWrapper(UIDOtpValidate body) {
            EsbRequestWrapper.UIDValidateRequestWrapper requestWrapper = new EsbRequestWrapper.UIDValidateRequestWrapper();
            EsbRequest.UIDValidateEsbRequest request = new EsbRequest.UIDValidateEsbRequest();
            request.setHeader(createHeader());
            request.setBody(body);
            requestWrapper.setRequest(request);
            return requestWrapper;
        }

        private Header createHeader(){
            // Header
            Header header = new Header();
            DeviceDetails deviceDetails = new DeviceDetails();
            deviceDetails.setDeviceID("Device1");
            deviceDetails.setOS("");
            deviceDetails.setMobileNumber("");
            deviceDetails.setBrowserType("");
            deviceDetails.setIMEINumber("");
            deviceDetails.setClientIP("");
            deviceDetails.setGeoLocation(new GeoLocation("13.072090", "80.201859"));
            ChannelDetails channelDetails = new ChannelDetails();
            channelDetails.setChannelID(ChannelID);
            channelDetails.setChannelType("WEB");
            channelDetails.setChannelSubClass("Retail");
            channelDetails.setBranchCode("");
            channelDetails.setChannelCusHdr(new ChannelCusHdr("Web"));
            header.setDeviceDetails(deviceDetails);
            header.setChannelDetails(channelDetails);
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
            header.setTimestamp(now.format(formatter));
            return header;
        }
}

