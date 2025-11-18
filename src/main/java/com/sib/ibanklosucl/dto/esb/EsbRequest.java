package com.sib.ibanklosucl.dto.esb;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


public class EsbRequest {

    @Getter
    @Setter
    public static class PanValidateEsbRequest {
        private Header Header;
        private PanNsdlBody Body;
    }
    @Data
    public static class UidDemoEsbRequest {
        private Header Header;
        private UIDDemographicBody Body;
    }
    @Getter
    @Setter
    public static class UIDEsbRequest {
        private Header Header;
        private UIDBody Body;
    }
    @Getter
    @Setter
    public static class UIDValidateEsbRequest {
        private Header Header;
        private UIDOtpValidate Body;
    }

}
