package com.sib.ibanklosucl.dto.losintegrator.cif;

        import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLoanCifResult {
    public String code;

    public String desc;
    private String cifId;
}
