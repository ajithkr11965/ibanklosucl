package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.CustomerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerDetailsRepository  extends RevisionRepository<CustomerDetails, Long,Long>, JpaRepository<CustomerDetails, Long>{

    CustomerDetails findByApplicantIdAndDelFlg(Long appid,String delflag);

    List<CustomerDetails> findByWiNumAndSlno(String wiNum, Long slno);
}
