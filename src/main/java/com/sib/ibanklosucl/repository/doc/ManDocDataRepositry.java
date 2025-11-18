package com.sib.ibanklosucl.repository.doc;


import com.sib.ibanklosucl.model.doc.ManDocData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManDocDataRepositry  extends RevisionRepository<ManDocData, Long,Long>, JpaRepository<ManDocData, Long> {

    Optional<List<ManDocData>> findBySlno(Long slno);

    @Query("select count(*)  from ManDocData l where l.slno=:slno and l.uploadFlg = false ")
    Long isAllDocUploaded(Long slno);


    @Modifying
    @Transactional
    @Query("update ManDocData l set l.uploadFlg=true,l.uploadDate=sysdate,l.uploadUser=:ppcno where l.slno=:slno ")
    void updateStatus(Long slno,String ppcno);
    @Modifying
    @Transactional
    @Query("update ManDocData l set l.uploadFlg=true,l.uploadDate=sysdate,l.uploadUser=:ppcno where l.slno=:slno and l.docName=:docName")
    void updateStatus(Long slno,String ppcno,String docName);

   // @Query("select count (*) from ManDocData m where m.slno=:slno and to_date(:docDate,'yyyy-mm-dd') between trunc(m.lastModDate) and trunc(m.uploadDate) ")
    @Query("select count (*) from ManDocData m where m.slno=:slno and to_date(:docDate,'yyyy-mm-dd') <= trunc(m.uploadDate)")
    Long isDocDateValid(Long slno ,String docDate);


}
