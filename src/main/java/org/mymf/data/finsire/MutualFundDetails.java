package org.mymf.data.finsire;


import java.time.LocalDate;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MutualFundDetails
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String amcCode;
    private String amcName;
    private String schemeName;
    private String schemeNameUnique;
    private String schemeCode;
    private LocalDate dateOfInception;
    private String assetCategory;
    private String assetSubCategory;
    private String optionName;
    private String planName;
    private String riskProfile;
    private int riskRating;
    private String benchmark;
    private double nav;
    private LocalDate navDate;
    private double fundSize;
    private String fundManager;
    private String isinDividendPayoutOrGrowth;
    private String isinDividendReinvest;
    private String bseTxn;
    private String bseCodePayoutOrGrowth;
    private String bseCodeReinvest;
    private double expenseRatio;
    private String objective;
    private String schemeDocUrl;
    private String riskometer;
    private int minInvest;
    private int minInvestSip;
    private String vrRating;

    // Exit Load as a separate entity
    @Embedded
    private ExitLoad exitLoad;

    // Getters and Setters
    // Omitted for brevity

    @Embeddable
    @Getter
    @Setter
    public static class ExitLoad
    {
        private int exitLoadPeriod;
        private double exitLoadRate;
        private String exitLoadPeriodRemark;

        // Getters and Setters
        // Omitted for brevity
    }
}


