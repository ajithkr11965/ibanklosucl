package com.sib.ibanklosucl.repository.menuaccess;

import com.sib.ibanklosucl.model.menuaccess.MISHRDesig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MISHRDesigRepository extends JpaRepository<MISHRDesig, MISHRDesig.MISHRDesigId> {

    // Example query method
    List<MISHRDesig> findByIdPpcNo(String ppcNo);

    // Add other custom query methods as needed
}
