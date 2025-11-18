package com.sib.ibanklosucl.repository;


import com.sib.ibanklosucl.model.VLFileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VLFileUploadRepository extends RevisionRepository<VLFileUpload, Long,Long>, JpaRepository<VLFileUpload, Long> {


    List<VLFileUpload> findAllBySlno(Long slno);

    @Query("select count(*) from VLFileUpload v where v.slno=:slno")
    Long countAllBySlno(Long slno);
}