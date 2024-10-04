package org.mymf.contoller;

import org.mymf.data.MutualFund;
import org.mymf.service.MutualFundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mutualfunds")
public class MutualFundController {

    @Autowired
    private MutualFundService mutualFundService;

    @GetMapping("/scheme-name/{name}")
    public List<MutualFund> getBySchemeName(@PathVariable String name) {
        return mutualFundService.getMutualFundsBySchemeName(name);
    }

    @GetMapping("/scheme-type/{type}")
    public List<MutualFund> getBySchemeType(@PathVariable String type) {
        return mutualFundService.getMutualFundsBySchemeType(type);
    }
}

