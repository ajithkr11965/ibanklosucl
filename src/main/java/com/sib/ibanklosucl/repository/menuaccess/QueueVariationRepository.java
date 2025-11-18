package com.sib.ibanklosucl.repository.menuaccess;

import com.sib.ibanklosucl.model.menuaccess.QueueVariation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QueueVariationRepository extends JpaRepository<QueueVariation, Long> {

    @Query(value = "SELECT DISTINCT previous_count FROM Queue_variation " +
            "WHERE sol_id = :solId AND menu_id = :menuId " +
            "AND TRUNC(timestamp) = TRUNC(SYSDATE - 1) AND ROWNUM = 1",
            nativeQuery = true)
    Optional<Integer> findPreviousCountBySolIdAndMenuIdAndTimestampBetween(String solId, String menuId);

}


