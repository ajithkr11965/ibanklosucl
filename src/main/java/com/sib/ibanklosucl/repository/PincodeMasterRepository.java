package com.sib.ibanklosucl.repository;


import com.sib.ibanklosucl.dto.ExperianPincodeMasterDTO;
import com.sib.ibanklosucl.dto.LocationDTO;
import com.sib.ibanklosucl.model.PincodeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PincodeMasterRepository extends JpaRepository<PincodeMaster, String> {



    @Query("SELECT new com.sib.ibanklosucl.dto.ExperianPincodeMasterDTO(a.pincode, a.district, a.stateName,a.stateCode, a.finacleCity, a.finacleCityCode,a.finacleState,a.finacleStateCode,b.expStateName,b.expStateCode,b.regionCode)  FROM PincodeMaster a inner join StateCodeExperian b on a.finacleStateCode=b.finacleStateCode  and a.pincode = :pincode ")
    Optional<ExperianPincodeMasterDTO> getexperianaddressdata(@Param("pincode") String pincode) ;


    @Query("SELECT new com.sib.ibanklosucl.dto.ExperianPincodeMasterDTO(b.expStateCode,b.regionCode) from StateCodeExperian b where  b.finacleStateCode = :statecode ")
    Optional<ExperianPincodeMasterDTO> getExpData(@Param("statecode") String statecode) ;

    Long countByFinacleStateCodeAndPincodeAndFinacleCityCode(String statecode,String pincode,String city);

    @Query("SELECT new  com.sib.ibanklosucl.dto.LocationDTO(p.stateCode, p.stateName, " +
            "p.finacleCityCode, p.finacleCity, p.district) FROM PincodeMaster p")
    List<LocationDTO> findAllLocationsWithDistricts();

    @Query("SELECT p FROM PincodeMaster p WHERE p.stateCode = :stateCode " +
            "AND p.finacleCityCode = :cityCode")
    List<PincodeMaster> findByStateAndCity(
            @Param("stateCode") String stateCode,
            @Param("cityCode") String cityCode
    );

    @Query("SELECT p FROM PincodeMaster p WHERE p.stateCode = :stateCode ")
    List<PincodeMaster> findByState(
            @Param("stateCode") String stateCode
    );

    boolean existsByPincode(String pincode);

}
