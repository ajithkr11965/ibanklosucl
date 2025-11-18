package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.Octdetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OctdetailsRepository extends JpaRepository<Octdetails, Long> {
    List<Octdetails> findByDelFlag(String DelFlag);
}
