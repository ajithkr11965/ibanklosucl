package com.sib.ibanklosucl.service.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.sib.ibanklosucl.model.integrations.BureauCheckDetails;
import com.sib.ibanklosucl.repository.integations.BureauCheckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class BureauCheckService {

    @Autowired
    private BureauCheckRepository bureauCheckRepository;

    public void saveBureauCheckResponse(String wiNum, Long slno, Long applicantId,
                                        JsonNode responseNode, String username) {

        // Extract message node from the nested structure
        JsonNode messageNode = responseNode.path("Response")
                                         .path("Body")
                                         .path("message");

        // First, mark existing records as deleted
        List<BureauCheckDetails> existingRecords = bureauCheckRepository
            .findByWiNumAndSlnoAndApplicantIdAndDelFlg(wiNum, slno, applicantId, "N");

        for (BureauCheckDetails existing : existingRecords) {
            existing.setDelFlg("Y");
            existing.setCmuser(username);
            existing.setCmdate(new Date());
            bureauCheckRepository.save(existing);
        }

        // Create new record
        BureauCheckDetails bureauCheck = new BureauCheckDetails();
        bureauCheck.setWiNum(wiNum);
        bureauCheck.setSlno(slno);
        bureauCheck.setApplicantId(applicantId);

        // Set fields from response
        bureauCheck.setFirstName(messageNode.path("firstName").asText());
        bureauCheck.setMiddleName(messageNode.path("middleName").asText());
        bureauCheck.setLastName(messageNode.path("lastName").asText());
        bureauCheck.setMaskedAadhaar(messageNode.path("maskedAadhaar").asText());
        bureauCheck.setName(messageNode.path("name").asText());
        bureauCheck.setStatus(messageNode.path("status").asText());
        bureauCheck.setAadhaarLinked(messageNode.path("aadhaarLinked").asBoolean());
        bureauCheck.setGender(messageNode.path("gender").asText());
        bureauCheck.setIsPanValid(messageNode.path("isPanValid").asBoolean());
        bureauCheck.setPanType(messageNode.path("panType").asText());
        bureauCheck.setPan(messageNode.path("pan").asText());

        bureauCheck.setDelFlg("N");
        bureauCheck.setCmuser(username);
        bureauCheck.setCmdate(new Date());

        bureauCheckRepository.save(bureauCheck);
    }
}
