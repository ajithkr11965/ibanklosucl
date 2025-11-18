
package com.sib.ibanklosucl.repository;
import com.sib.ibanklosucl.model.VehicleLoanBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;


@Repository
public interface VehicleLoanBlockRepository extends RevisionRepository<VehicleLoanBlock, Long,Long>, JpaRepository<VehicleLoanBlock, Long> {
    Optional<List<VehicleLoanBlock>> findByWiNumAndBlockType(String wiNum, String blockType);

    VehicleLoanBlock findByWiNumAndBlockTypeAndDelFlag(String wiNum, String blockType, String delFlag);
    VehicleLoanBlock findByWiNumAndBlockTypeAndApplicantIdAndDelFlag(String wiNum, String blockType, String applicantId, String delFlag);

}



