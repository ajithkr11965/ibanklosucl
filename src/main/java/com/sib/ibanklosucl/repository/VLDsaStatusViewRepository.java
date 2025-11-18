package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VLDsaStatusView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VLDsaStatusViewRepository extends JpaRepository<VLDsaStatusView, String> {
    List<VLDsaStatusView> findByCmuser(@Param("cmuser") String cmuser);
}

