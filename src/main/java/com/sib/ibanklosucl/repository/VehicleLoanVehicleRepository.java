package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface VehicleLoanVehicleRepository extends RevisionRepository<VehicleLoanVehicle, Long,Long>, JpaRepository<VehicleLoanVehicle, Long> {


    @Query("SELECT v FROM VehicleLoanVehicle v WHERE v.wiNum = :wiNum AND v.slno = :slno AND v.applicantId = :applicantId AND v.delFlg = 'N'")
    List<VehicleLoanVehicle> findActiveEntries(@Param("wiNum") String wiNum, @Param("slno") Long slno, @Param("applicantId") Long applicantId);


    @Query("SELECT v FROM VehicleLoanVehicle v WHERE v.wiNum = :wiNum AND v.slno = :slno AND v.applicantId = :applicantId AND v.delFlg = 'N'")
    VehicleLoanVehicle findExistingEntry(@Param("wiNum") String wiNum, @Param("slno") Long slno, @Param("applicantId") Long applicantId);

    @Query("SELECT v FROM VehicleLoanVehicle v WHERE v.wiNum = :wiNum AND v.slno = :slno AND v.delFlg = 'N'")
    VehicleLoanVehicle findExistingByWiNumAndSlno(@Param("wiNum") String wiNum, @Param("slno") Long slno);

    @Query("SELECT v FROM VehicleLoanVehicle v WHERE  v.slno = :slno AND v.delFlg = 'N'")
    VehicleLoanVehicle findExistingBySlno(@Param("slno") Long slno);

    @Modifying
    @Query("UPDATE VehicleLoanVehicle v SET v.invoiceDoc = :invoiceDoc,v.invoiceExt = :invoiceext,v.invoiceDate = :invoiceDate,v.totalInvoicePrice = :invoicePrice ,v.invoiceNo = :invoicenumber WHERE v.wiNum = :wiNum and v.slno = :slno")
    int updateDsaInvoiceBywiNumAndSlno(String wiNum, Long slno, String invoiceDoc, String invoiceext, Date invoiceDate, String invoicePrice, String invoicenumber);


}
