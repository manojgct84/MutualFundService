package org.mymf.service.finsire;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.mymf.config.FinSireOAuthTokenInit;
import org.mymf.data.finsire.DataMetrics;
import org.mymf.data.finsire.DiscreteReturns;
import org.mymf.data.finsire.DiscreteReturnsRepository;
import org.mymf.data.finsire.MutualFundDetails;
import org.mymf.data.finsire.MutualFundDetailsRepository;
import org.mymf.data.finsire.NavHistoryDetails;
import org.mymf.data.finsire.NavHistoryRepository;
import org.mymf.data.finsire.RiskRequest;
import org.mymf.data.finsire.SchemeLevelRiskRepository;
import org.mymf.data.finsire.SectorWiseHolding;
import org.mymf.data.finsire.SectorWiseHoldingRepository;
import org.mymf.data.finsire.SecurityHolding;
import org.mymf.data.finsire.SecurityHoldingsRepository;
import org.mymf.data.finsire.TimeRangeMetrics;
import org.mymf.data.finsire.risk.CategoryLevelRisk;
import org.mymf.data.finsire.risk.CategoryLevelRiskRepository;
import org.mymf.data.finsire.risk.CategoryRiskResponse;
import org.mymf.data.finsire.risk.RiskAverages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MutualFundFinSireService
{

    private final String baseUrl = "https://sandbox.dpiwealth.com/v1/api/mf_data/"; // Example URL for the API

    private final RestTemplate restTemplate; // OkHttpClient is injected

    private final FinSireOAuthTokenInit finSireOAuthTokenInit;

    @Autowired
    private MutualFundDetailsRepository mutualFundDetailsRepository;

    @Autowired
    private NavHistoryRepository navHistoryRepository;

    @Autowired
    private SecurityHoldingsRepository securityHoldingsRepository;

    @Autowired
    private SectorWiseHoldingRepository sectorWiseHoldingRepository;

    @Autowired
    private CategoryLevelRiskRepository categoryLevelRiskRepository;

    @Autowired
    private SchemeLevelRiskRepository schemeLevelRiskRepository;

    @Autowired
    private DiscreteReturnsRepository discreteReturnsRepository;

    // Constructor to inject the RestTemplate instance
    @Autowired
    public MutualFundFinSireService (final FinSireOAuthTokenInit finSireOAuthTokenInit,
                                     final RestTemplate restTemplate)
    {
        this.finSireOAuthTokenInit = finSireOAuthTokenInit;
        this.restTemplate = restTemplate;
    }

    /**
     * Fetch and save MF Details using RestTemplate
     *
     * @param schemeCode - Scheme code of the mutual fund
     */
    public void fetchAndSaveMutualFundDetails (String schemeCode)
    {
        final String url = baseUrl + "mf_detailed?scheme_code=" + schemeCode;

        // Create headers
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + getEncryptedToken());
        headers.set("Accept", "application/json");

        // Create an entity with the headers
        final HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Make the GET request with RestTemplate and fetch the response as a String
            final ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,
                entity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Failed to fetch mutual fund details: " + response.getStatusCode());
            }

            // Parse JSON response
            final ObjectMapper objectMapper = new ObjectMapper();
            final JsonNode responseBody = objectMapper.readTree(response.getBody());

            // Extract mutual fund details from response data
            final JsonNode dataNode = responseBody.path("response").path("data").path(schemeCode);

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

            // Save to database
            mutualFundDetailsRepository.save(mutualFundDetails);

        }
        catch (Exception e) {
            throw new RuntimeException("Error while fetching mutual fund details", e);
        }
    }

    /**
     * Fetch and store mutual fund details from the API based on the schemeCode.
     *
     * @param schemeCode The mutual fund scheme code.
     * @return The mutual fund details.
     */

    /**
     * Utility method to generate the encrypted token for API authentication.
     */
    private String getEncryptedToken ()
    {
        String token =
            finSireOAuthTokenInit.getLenderSecret() + ":" + finSireOAuthTokenInit.getDistributorSecret();
        return Base64.getUrlEncoder().encodeToString(token.getBytes());
    }

    /**
     * Fetch and store NAV history from the API based on the schemeCode.
     *
     * @param schemeCode The mutual fund scheme code.
     */
    public void fetchAndSaveNavHistoryDetails (String schemeCode, String fromDate, String toDate, String frequency)
    {
        String url = baseUrl + "nav_history";

        // Prepare the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("scheme_code", schemeCode);
        requestBody.put("from", fromDate);
        requestBody.put("to", toDate);
        requestBody.put("frequency", frequency);

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + getEncryptedToken()); // Add Authorization
        headers.setAccept(new ArrayList<>(List.of(new org.springframework.http.MediaType[]{org.springframework.http.MediaType.APPLICATION_JSON})));

        // Create HttpEntity to include both headers and body
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // Send POST request using RestTemplate
            ResponseEntity<NavHistoryDetails[]> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                NavHistoryDetails[].class
            );

            // Check if response is successful
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                NavHistoryDetails[] navHistoryDetailsArray = response.getBody();

                // Save to database
                for (NavHistoryDetails navHistoryDetails : navHistoryDetailsArray) {
                    navHistoryRepository.save(navHistoryDetails);
                }

                System.out.println("Nav history details saved successfully.");
            }
            else {
                System.out.println("Error: " + response.getStatusCodeValue() + " " + response.getStatusCode());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while fetching NAV history details.");
        }
    }

    /**
     * Fetch and store security holdings from the API based on the schemeCode.
     *
     * @param schemeCode The mutual fund scheme code.
     */
    public void fetchAndSaveSecurityHoldings (final String schemeCode, final String date)
    {
        final String url =
            baseUrl + "security_holdings?scheme_code=" + schemeCode + "&date=" + date;

        // Prepare headers
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + getEncryptedToken());  // Add Authorization
        headers.set("Accept", "application/json");

        // Create HttpEntity with headers
        final HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Send GET request and get the response
            final ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );

            // Step 2: Parse the JSON response
            final ObjectMapper mapper = new ObjectMapper();
            final JsonNode rootNode = mapper.readTree(response.getBody());
            final JsonNode dataNode = rootNode.path("response").path("data");

            // Step 3: Iterate over the array and map to SecurityHolding
            if (dataNode.isArray()) {
                for (JsonNode node : dataNode) {
                    SecurityHolding holding = new SecurityHolding();
                    holding.setSchemeCode(schemeCode);
                    holding.setMarketValue(node.path("market_value").asDouble());
                    holding.setAsOnDate(node.path("as_on_date").asText());
                    holding.setSecurity(node.path("security").asText());
                    holding.setQuantity(node.path("quantity").asLong());
                    holding.setCoupon(node.path("coupon").asDouble());
                    holding.setAgency(node.path("agency").asText());
                    holding.setHoldingPercentage(node.path("holding_perc").asDouble());
                    holding.setMarketCapCategory(node.path("mkt_cap_category").asText());
                    holding.setRating(node.path("rating").asText());
                    holding.setSector(node.path("sector").asText());
                    holding.setEquivRating(node.path("equiv_rating").asText());
                    holding.setIsinSecurity(node.path("isin_security").asText());

                    // Step 4: Save to the database
                    securityHoldingsRepository.save(holding);
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetch and store sector-wise holdings from the API based on the schemeCode.
     *
     * @param schemeCode The mutual fund scheme code.
     * @param date       The date in which the holding as present.
     * @return List of sector-wise holdings for the mutual fund.
     */
    public List<SectorWiseHolding> fetchAndSaveSectorWiseHolding (final String schemeCode,
                                                                  final String date)
    {
        final String url = baseUrl + "sector_holdings?scheme_code=" + schemeCode + "&date=" + date;
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + getEncryptedToken());

        final HttpEntity<String> entity = new HttpEntity<>(headers);
        List<SectorWiseHolding> sectorWiseHoldingList = null;
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class);

            // Step 2: Parse the JSON response
            final ObjectMapper mapper = new ObjectMapper();
            final JsonNode rootNode = mapper.readTree(response.getBody());
            final JsonNode dataNode = rootNode.path("response").path("data");
            sectorWiseHoldingList = new ArrayList<>();
            // Step 3: Iterate over the array and map to SecurityHolding
            if (dataNode.isArray()) {
                for (JsonNode node : dataNode) {
                    SectorWiseHolding swHolding = new SectorWiseHolding();
                    swHolding.setSchemeCode(schemeCode);
                    swHolding.setSector(node.path("sector").asText());
                    swHolding.setHoldingPerc(node.path("holding_perc").decimalValue());
                    swHolding.setMarketValue(node.path("market_value").decimalValue());
                    swHolding.setAsOnDate(node.path("as_on_date").asText());
                    // Step 4: Save to the database
                    sectorWiseHoldingRepository.save(swHolding);
                    sectorWiseHoldingList.add(swHolding);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return sectorWiseHoldingList;
    }

    /**
     * Fetch and store category-level risks from the API based on the schemeCode.
     *
     * @param request The mutual fund risk based on the filter request.
     */
    public void fetchAndSaveCategoryLevelRisks (final RiskRequest request)
    {

        final String url = baseUrl + "/category_level_risks/";
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + getEncryptedToken());

        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.set("accept", "application/json");

        // Create the request entity (headers + body)
        final HttpEntity<RiskRequest> entity = new HttpEntity<>(request, headers);

        // Send POST request
        final ResponseEntity<CategoryRiskResponse> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            entity,
            CategoryRiskResponse.class
        );

        // Check if the response status is OK (200)
        if (response.getStatusCode() == HttpStatus.OK) {
            // Return the parsed response body
            CategoryRiskResponse categoryLevelRisk = response.getBody();

            // Extract risk metrics from response
            RiskAverages averages = categoryLevelRisk.getData().getOneY().getAverages();

            // Create the entity object to save
            CategoryLevelRisk categoryRiskEntity = new CategoryLevelRisk();
            categoryRiskEntity.setAnnualSd(averages.getAnnualSd());
            categoryRiskEntity.setBeta(averages.getBeta());
            categoryRiskEntity.setSharpeRatio(averages.getSharpeRatio());
            categoryRiskEntity.setJensensAlpha(averages.getJensensAlpha());
            categoryRiskEntity.setAssetSubCategory(request.getAssetSubCategory());
            categoryRiskEntity.setPlanName(request.getPlanName());
            categoryRiskEntity.setRange(request.getRanges());

            // Save to database
            categoryLevelRiskRepository.save(categoryRiskEntity);

        }
        else {
            throw new RuntimeException("Failed to fetch category level risks. Status: " + response.getStatusCode());
        }
    }

    /**
     * Fetch and store scheme-level risks from the API based on the schemeCode.
     *
     * @param schemeCode The mutual fund scheme code.
     * @return List of scheme-level risks for the mutual fund.
     */
    public void fetchAndSaveSchemeLevelRisks (final String schemeCode,
                                              final String fromDate, final String toDate, final String range)
    {
        final String url = baseUrl + "scheme_level_risks";

        final String jsonBody = "{"
            + "\"scheme_code\":\"" + schemeCode + "\","
            + "\"from\":\"" + fromDate + "\","
            + "\"to\":\"" + toDate + "\","
            + "\"frequency\":\"" + range + "\""
            + "}";

        // Set headers
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + getEncryptedToken());  // Add Authorization token
        headers.set("Accept", "application/json");
        headers.set("Content-Type", "application/json");

        // Create request entity
        final HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        try {
            // Make POST request
            final ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
            );

            // Check if the response is successful
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IOException("Unexpected response status: " + response.getStatusCode());
            }
            // Parse and save data
            saveData(Objects.requireNonNull(response.getBody()), schemeCode);

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveData (final String jsonResponse, String schemeCode) throws Exception
    {
        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode rootNode = objectMapper.readTree(jsonResponse);
        final JsonNode dataNode = rootNode.path("response").path("data").path("118419");

        // Create DataMetrics entity
        final DataMetrics dataMetrics = new DataMetrics();
        dataMetrics.setSchemeCode(schemeCode);

        // List to hold all time range metrics
        final List<TimeRangeMetrics> timeRangeMetricsList = new ArrayList<>();

        // Parse and save each time range (1M, 3M, 6M, etc.)
        saveTimeRangeMetrics(dataNode, "1M", dataMetrics, timeRangeMetricsList);
        saveTimeRangeMetrics(dataNode, "3M", dataMetrics, timeRangeMetricsList);
        saveTimeRangeMetrics(dataNode, "6M", dataMetrics, timeRangeMetricsList);
        saveTimeRangeMetrics(dataNode, "9M", dataMetrics, timeRangeMetricsList);
        saveTimeRangeMetrics(dataNode, "1Y", dataMetrics, timeRangeMetricsList);
        saveTimeRangeMetrics(dataNode, "3Y", dataMetrics, timeRangeMetricsList);
        saveTimeRangeMetrics(dataNode, "5Y", dataMetrics, timeRangeMetricsList);
        saveTimeRangeMetrics(dataNode, "9Y", dataMetrics, timeRangeMetricsList);

        // Set the list of time range metrics to the dataMetrics entity
        dataMetrics.setTimeRangeMetrics(timeRangeMetricsList);

        // Save to the repository
        schemeLevelRiskRepository.save(dataMetrics);
    }

    private void saveTimeRangeMetrics (final JsonNode dataNode, final String timeRange,
                                       final DataMetrics dataMetrics,
                                       final List<TimeRangeMetrics> timeRangeMetricsList)
    {
        JsonNode timeRangeNode = dataNode.path(timeRange);
        if (!timeRangeNode.isMissingNode()) {
            // Create TimeRangeMetrics entity
            TimeRangeMetrics metrics = new TimeRangeMetrics();
            metrics.setTimeRange(timeRange);
            metrics.setAnnualSd(timeRangeNode.path("annual_sd").asDouble());
            metrics.setBeta(timeRangeNode.path("beta").asDouble());
            metrics.setSharpeRatio(timeRangeNode.path("sharpe_ratio").asDouble());
            metrics.setTreynorRatio(timeRangeNode.path("treynor_ratio").asDouble());
            metrics.setJensensAlpha(timeRangeNode.path("jensens_alpha").asDouble());
            metrics.setInformationRatio(timeRangeNode.path("information_ratio").asDouble());
            metrics.setSortinoRatio(timeRangeNode.path("sortino_ratio").asDouble());
            metrics.setRSquared(timeRangeNode.path("r_squared").asDouble());

            // Associate it with the DataMetrics
            metrics.setDataMetrics(dataMetrics);

            // Add to the list
            timeRangeMetricsList.add(metrics);
        }
    }

    /**
     * Fetch and store discrete returns from the API based on the schemeCode.
     *
     * @param schemeCode The mutual fund scheme code.
     * @return List of discrete returns for the mutual fund.
     */

    @Transactional
    public void fetchAndSaveDiscreteReturns(String schemeCode, String fromDate, String frequency) {
        final String url = baseUrl + "discrete_returns";

        // Create the request body as a JSON string
        final String jsonRequestBody = String.format(
            "{\"scheme_codes\":\"%s\",\"from\":\"%s\",\"frequency\":\"%s\"}",
            schemeCode, fromDate, frequency
        );

        // Set the headers
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + getEncryptedToken());  // Add Authorization token
        headers.set("Accept", "application/json");
        headers.set("Content-Type", "application/json");

        // Create the HttpEntity with headers and request body
        final HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequestBody, headers);

        try {
            // Make the POST request using RestTemplate
            final ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
            );

            // Check if the response is successful
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to fetch data: " + response.getStatusCode());
            }

            // Parse the response body
            final String jsonResponse = response.getBody();
            final ObjectMapper objectMapper = new ObjectMapper();
            final JsonNode rootNode = objectMapper.readTree(jsonResponse);
            final JsonNode dataNode = rootNode.path("data").path(schemeCode);

            // Iterate over the years and store the data
            final Iterator<Map.Entry<String, JsonNode>> yearsIterator = dataNode.fields();
            while (yearsIterator.hasNext()) {
                Map.Entry<String, JsonNode> yearEntry = yearsIterator.next();
                String year = yearEntry.getKey();
                JsonNode yearData = yearEntry.getValue();

                // Create the DiscreteReturn entity and map data
                final DiscreteReturns discreteReturn = new DiscreteReturns();
                discreteReturn.setSchemeCode(schemeCode);
                discreteReturn.setYear(year);
                discreteReturn.setQ1(yearData.path("Q1").decimalValue());
                discreteReturn.setQ2(yearData.path("Q2").decimalValue());
                discreteReturn.setH1(yearData.path("H1").decimalValue());
                discreteReturn.setQ3(yearData.path("Q3").decimalValue());
                discreteReturn.setQ4(yearData.path("Q4").decimalValue());
                discreteReturn.setH2(yearData.path("H2").decimalValue());
                discreteReturn.setAnnual(yearData.path("annual").decimalValue());
                discreteReturn.setYtd(yearData.has("YTD") ? yearData.path("YTD").decimalValue() : null);

                // Save the discrete return to the database
                discreteReturnsRepository.save(discreteReturn);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
