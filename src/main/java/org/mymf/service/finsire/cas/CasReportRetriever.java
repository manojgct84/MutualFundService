package org.mymf.service.finsire.cas;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mymf.service.TokenService;
import org.mymf.service.finsire.cas.datamodel.AssetDetail;
import org.mymf.service.finsire.cas.datamodel.ResponseDataDetail;
import org.mymf.service.finsire.cas.datamodel.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class CasReportRetriever
{
    private static final Logger logger = LogManager.getLogger(CasReportRetriever.class);

    // URL for the MF API
    private static final String baseUrl = "https://sandbox.dpiwealth.com/v1/api/mfc/mfc_cas/get_detailed_cas";
    //Mock data for CAS report
    private static final String MOCK_DATA_FILE_PATH = "src/main/resources/castestfile/finsire1035.txt";
    private final RestTemplate restTemplate; // restTemplate is injected
    private final TokenService tokenService;
    private final String CAS_DETAILED = "cas_detailed";
    private final String CAS_SUMMARY = "cas_summary";

    // Constructor to inject the RestTemplate instance
    @Autowired
    public CasReportRetriever (final TokenService tokenService, final RestTemplate restTemplate)
    {
        this.tokenService = tokenService;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity getCasReport (final String finsire_id) throws Exception
    {
        final ObjectMapper objectMapper = new ObjectMapper();

        ResponseEntity<String> response;
        List<AssetDetail> casReport;
        JsonNode rootNode;

        if ("finsire1035".equals(finsire_id)) {
            final String jsonData = Files.readString(Paths.get(MOCK_DATA_FILE_PATH));
            rootNode = objectMapper.readTree(jsonData);
        }
        else {
            final String url = baseUrl + finsire_id + "?type=" + CAS_DETAILED;
            System.out.println(String.format("finservice url %s", url));

            final HttpEntity<String> entity = new HttpEntity<>(buildHeaders());

            response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            rootNode = objectMapper.readTree(response.getBody());

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Failed to fetch mutual fund details: " + response.getStatusCode());
            }
        }

        boolean status = rootNode.path("status").asBoolean();
        int responseCode = rootNode.path("response").path("code").asInt();
        String message = rootNode.path("response").path("message").asText();

        if (responseCode == 206) {
            System.out.println(String.format("Message %s %s ", status, message));
        }

        final JsonNode dataNode = rootNode.path("response");
        casReport = new ArrayList<>();

        for (JsonNode assetNode : dataNode.path("asset_details")) {
            AssetDetail assetDetail = objectMapper.convertValue(assetNode, AssetDetail.class);

            JsonNode responseDataNode = assetNode.path("response_data");
            ResponseDataDetail responseDataDetail = objectMapper.convertValue(responseDataNode, ResponseDataDetail.class);
            assetDetail.setResponseData(responseDataDetail);

            List<Transaction> transactions = objectMapper.convertValue(
                assetNode.path("transactions"),
                new TypeReference<List<Transaction>>()
                {
                }
            );
            assetDetail.setTransactions(transactions);
            casReport.add(assetDetail);
        }
        Map<String, String> assetAllocation = calculateAssetAllocation(casReport);
        String jsonResponse = objectMapper.writeValueAsString(assetAllocation);
        return ResponseEntity.ok(jsonResponse);
    }

    /**
     * Build headers with authorization token.
     */
    private HttpHeaders buildHeaders ()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + tokenService.getEncryptedToken());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public Map<String, String> calculateAssetAllocation (List<AssetDetail> casReport)
    {
        Map<String, BigDecimal> allocationMap = new HashMap<>();
        BigDecimal totalMarketValue = BigDecimal.ZERO;
        Map<String, String> response = new LinkedHashMap<>();

        // Calculate the total market value and allocation per asset type
        for (AssetDetail assetDetail : casReport) {
            String assetType = assetDetail.getResponseData().getAssetType();
            BigDecimal marketValue = new BigDecimal(assetDetail.getResponseData().getMarketValue());

            // Aggregate the market value for each asset type
            allocationMap.put(assetType, allocationMap.getOrDefault(assetType, BigDecimal.ZERO).add(marketValue));
            totalMarketValue = totalMarketValue.add(marketValue);

            // Store individual asset details in the response map
            response.put(assetDetail.getName() + " Unit Price",
                String.valueOf(assetDetail.getUnitPrice()));

            // Calculate total transactions for the asset
            BigDecimal totalTrans = BigDecimal.ZERO;
            for (Transaction trxnAmount : assetDetail.getTransactions()) {
                totalTrans = totalTrans.add(BigDecimal.valueOf(trxnAmount.getTrxnAmount()));
            }
            response.put(assetDetail.getName() + " Total Amount", totalTrans.toString());
        }

        // Calculate percentage allocation and determine the sector with the highest allocation
        String highestAllocationSector = null;
        BigDecimal highestPercentage = BigDecimal.ZERO;

        for (Map.Entry<String, BigDecimal> entry : allocationMap.entrySet()) {
            String assetType = entry.getKey();
            BigDecimal allocationValue = entry.getValue();
            BigDecimal percentage = allocationValue.divide(totalMarketValue, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));

            response.put(assetType + " Allocation", percentage.toPlainString() + "%");

            // Determine if this asset type has the highest allocation
            if (percentage.compareTo(highestPercentage) > 0) {
                highestPercentage = percentage;
                highestAllocationSector = assetType;
            }
        }

        // Output the sector with the highest allocation
        response.put("Highest Allocation Sector", highestAllocationSector);
        response.put("Highest Allocation Percentage", highestPercentage.toPlainString() + "%");

        response.putAll(rebalancePortfolio(casReport, 30, 30, 40));
        return response;
    }

    /**
     * Calculate the allocation based on the user input on each assert class.
     * @param portfolio cas report
     * @param equityTargetPercentage percentage of allocation needed ex: 20% or 30% ..etc
     * @param debtTargetPercentage percentage of allocation needed ex: 30% or 40% ..etc
     * @param goldTargetPercentage percentage of allocation needed ex: 500% or 30% ..etc
     * @return rebalanced allocation value in hashmap.
     */
    public static Map<String, String> rebalancePortfolio (List<AssetDetail> portfolio,
                                                          double equityTargetPercentage,
                                                          double debtTargetPercentage,
                                                          double goldTargetPercentage)
    {

        // Calculate current portfolio values
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal equityValue = BigDecimal.ZERO;
        BigDecimal debtValue = BigDecimal.ZERO;
        BigDecimal goldValue = BigDecimal.ZERO;

        // Loop through the portfolio and calculate current allocation
        for (AssetDetail assetDetail : portfolio) {
            BigDecimal marketValue = new BigDecimal(assetDetail.getResponseData().getMarketValue());
            String assetType = assetDetail.getResponseData().getAssetType();

            totalValue = totalValue.add(marketValue);

            if ("EQUITY".equalsIgnoreCase(assetType)) {
                equityValue = equityValue.add(marketValue);
            }
            else if ("DEBT".equalsIgnoreCase(assetType)) {
                debtValue = debtValue.add(marketValue);
            }
            else if ("GOLD".equalsIgnoreCase(assetType)) {
                goldValue = goldValue.add(marketValue);
            }
        }

        // Calculate the target allocation in monetary terms
        BigDecimal equityTargetValue = totalValue.multiply(BigDecimal.valueOf(equityTargetPercentage / 100));
        BigDecimal debtTargetValue = totalValue.multiply(BigDecimal.valueOf(debtTargetPercentage / 100));
        BigDecimal goldTargetValue = totalValue.multiply(BigDecimal.valueOf(goldTargetPercentage / 100));

        // Calculate the required changes for rebalancing
        BigDecimal equityAdjustment = equityTargetValue.subtract(equityValue);
        BigDecimal debtAdjustment = debtTargetValue.subtract(debtValue);
        BigDecimal goldAdjustment = goldTargetValue.subtract(goldValue);

        // Prepare the response with recommendations
        Map<String, String> response = new LinkedHashMap<>();
        response.put("Total Portfolio Value", totalValue.toString());

        response.put("Current Equity Value", equityValue.toString());
        response.put("Target Equity Value", equityTargetValue.toString());
        response.put("Equity Adjustment (Buy/Sell)", equityAdjustment.toString());

        response.put("Current Debt Value", debtValue.toString());
        response.put("Target Debt Value", debtTargetValue.toString());
        response.put("Debt Adjustment (Buy/Sell)", debtAdjustment.toString());

        response.put("Current Gold Value", goldValue.toString());
        response.put("Target Gold Value", goldTargetValue.toString());
        response.put("Gold Adjustment (Buy/Sell)", goldAdjustment.toString());

        return response;
    }
}
