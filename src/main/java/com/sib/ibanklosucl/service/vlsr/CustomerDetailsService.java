package com.sib.ibanklosucl.service.vlsr;

import com.sib.ibanklosucl.model.CustomerDetails;
import com.sib.ibanklosucl.repository.CustomerDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerDetailsService {

    @Autowired
    private CustomerDetailsRepository customerDetailsRepository;

    @Transactional
    public void saveCustomerDetails(CustomerDetails customerDetails) {
        customerDetailsRepository.save(customerDetails);
    }
    public CustomerDetails findByAppId(Long id) {
        return customerDetailsRepository.findByApplicantIdAndDelFlg(id,"N");
    }


}
