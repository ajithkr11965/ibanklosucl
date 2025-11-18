package com.sib.ibanklosucl.service.dashboard;

import com.sib.ibanklosucl.model.menuaccess.NewsAlert;
import com.sib.ibanklosucl.repository.menuaccess.NewsAlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NewsAlertService {

    @Autowired
    private NewsAlertRepository newsAlertRepository;

    public List<NewsAlert> getActiveNewsAlerts() {
        LocalDateTime currentDate = LocalDateTime.now();
        return newsAlertRepository.findActiveNewsAlerts(currentDate);
    }
}
