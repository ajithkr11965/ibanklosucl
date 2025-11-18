package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.DKData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DKDataRepository extends JpaRepository<DKData, String> {
    DKData findByAppidAndSlnoAndWinumberAndActiveFlg(String appid,String slno,String winumber, String activeflg);
    @Query("SELECT d FROM DKData d WHERE d.winumber = :winumber AND d.slno = :slno AND d.activeFlg = 'Y' ORDER BY d.appid")
    List<DKData> findAllByWinumberAndSlnoAndActiveFlg(@Param("winumber") String winumber, @Param("slno") String slno);


}

