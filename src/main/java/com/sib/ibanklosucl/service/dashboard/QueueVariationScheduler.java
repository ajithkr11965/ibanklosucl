package com.sib.ibanklosucl.service.dashboard;

import com.sib.ibanklosucl.model.menuaccess.QueueStat;
import com.sib.ibanklosucl.model.menuaccess.QueueVariation;
import com.sib.ibanklosucl.repository.menuaccess.QueueStatRepository;
import com.sib.ibanklosucl.repository.menuaccess.QueueVariationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@Slf4j
@Profile({"uat","prod"})
public class QueueVariationScheduler {
    @Autowired
    private QueueVariationRepository queueVariationRepository;
    @Autowired
    private QueueStatRepository queueStatRepository; // Assuming this exists to fetch data from queue_stat view.
   // @Scheduled(cron = "0 * * * * ?")
    public void testSchedule()
    {
        log.info("Starting Scheduler ");
    }
    @Scheduled(cron = "0 59 23 ? * MON-SUN")
    public void captureDailyCounts() {
        log.info("Starting_Scheduler captureDailyCounts");
        List<QueueStat> queueStats = queueStatRepository.findAll();

        log.info("Questats "+queueStats);
        for (QueueStat queueStat : queueStats) {
            QueueVariation variation = new QueueVariation();
            variation.setSolId(queueStat.getSolId());
            variation.setMenuId(queueStat.getQueue());
            variation.setPreviousCount(queueStat.getCount());

            variation.setTimestamp(LocalDateTime.now());
            log.info("inserting "+variation);
            queueVariationRepository.save(variation);
        }
    }

}
