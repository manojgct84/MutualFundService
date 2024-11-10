package org.mymf.userprofile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mymf.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

@Service
public class AssetSelectionService
{

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AssetSelectionRequestRepository requestRepository;

    @Autowired
    private final TokenService tokenService;

    public AssetSelectionService (final TokenService tokenService, final RestTemplate restTemplate)
    {
        this.tokenService = tokenService;
        this.restTemplate = restTemplate;
    }

    public AssetSelectionRequest saveRequest (AssetSelectionRequest request)
    {
        String url = "https://sandbox.dpiwealth.com/v1/api/assets_selection";

        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user", Map.of("name", request.getUser().getName(), "phone",
            request.getUser().getPhone(), "pan", request.getUser().getPan()));
        requestBody.put("assets", List.of("Salary", "Mutual Funds", "Vehicle"));
        requestBody.put("kyc_done", true);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, buildHeaders());

        // Make the request
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String finservID = (String) response.getBody().get("finsire_id");
            request.getUser().setFinservID(finservID);
        }

        return requestRepository.save(request);
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
}

