package org.mymf.service;


import jakarta.annotation.PostConstruct;
import org.mymf.data.MutualFund;
import org.mymf.data.MutualFundRepository;
import org.mymf.data.NAVHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The {@code MutualFundService} class provides methods to manage and retrieve
 * mutual fund data. This service handles the fetching of mutual fund information
 * from an external API, saving it to the database, and exposing methods to query
 * the data based on scheme name or scheme type.
 *
 * <p>This class is a Spring {@code @Service} component and is typically used by
 * the controller to handle business logic related to mutual funds.</p>
 *
 * <h2>Key Responsibilities:</h2>
 * <ul>
 *   <li>Fetching mutual fund data from the external API.</li>
 *   <li>Saving or updating mutual fund data in the database.</li>
 *   <li>Providing methods to query mutual fund data by scheme name or scheme type.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * This class should be annotated with {@code @Service} to allow Spring to manage it as a bean.
 *
 * <pre>
 * {@code
 * @Service
 * public class MutualFundService {
 *     // Business logic for mutual funds
 * }
 * }
 * </pre>
 *
 * @author [Manojkumar]
 * @version 1.0
 * @since 2024-10-03
 */
@Service
public class MutualFundService
{

    private final String MF_API_URL = "https://api.mfapi.in/mf";

    @Autowired
    private MutualFundRepository mutualFundRepository;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Fetches and saves mutual fund data from an external API.
     * <p>This method is called during the initialization of the service
     * and updates the database with the latest mutual fund data.</p>
     */
    @PostConstruct
    public void init ()
    {
        fetchAndSaveMutualFunds();
    }

    /**
     * Fetches mutual fund data from the external API and saves it in the database.
     * <p>If a mutual fund already exists, it will be updated with the new data.
     * Otherwise, the new mutual fund is added to the database.</p>
     */
    public void fetchAndSaveMutualFunds ()
    {
        // Fetch all mutual fund data from the general API
        MutualFund[] funds = restTemplate.getForObject(MF_API_URL, MutualFund[].class);

        if (funds != null) {
            for (MutualFund fund : funds) {
                // Get additional scheme details, like NAV history for each mutual fund scheme
                Map<String, Object> schemeDetails = getSchemeDetails(fund.getSchemeCode());

                if (schemeDetails != null) {
                    // Parse the NAV history and store it
                    ArrayList<HashMap<String, String>> navData = (ArrayList<HashMap<String, String>>) schemeDetails.get("data");
                    List<NAVHistory> navHistory = parseNavHistory(navData);

                    // Add NAV history to the MutualFund object
                    fund.setNavHistory(navHistory);

                    // Check if the fund already exists and update or save
                    List<MutualFund> existingFund = mutualFundRepository.findBySchemeCode(fund.getSchemeCode());
                    if (existingFund.isEmpty()) {
                        mutualFundRepository.save(fund);
                    }
                    else {
                        MutualFund updatedFund = existingFund.get(0);
                        updatedFund.setNavHistory(navHistory);
                        mutualFundRepository.save(updatedFund);
                    }
                }
            }
            System.out.print("The DB insert process is completed!!!");
        }
    }

    /**
     * Retrieves a list of mutual funds by scheme code for all nav value.
     *
     * @param schemeName The name of the scheme to search for.
     * @return A list of mutual funds matching the scheme code with nav value.
     */
    public Map<String, Object> getSchemeDetails (String schemeCode)
    {
        String apiUrl = MF_API_URL + "/" + schemeCode;
        try {
            return restTemplate.getForObject(apiUrl, Map.class);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private List<NAVHistory> parseNavHistory (ArrayList<HashMap<String, String>> navData)
    {
        List<NAVHistory> navHistoryList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (HashMap<String, String> navEntry : navData) {
            String dateStr = navEntry.get("date");
            String navValueStr = navEntry.get("nav");

            if (dateStr != null && navValueStr != null) {
                LocalDate navDate = LocalDate.parse(dateStr, formatter);
                double navValue = Double.parseDouble(navValueStr);

                NAVHistory navHistory = new NAVHistory(navDate, navValue);
                navHistoryList.add(navHistory);
            }
        }
        return navHistoryList;
    }


    /**
     * Retrieves a list of mutual funds by scheme name.
     *
     * @param schemeName The name of the scheme to search for.
     * @return A list of mutual funds matching the scheme name.
     */
    public List<MutualFund> getMutualFundsBySchemeName (String schemeName)
    {
        return mutualFundRepository.findBySchemeNameContaining(schemeName);
    }

    /**
     * Retrieves a list of mutual funds by scheme type.
     *
     * @param schemeType The type of the scheme to search for.
     * @return A list of mutual funds matching the scheme type.
     */
    public List<MutualFund> getMutualFundsBySchemeType (String schemeType)
    {
        return mutualFundRepository.findBySchemeType(schemeType);
    }
}

