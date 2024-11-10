package org.mymf.data.finsire;


import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class MutualFundDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String amcCode;
    private String amcName;
    @Column(columnDefinition = "TEXT")
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
    @Column(columnDefinition = "TEXT")
    private String objective;
    @Column(columnDefinition = "TEXT")
    private String schemeDocUrl;
    private String riskometer;
    private int minInvest;
    private int minInvestSip;
    private String vrRating;

    // Exit Load as a separate entity
    @Embedded
    private ExitLoad exitLoad;

    @Embeddable
    @Getter
    @Setter
    public static class ExitLoad
    {
        @Override
        public String toString ()
        {
            return "ExitLoad{" +
                "exitLoadPeriod=" + exitLoadPeriod +
                ", exitLoadRate=" + exitLoadRate +
                ", exitLoadPeriodRemark='" + exitLoadPeriodRemark + '\'' +
                '}';
        }
        private int exitLoadPeriod;
        private double exitLoadRate;
        @Column(columnDefinition = "TEXT")
        private String exitLoadPeriodRemark;
    }
}


