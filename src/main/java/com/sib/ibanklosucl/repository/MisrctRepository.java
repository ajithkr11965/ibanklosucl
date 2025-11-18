package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.Misrct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MisrctRepository extends JpaRepository<Misrct, String> {

    List<Misrct> findByCodetypeAndDelflag(String codetype,String del);
    List<Misrct> findByCodetypeAndDelflagOrderByCodedesc(String codetype,String del);
    List<Misrct> findByCodevalueAndDelflag(String codevalue,String del);
    Misrct findByCodetypeAndCodevalueAndDelflag(String codetype,String codevalue,String del);
    Optional<Misrct> findByCodetypeAndCodedescIgnoreCaseAndDelflag(String codetype,String codedesc, String del);

    @Query(value = "select m from Misrct m where m.delflag='N' and m.codetype=:codetype and lower(m.codedesc) like lower(concat('%',:codedesc,'%')) order by m.codedesc")
    List<Misrct> findLikeCode(String codetype,String codedesc);
}
