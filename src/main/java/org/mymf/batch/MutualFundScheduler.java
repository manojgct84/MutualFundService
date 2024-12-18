package org.mymf.batch;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mymf.data.MutualFund;
import org.mymf.exception.RecordStatusTracker;
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

    private static final Logger logger = LogManager.getLogger(MutualFundScheduler.class);


    @Autowired
    private MutualFundService mutualFundService;

    @Autowired
    private MutualFundFinSireService mutualFundFinSireService;

    @Autowired
    private RecordStatusTracker recordStatusTracker;

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
    public void updateMutualFundsDaily () throws IOException
    {
        LocalDate tMinusOneDate = LocalDate.now().minusMonths(1);
        DateTimeFormatter holdingDate = DateTimeFormatter.ofPattern("yyyy-MM");
        String tMinusOneDateString = tMinusOneDate.format(holdingDate);
        logger.info(String.format("Holding for the month %s", tMinusOneDateString));

        mutualFundService.fetchAndSaveMutualFunds();

        List<MutualFund> allDetails = mutualFundService.getAllMutualFunds();

        logger.info("allDetails size %s", allDetails.size());

        for (MutualFund mf : allDetails) {
            try {

                mutualFundFinSireService.fetchAndSaveMutualFundDetails(mf.getSchemeCode());
                mutualFundFinSireService.fetchAndSaveSectorWiseHolding(mf.getSchemeCode(),
                    tMinusOneDateString);
                mutualFundFinSireService.fetchAndSaveSecurityHoldings(mf.getSchemeCode(),
                    tMinusOneDateString);
            }
            catch (Exception e) {
                Matcher codeMatcher = extractErrorCode(String.valueOf(e));
                if (codeMatcher != null) {
                    recordStatusTracker.writeRecordStatus(Long.valueOf(mf.getSchemeCode()),
                        codeMatcher.group(2), codeMatcher.group(1));
                }
            }
        }
    }

    public static Matcher extractErrorCode (String log)
    {
        Pattern codePattern = Pattern.compile("SQL Error: (\\d+), SQLState: (\\w+)");
        Matcher codeMatcher = codePattern.matcher(log);
        if (codeMatcher.find()) {
            logger.info("SQL Error: " + codeMatcher.group(1) + ", SQLState: " + codeMatcher.group(2));
            return codeMatcher;
        }
        return null;
    }
}

