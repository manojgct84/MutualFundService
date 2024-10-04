package org.mymf.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mymf.data.MutualFund;
import org.mymf.data.MutualFundRepository;
import org.mymf.data.NAVHistory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class MutualFundServiceTest
{

    @InjectMocks
    private MutualFundService mutualFundService;

    @MockBean
    private MutualFundRepository mutualFundRepository;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp ()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFetchAndSaveMutualFunds_withNewFunds ()
    {
        // Arrange
        MutualFund fund1 = new MutualFund();
        fund1.setSchemeCode("ABC123");
        fund1.setUpdatedAt(LocalDate.now().atStartOfDay());

        MutualFund fund2 = new MutualFund();
        fund2.setSchemeCode("XYZ456");
        fund2.setUpdatedAt(LocalDate.now().atStartOfDay());

        MutualFund[] mockResponse = {fund1, fund2};

        when(restTemplate.getForObject(any(String.class), eq(MutualFund[].class))).thenReturn(mockResponse);
        when(mutualFundRepository.findBySchemeCode("ABC123")).thenReturn(new ArrayList<>());
        when(mutualFundRepository.findBySchemeCode("XYZ456")).thenReturn(new ArrayList<>());

        // Act
        mutualFundService.fetchAndSaveMutualFunds();

        // Assert
        verify(mutualFundRepository, times(2)).save(any(MutualFund.class));
    }

    @Test
    public void testFetchAndSaveMutualFunds_withExistingFunds ()
    {
        // Arrange
        MutualFund existingFund = new MutualFund();
        existingFund.setSchemeCode("ABC123");
        existingFund.setUpdatedAt(LocalDate.now().atStartOfDay());

        MutualFund newFund = new MutualFund();
        newFund.setSchemeCode("ABC123");
        newFund.setNavHistory(new ArrayList<>((Collection) new NAVHistory(LocalDate.parse("10-08-2024"), 100.00)));
        newFund.setUpdatedAt(LocalDate.now().atStartOfDay());

        MutualFund[] mockResponse = {newFund};

        List<MutualFund> existingFunds = new ArrayList<>();
        existingFunds.add(existingFund);

        when(restTemplate.getForObject(any(String.class), eq(MutualFund[].class))).thenReturn(mockResponse);
        when(mutualFundRepository.findBySchemeCode("ABC123")).thenReturn(new ArrayList<>());

        // Act
        mutualFundService.fetchAndSaveMutualFunds();

        // Assert
        verify(mutualFundRepository, times(1)).save(any(MutualFund.class));
        ArgumentCaptor<MutualFund> capturedFund = ArgumentCaptor.forClass(MutualFund.class);
        verify(mutualFundRepository).save(capturedFund.capture());
        assertEquals(100.0, capturedFund.getAllValues().get(0));
    }

    @Test
    public void testGetMutualFundsBySchemeName ()
    {
        // Arrange
        MutualFund fund = new MutualFund();
        fund.setSchemeName("Equity Fund");

        List<MutualFund> funds = new ArrayList<>();
        funds.add(fund);

        when(mutualFundRepository.findBySchemeNameContaining("Equity")).thenReturn(funds);

        // Act
        List<MutualFund> result = mutualFundService.getMutualFundsBySchemeName("Equity");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Equity Fund", result.get(0).getSchemeName());
    }

    @Test
    public void testGetMutualFundsBySchemeType ()
    {
        // Arrange
        MutualFund fund = new MutualFund();
        fund.setSchemeType("Equity");

        List<MutualFund> funds = new ArrayList<>();
        funds.add(fund);

        when(mutualFundRepository.findBySchemeType("Equity")).thenReturn(funds);

        // Act
        List<MutualFund> result = mutualFundService.getMutualFundsBySchemeType("Equity");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Equity", result.get(0).getSchemeType());
    }
}

