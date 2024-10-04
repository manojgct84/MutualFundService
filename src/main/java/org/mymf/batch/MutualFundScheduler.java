package org.mymf.batch;

import org.mymf.service.MutualFundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MutualFundScheduler {

    @Autowired
    private MutualFundService mutualFundService;

    // Schedule this task to run once a day
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateMutualFundsDaily() {
        mutualFundService.fetchAndSaveMutualFunds();
    }
}

