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

@Service
public class MutualFundService
{

    private final String MF_API_URL = "https://api.mfapi.in/mf";

    @Autowired
    private MutualFundRepository mutualFundRepository;

    @Autowired
    private RestTemplate restTemplate;

    // Method to fetch and save data from MFAPI
    @PostConstruct
    public void init ()
    {
        fetchAndSaveMutualFunds();
    }

    // Fetch the latest data from the API and store NAV values
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

    // Method to fetch NAV value for a specific scheme
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

    // Parse the NAV history from the API response
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

    // Other service methods like getMutualFundsBySchemeName, etc.
    public List<MutualFund> getMutualFundsBySchemeName (String schemeName)
    {
        return mutualFundRepository.findBySchemeNameContaining(schemeName);
    }

    public List<MutualFund> getMutualFundsBySchemeType (String schemeType)
    {
        return mutualFundRepository.findBySchemeType(schemeType);
    }
}

