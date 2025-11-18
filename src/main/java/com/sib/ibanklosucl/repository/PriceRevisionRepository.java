package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.PriceRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceRevisionRepository extends JpaRepository<PriceRevision, Long> {
    @Query(value = "SELECT * from (SELECT * FROM VLPRICEREVISION pr WHERE pr.WI_NUM = :wiNum AND pr.slno = :slno ORDER BY pr.cmdate DESC) WHERE ROWNUM = 1",nativeQuery = true)
    Optional<PriceRevision> findLatestByWiNumAndSlno(@Param("wiNum") String wiNum, @Param("slno") Long slno);

}
