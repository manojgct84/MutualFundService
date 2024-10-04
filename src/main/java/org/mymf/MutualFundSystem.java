package org.mymf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Mutual Fund Service to get all the MF details
 *
 */
@SpringBootApplication
@EnableScheduling
public class MutualFundSystem
{
    public static void main( String[] args )
    {
        SpringApplication.run(MutualFundSystem.class, args);
    }
}
