package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.model.integrations.VLBlackList;

public interface CheckerIntegrationService {

    VLBlackList runBlacklist(String workItemId, String personType);
}
