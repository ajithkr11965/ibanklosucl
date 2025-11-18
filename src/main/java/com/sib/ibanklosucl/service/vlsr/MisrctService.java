package com.sib.ibanklosucl.service.vlsr;

import com.sib.ibanklosucl.model.Misrct;
import com.sib.ibanklosucl.repository.MisrctRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MisrctService {
    @Autowired
    private MisrctRepository misrctRepository;

    public List<Misrct> getCodeValuesByType(String codetype) {
        return misrctRepository.findByCodetypeAndDelflag(codetype,"N");
    }
    public List<Misrct> getCodeValuesByTypeOrdered(String codetype) {
        return misrctRepository.findByCodetypeAndDelflagOrderByCodedesc(codetype,"N");
    }
    public List<Misrct> getByCodeValue(String codetype) {
        return misrctRepository.findByCodevalueAndDelflag(codetype,"N");
    }
    public Optional<Misrct> getByCodedesc(String codetype,String codedesc) {
        return misrctRepository.findByCodetypeAndCodedescIgnoreCaseAndDelflag(codetype,codedesc,"N");
    }
    public Misrct getByCodeValue(String codetype,String codevalue) {
        return misrctRepository.findByCodetypeAndCodevalueAndDelflag(codetype,codevalue,"N");
    }
    public List<Misrct> getCodeLikeType(String codetype,String codedesc) {
        return misrctRepository.findLikeCode(codetype,codedesc);
    }
}
