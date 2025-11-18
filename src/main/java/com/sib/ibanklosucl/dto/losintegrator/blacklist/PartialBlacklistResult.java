
package com.sib.ibanklosucl.dto.losintegrator.blacklist;

        import lombok.*;

        import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartialBlacklistResult {
    public String statusCode;
    private List<PartialBlacklistResponse.PartialBlacklistcheck> body;
}
