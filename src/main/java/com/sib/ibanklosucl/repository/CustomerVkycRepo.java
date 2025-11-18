package com.sib.ibanklosucl.repository;
import com.sib.ibanklosucl.dto.CustomerVkycBasicDetailsDTO;
import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerVkycRepo extends JpaRepository<VehicleLoanApplicant, Long>{
    @Query("SELECT new com.sib.ibanklosucl.dto.CustomerVkycBasicDetailsDTO(a.applName, c.mobileNo, b.aadharRefNum,a.applicantId, a.wiNum, a.slno,'xxxxxx',nvl(a.cifId,'NA'),a.sibCustomer,nvl(a.cifCreationMode,'NA'),nvl(b.panNo,'NA'),nvl(b.panName,'NA'),nvl(to_char(b.panDob,'dd-mm-yyyy'),'NA'))  FROM VehicleLoanApplicant a inner join VehicleLoanKyc b on a.slno=b.slno and a.wiNum=b.wiNum and a.applicantId = b.applicantId and a.delFlg='N' and b.delFlg='N' inner join VehicleLoanBasic c on a.slno=c.slno and a.wiNum=c.wiNum and a.applicantId = c.applicantId and c.delFlg='N' where a.slno = :slno and a.applicantId = :appid ")
    CustomerVkycBasicDetailsDTO getBaseDetails(@Param("slno") Long slno,@Param("appid") Long appid) ;

}
