package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.user.VehicleCustomUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface CustomUploadRepository extends RevisionRepository<VehicleCustomUpload, Long,Long>, JpaRepository<VehicleCustomUpload, Long> {


}