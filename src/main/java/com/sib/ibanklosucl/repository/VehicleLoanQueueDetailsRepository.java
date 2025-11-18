package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanQueueDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleLoanQueueDetailsRepository extends JpaRepository<VehicleLoanQueueDetails, Long> {

    List<VehicleLoanQueueDetails> findByWiNum(String wiNum);

    List<VehicleLoanQueueDetails> findBySlno(Long slno);

    List<VehicleLoanQueueDetails> findByWiNumAndSlno(String wiNum, Long slno);

    List<VehicleLoanQueueDetails> findByFromQueue(String fromQueue);

    List<VehicleLoanQueueDetails> findByToQueue(String toQueue);
}