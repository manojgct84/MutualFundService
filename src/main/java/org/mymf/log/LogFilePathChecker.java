package org.mymf.log;

import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;


@Component
public class LogFilePathChecker {

    private static final Logger logger = LogManager.getLogger(LogFilePathChecker.class);

    @PostConstruct
    public void logTempDirectory() {
        logger.info("Log file is being created in temporary directory: " + System.getProperty("java.io.tmpdir"));
    }
}

