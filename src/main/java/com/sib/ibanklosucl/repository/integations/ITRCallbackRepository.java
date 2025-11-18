package com.sib.ibanklosucl.repository.integations;

import com.sib.ibanklosucl.model.integrations.ITRCallback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITRCallbackRepository extends JpaRepository<ITRCallback, Long> {
}
