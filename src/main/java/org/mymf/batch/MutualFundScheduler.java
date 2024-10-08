package org.mymf.batch;

import java.util.List;

import org.mymf.data.MutualFund;
import org.mymf.service.MutualFundService;
import org.mymf.service.finsire.MutualFundFinSireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * The {@code MutualFundScheduler} class is responsible for scheduling
 * the periodic fetching and updating of mutual fund data from an external API.
 * This scheduler ensures that the mutual fund data is updated in the system once a day.
 *
 * <p> It uses Spring's {@code @Scheduled} annotation to define a scheduled task
 * that runs at a fixed interval, and it relies on the {@code MutualFundService}
 * to perform the data-fetching and saving operations.
 *
 * @author [Manojkumar M]
 * @version 1.0
 * @since 2024-10-03
 */

@Component
public class MutualFundScheduler
{

    @Autowired
    private MutualFundService mutualFundService;

    @Autowired
    private MutualFundFinSireService mutualFundFinSireService;

    /**
     * Scheduled method that triggers the data fetch and save process.
     * <p>
     * This method is executed once a day at 12 AM server time (configurable through the cron
     * expression).
     * </p>
     *
     * <p>The schedule is defined using the cron expression {@code "0 0 1 * * ?"},
     * which means the method will run at 00:00 AM every day.</p>
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateMutualFundsDaily ()
    {
        mutualFundService.fetchAndSaveMutualFunds();

        List<MutualFund> allDetails = mutualFundService.getAllMutualFunds();

        for (MutualFund mf : allDetails) {
            mutualFundFinSireService.fetchAndSaveMutualFundDetails(mf.getSchemeCode());
        }
    }
}

