package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.model.VehicleLoanQueueDetails;
import com.sib.ibanklosucl.repository.VehicleLoanQueueDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleLoanQueueDetailsService {

    @Autowired
    private VehicleLoanQueueDetailsRepository queueDetailsRepository;

    @Transactional
    public VehicleLoanQueueDetails save(VehicleLoanQueueDetails queueDetails) {
        return queueDetailsRepository.save(queueDetails);
    }

    public Optional<VehicleLoanQueueDetails> findById(Long id) {
        return queueDetailsRepository.findById(id);
    }

    public List<VehicleLoanQueueDetails> findByWiNum(String wiNum) {
        return queueDetailsRepository.findByWiNum(wiNum);
    }

    public List<VehicleLoanQueueDetails> findBySlno(Long slno) {
        return queueDetailsRepository.findBySlno(slno);
    }

    public List<VehicleLoanQueueDetails> findByWiNumAndSlno(String wiNum, Long slno) {
        return queueDetailsRepository.findByWiNumAndSlno(wiNum, slno);
    }

    public List<VehicleLoanQueueDetails> findByFromQueue(String fromQueue) {
        return queueDetailsRepository.findByFromQueue(fromQueue);
    }

    public List<VehicleLoanQueueDetails> findByToQueue(String toQueue) {
        return queueDetailsRepository.findByToQueue(toQueue);
    }

    @Transactional
    public void delete(VehicleLoanQueueDetails queueDetails) {
        queueDetailsRepository.delete(queueDetails);
    }

    @Transactional
    public VehicleLoanQueueDetails createQueueEntry(String wiNum, Long slno, String remarks, String cmuser, String fromQueue, String toQueue) {
        VehicleLoanQueueDetails queueDetails = new VehicleLoanQueueDetails();
        queueDetails.setWiNum(wiNum);
        queueDetails.setSlno(slno);
        queueDetails.setRemarks(remarks);
        queueDetails.setCmuser(cmuser);
        queueDetails.setFromQueue(fromQueue);
        queueDetails.setToQueue(toQueue);
        return save(queueDetails);
    }
    @Transactional
    public VehicleLoanQueueDetails createQueueWithAssignUserEntry(String wiNum, Long slno, String remarks, String cmuser, String fromQueue, String toQueue,String assignUser) {
        VehicleLoanQueueDetails queueDetails = new VehicleLoanQueueDetails();
        queueDetails.setWiNum(wiNum);
        queueDetails.setSlno(slno);
        queueDetails.setRemarks(remarks);
        queueDetails.setCmuser(cmuser);
        queueDetails.setFromQueue(fromQueue);
        queueDetails.setToQueue(toQueue);
        queueDetails.setAssignUser(assignUser);
        return save(queueDetails);
    }

}

