package org.mymf;

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
import org.mymf.service.finsire.cas.CasReportRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * This is to run the batch service to get the data for the api.
 */
@Component
public class StartupTask
{
    private static final Logger logger = LogManager.getLogger(StartupTask.class);

    @Autowired
    private MutualFundService mutualFundService;

    @Autowired
    private MutualFundFinSireService mutualFundFinSireService;

    @Autowired
    private RecordStatusTracker recordStatusTracker;

    /**
     * This will call the service to get all the MF data.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup () throws IOException
    {
        LocalDate tMinusOneDate = LocalDate.now().minusMonths(2);
        DateTimeFormatter holdingDate = DateTimeFormatter.ofPattern("yyyy-MM");
        String tMinusOneDateString = tMinusOneDate.format(holdingDate);
        logger.info(String.format("Holding for the month %s", tMinusOneDateString));
        logger.info("Application is fully started!");
        //mutualFundService.init();
        List<MutualFund> allDetails = mutualFundService.getAllMutualFunds();

        logger.info(String.format("allDetails size %s", allDetails.size()));

        for (MutualFund mf : allDetails) {
            try {
                Set<String> insertedRecords = recordStatusTracker.loadExistingRecords();
                if (insertedRecords.contains(mf.getSchemeCode())) {
                    continue;
                }
                mutualFundFinSireService.fetchAndSaveMutualFundDetails(mf.getSchemeCode());
                mutualFundFinSireService.fetchAndSaveSectorWiseHolding(mf.getSchemeCode(),
                    tMinusOneDateString);
                mutualFundFinSireService.fetchAndSaveSecurityHoldings(mf.getSchemeCode(),
                    tMinusOneDateString);
                recordStatusTracker.logSuccessRecord(mf.getSchemeCode());
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
