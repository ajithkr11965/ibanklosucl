package com.sib.ibanklosucl.dto.losintegrator.blacklist;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistResult {
    private boolean blacklisted;
    private String blacklistReasons;
}
