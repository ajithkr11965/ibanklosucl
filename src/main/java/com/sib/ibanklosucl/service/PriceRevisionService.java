package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.model.PriceRevision;
import com.sib.ibanklosucl.repository.PriceRevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class PriceRevisionService {

    @Autowired
    private PriceRevisionRepository repository;

    public PriceRevision save(PriceRevision priceRevision) {
        priceRevision.setCmdate(new Date());
        return repository.save(priceRevision);
    }

    public Optional<PriceRevision> findByWiNumAndSlno(String wiNum, Long slno) {
        return repository.findLatestByWiNumAndSlno(wiNum, slno);
    }

}
