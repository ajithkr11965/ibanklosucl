package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VLBREparams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VLBREparamsRepository extends JpaRepository<VLBREparams, Long> {


    List<VLBREparams> findByWinumAndSlnoOrderByApplicanttype(String wiNum, Long slno);
}
