package com.sib.ibanklosucl.dto.esb;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


public class EsbRequestWrapper {

    @Getter
    @Setter
    public static class PanValidateRequestWrapper {
        private EsbRequest.PanValidateEsbRequest Request;
    }
    @Data
    public static class UidDemoRequestWrapper {
        private EsbRequest.UidDemoEsbRequest Request;
    }
    @Getter
    @Setter
    public static class UIDRequestWrapper {
        private EsbRequest.UIDEsbRequest Request;
    }
    @Getter
    @Setter
    public static class UIDValidateRequestWrapper {
        private EsbRequest.UIDValidateEsbRequest Request;
    }

}


