package com.sib.ibanklosucl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;
import com.sib.ibanklosucl.model.MaterialListData;
import java.util.List;


@Repository
public interface MaterialListDataRepository extends RevisionRepository<MaterialListData, Long,Long>, JpaRepository<MaterialListData, Long> {
    List<MaterialListData> findBySlno(Long slno);


}
