package org.mymf.service.finsire.mapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.JsonNode;
import org.mymf.data.finsire.MutualFundDetails;
import org.mymf.data.finsire.MutualFundDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MutualFundDetailsMapping implements MutualFundMapping
{
    @Autowired
    private MutualFundDetailsRepository mutualFundDetailsRepository;

    @Override
    public void mapDetails (JsonNode dataNode)
    {
        final MutualFundDetails mutualFundDetails = new MutualFundDetails();
        mutualFundDetails.setAmcCode(dataNode.path("amc_code").asText());
        mutualFundDetails.setAmcName(dataNode.path("amc_name").asText());
        mutualFundDetails.setSchemeName(dataNode.path("scheme_name").asText());
        mutualFundDetails.setSchemeNameUnique(dataNode.path("scheme_name_unique").asText());
        mutualFundDetails.setSchemeCode(dataNode.path("scheme_code").asText());
        mutualFundDetails.setDateOfInception(LocalDate.parse(dataNode.path("date_of_inception").asText(), DateTimeFormatter.ofPattern("dd-MMM-yy")));
        mutualFundDetails.setAssetCategory(dataNode.path("asset_category").asText());
        mutualFundDetails.setAssetSubCategory(dataNode.path("asset_sub_category").asText());
        mutualFundDetails.setOptionName(dataNode.path("option_name").asText());
        mutualFundDetails.setPlanName(dataNode.path("plan_name").asText());
        mutualFundDetails.setRiskProfile(dataNode.path("risk_profile").asText());
        mutualFundDetails.setRiskRating(dataNode.path("risk_rating").asInt());
        mutualFundDetails.setBenchmark(dataNode.path("benchmark").asText());
        mutualFundDetails.setNav(dataNode.path("nav").asDouble());
        mutualFundDetails.setNavDate(LocalDate.parse(dataNode.path("nav_date").asText().substring(0, 10))); // Only the date part
        mutualFundDetails.setFundSize(dataNode.path("fund_size").asDouble());
        mutualFundDetails.setFundManager(dataNode.path("fund_manager").asText());
        mutualFundDetails.setIsinDividendPayoutOrGrowth(dataNode.path("isin_dividend_payout_or_growth").asText());
        mutualFundDetails.setIsinDividendReinvest(dataNode.path("isin_dividend_reinvest").asText(""));
        mutualFundDetails.setBseTxn(dataNode.path("bse_txn").asText());
        mutualFundDetails.setBseCodePayoutOrGrowth(dataNode.path("bse_code_payout_or_growth").asText());
        mutualFundDetails.setBseCodeReinvest(dataNode.path("bse_code_reinvest").asText(""));
        mutualFundDetails.setExpenseRatio(dataNode.path("expense_ratio(s)_&_d").asDouble());
        mutualFundDetails.setObjective(dataNode.path("objective").asText());
        mutualFundDetails.setSchemeDocUrl(dataNode.path("scheme_doc_url").asText());
        mutualFundDetails.setRiskometer(dataNode.path("riskometer").asText());

        // Transaction Info
        final JsonNode txnInfo = dataNode.path("txn_info");
        mutualFundDetails.setMinInvest(txnInfo.path("min_invest").asInt());
        mutualFundDetails.setMinInvestSip(txnInfo.path("min_invest_sip").asInt());
        mutualFundDetails.setVrRating(dataNode.path("vr_rating").asText());

        // Exit Load
        final MutualFundDetails.ExitLoad exitLoad = new MutualFundDetails.ExitLoad();
        JsonNode exitLoadNode = dataNode.path("exit_load");
        exitLoad.setExitLoadPeriod(exitLoadNode.path("exit_load_period").asInt());
        exitLoad.setExitLoadRate(exitLoadNode.path("exit_load_rate").asDouble());
        exitLoad.setExitLoadPeriodRemark(exitLoadNode.path("exit_load_period_remark").asText());

        mutualFundDetails.setExitLoad(exitLoad);
        System.out.println(String.format("mutualFundDetails %s ", mutualFundDetails.toString()));
        mutualFundDetailsRepository.save(mutualFundDetails);
    }
}
