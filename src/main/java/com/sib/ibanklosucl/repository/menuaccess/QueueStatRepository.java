package com.sib.ibanklosucl.repository.menuaccess;

import com.sib.ibanklosucl.model.menuaccess.QueueStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QueueStatRepository extends JpaRepository<QueueStat, Long> {

    // Custom query to find the count for a specific solId and menuId (queue)
    @Query(value = "SELECT COUNT FROM queue_stat WHERE sol_id = :solId AND queue = :queue", nativeQuery = true)
    int findCountBySolIdAndMenuId(@Param("solId") String solId, @Param("queue") String queue);
}

