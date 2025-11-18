package com.sib.ibanklosucl.repository.menuaccess;

import com.sib.ibanklosucl.model.menuaccess.NewsAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface NewsAlertRepository extends JpaRepository<NewsAlert, Long> {

    @Query("SELECT n FROM NewsAlert n WHERE n.delFlg = 'N' AND :currentDate BETWEEN n.startDate AND n.endDate")
    List<NewsAlert> findActiveNewsAlerts(LocalDateTime currentDate);
}
