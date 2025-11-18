package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.dto.dashboard.RemarksHistDTO;
import com.sib.ibanklosucl.repository.RemarksHistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RemarksHistService {

    @Autowired
    private RemarksHistRepository remarkshistrepository;




    public List<RemarksHistDTO> getRemarksList(String slno) {
        return remarkshistrepository.getRemarks(slno);
    }






}

