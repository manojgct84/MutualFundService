package org.mymf.contoller;

import java.util.Map;

import org.mymf.userprofile.AssetSelectionRequest;
import org.mymf.userprofile.AssetSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("v1/api")
public class AssetSelectionController
{

    @Autowired
    private AssetSelectionService service;

    @PostMapping("/assets_selection")
    public AssetSelectionRequest createAssetSelectionRequest (@RequestBody AssetSelectionRequest request)
    {
        // Save the initial request
        return service.saveRequest(request);
    }
}
