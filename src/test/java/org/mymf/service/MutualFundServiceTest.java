package org.mymf.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mymf.data.MutualFund;
import org.mymf.data.MutualFundRepository;
import org.mymf.data.NAVHistory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MutualFundServiceTest {

    @Mock
    private MutualFundRepository mutualFundRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MutualFundService mutualFundService;

    private MutualFund mockFund1;
    private MutualFund mockFund2;
    private NAVHistory navHistory1;
    private NAVHistory navHistory2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Prepare mock MutualFund and NAVHistory data
        mockFund1 = new MutualFund();
        mockFund1.setSchemeCode("ABC123");
        mockFund1.setUpdatedAt(LocalDate.now().atStartOfDay());

        mockFund2 = new MutualFund();
        mockFund2.setSchemeCode("XYZ456");
        mockFund2.setUpdatedAt(LocalDate.now().atStartOfDay());

        navHistory1 = new NAVHistory(LocalDate.parse("01-10-2024", DateTimeFormatter.ofPattern("dd-MM-yyyy")), 100.0);
        navHistory2 = new NAVHistory(LocalDate.parse("02-10-2024", DateTimeFormatter.ofPattern("dd-MM-yyyy")), 102.0);
    }

    @Test
    public void testFetchAndSaveMutualFunds_withNewFunds() {
        // Arrange
        MutualFund[] mockResponse = {mockFund1, mockFund2};

        // Mock external API responses for funds and scheme details
        when(restTemplate.getForObject(eq("https://api.mfapi.in/mf"), eq(MutualFund[].class)))
            .thenReturn(mockResponse);

        Map<String, Object> schemeDetails = new HashMap<>();
        ArrayList<HashMap<String, String>> navData = new ArrayList<>();
        HashMap<String, String> navEntry1 = new HashMap<>();
        navEntry1.put("date", "01-10-2024");
        navEntry1.put("nav", "100.0");
        HashMap<String, String> navEntry2 = new HashMap<>();
        navEntry2.put("date", "02-10-2024");
        navEntry2.put("nav", "102.0");
        navData.add(navEntry1);
        navData.add(navEntry2);
        schemeDetails.put("data", navData);

        when(restTemplate.getForObject(any(String.class), eq(Map.class)))
            .thenReturn(schemeDetails);

        // Mock repository responses
        when(mutualFundRepository.findBySchemeCode("ABC123")).thenReturn(Collections.emptyList());
        when(mutualFundRepository.findBySchemeCode("XYZ456")).thenReturn(Collections.emptyList());

        // Act
        mutualFundService.fetchAndSaveMutualFunds();

        // Assert
        // Verify that save() was called twice, once for each new fund
        verify(mutualFundRepository, times(2)).save(any(MutualFund.class));
    }

    @Test
    public void testFetchAndSaveMutualFunds_withExistingFunds() {
        // Arrange
        MutualFund existingFund = new MutualFund();
        existingFund.setSchemeCode("ABC123");

        MutualFund[] mockResponse = {mockFund1};

        // Mock external API response
        when(restTemplate.getForObject(eq("https://api.mfapi.in/mf"), eq(MutualFund[].class)))
            .thenReturn(mockResponse);

        Map<String, Object> schemeDetails = new HashMap<>();
        ArrayList<HashMap<String, String>> navData = new ArrayList<>();
        HashMap<String, String> navEntry = new HashMap<>();
        navEntry.put("date", "01-10-2024");
        navEntry.put("nav", "100.0");
        navData.add(navEntry);
        schemeDetails.put("data", navData);

        when(restTemplate.getForObject(any(String.class), eq(Map.class)))
            .thenReturn(schemeDetails);

        // Mock repository response for existing fund
        when(mutualFundRepository.findBySchemeCode("ABC123")).thenReturn(Arrays.asList(existingFund));

        // Act
        mutualFundService.fetchAndSaveMutualFunds();

        // Assert
        // Verify that save() was called once for the updated fund
        verify(mutualFundRepository, times(1)).save(any(MutualFund.class));
    }

    @Test
    public void testGetMutualFundsBySchemeName() {
        // Arrange
        String schemeName = "Test Scheme";
        List<MutualFund> expectedFunds = Arrays.asList(mockFund1, mockFund2);
        when(mutualFundRepository.findBySchemeNameContaining(schemeName)).thenReturn(expectedFunds);

        // Act
        List<MutualFund> actualFunds = mutualFundService.getMutualFundsBySchemeName(schemeName);

        // Assert
        assertEquals(expectedFunds.size(), actualFunds.size());
        assertTrue(actualFunds.contains(mockFund1));
        assertTrue(actualFunds.contains(mockFund2));
    }

    @Test
    public void testGetMutualFundsBySchemeType() {
        // Arrange
        String schemeType = "Equity";
        List<MutualFund> expectedFunds = Arrays.asList(mockFund1, mockFund2);
        when(mutualFundRepository.findBySchemeType(schemeType)).thenReturn(expectedFunds);

        // Act
        List<MutualFund> actualFunds = mutualFundService.getMutualFundsBySchemeType(schemeType);

        // Assert
        assertEquals(expectedFunds.size(), actualFunds.size());
        assertTrue(actualFunds.contains(mockFund1));
        assertTrue(actualFunds.contains(mockFund2));
    }

    @Test
    public void testGetAllMutualFunds() {
        // Arrange
        List<MutualFund> expectedFunds = Arrays.asList(mockFund1, mockFund2);
        when(mutualFundRepository.findAll()).thenReturn(expectedFunds);

        // Act
        List<MutualFund> actualFunds = mutualFundService.getAllMutualFunds();

        // Assert
        assertEquals(expectedFunds.size(), actualFunds.size());
        assertTrue(actualFunds.contains(mockFund1));
        assertTrue(actualFunds.contains(mockFund2));
    }
}


