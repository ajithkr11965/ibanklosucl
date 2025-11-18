package com.sib.ibanklosucl.repository.program;

import com.sib.ibanklosucl.model.ExperianData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExperianDataRepository extends JpaRepository<ExperianData, Long> {

    Optional<ExperianData> findExperianByWiNumAndActiveFlg(String winum, String activeFlg);

List<ExperianData> findByWiNumAndPan(String wiNum, String pan);
void deleteByWiNumAndPan(String wiNum,String pan);
    @Query("select count(*) from ExperianData e where e.wiNum =  :wiNum")
    int countByWiNum(String wiNum);





}
