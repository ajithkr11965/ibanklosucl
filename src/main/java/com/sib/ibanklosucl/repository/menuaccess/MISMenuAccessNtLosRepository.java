package com.sib.ibanklosucl.repository.menuaccess;


import com.sib.ibanklosucl.model.menuaccess.MISMenuAccessNtLos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MISMenuAccessNtLosRepository extends JpaRepository<MISMenuAccessNtLos, MISMenuAccessNtLos.MISMenuAccessNtLosId> {

    // Correctly reference the busUnitId within the embedded ID class
    List<MISMenuAccessNtLos> findByIdAccessId(String AccessId);

    // Or you can explicitly specify the path if needed
}

