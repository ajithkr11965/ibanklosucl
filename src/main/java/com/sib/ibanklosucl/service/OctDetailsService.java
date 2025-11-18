package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.model.Octdetails;
import com.sib.ibanklosucl.repository.OctdetailsRepository;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OctDetailsService {
    @Autowired
    private OctdetailsRepository octdetailsrepository;
    @Autowired
    private UserSessionData usd;


    public List<Octdetails> getOctDetails()
    {
        return octdetailsrepository.findByDelFlag("N").stream().sorted(Comparator.comparing(Octdetails::getOctDesc)).collect(Collectors.toList());
    }


}

