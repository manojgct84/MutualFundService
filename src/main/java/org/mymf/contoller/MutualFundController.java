package org.mymf.contoller;

import java.util.List;

import org.mymf.data.MutualFund;
import org.mymf.service.MutualFundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The {@code MutualFundController} class provides REST API endpoints for interacting
 * with the mutual fund data in the system. It handles incoming HTTP requests and delegates
 * the processing to the {@code MutualFundService} to fetch the necessary mutual fund information.
 *
 * <p>It exposes endpoints to retrieve mutual funds by scheme name and scheme type,
 * allowing clients to query data based on these criteria.</p>
 *
 * <h2>Endpoints:</h2>
 * <ul>
 *   <li>{@code GET /api/mutualfunds/schemeName/{schemeName}}
 *   - Retrieves mutual funds that match the provided scheme name.</li>
 *   <li>{@code GET /api/mutualfunds/schemeType/{schemeType}}
 *   - Retrieves mutual funds that match the provided scheme type.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * The {@code MutualFundController} is a Spring MVC {@code @RestController} that serves
 * HTTP requests and returns JSON responses.
 *
 * <pre>
 * {@code
 * @RestController
 * @RequestMapping("/api/mutualfunds")
 * public class MutualFundController {
 *    // API methods
 * }
 * }
 * </pre>
 *
 * <h2>Dependencies:</h2>
 * <ul>
 * <li>{@code MutualFundService} - The service used to interact with the data
 * layer for mutual funds.</li>
 * </ul>
 *
 * <h2>Example:</h2>
 * <pre>
 * {@code
 * @GetMapping("/schemeName/{schemeName}")
 * public List<MutualFund> getMutualFundsBySchemeName(@PathVariable String schemeName) {
 *     return mutualFundService.getMutualFundsBySchemeName(schemeName);
 * }
 * }
 * </pre>
 *
 * @author [Manojkumar]
 * @version 1.0
 * @since 2024-10-03
 */
@RestController
@RequestMapping("/api/mutualfunds")
public class MutualFundController
{

    @Autowired
    private MutualFundService mutualFundService;

    /**
     * Retrieves mutual funds by scheme name.
     *
     * <p>This endpoint allows clients to retrieve mutual fund details
     * that match the given {@code schemeName}. The results will include
     * mutual funds whose names contain the provided string.</p>
     *
     * @param schemeName The scheme name or partial name to search for.
     * @return A list of mutual funds matching the scheme name.
     */
    @GetMapping("/schemeName/{schemeName}")
    public List<MutualFund> getMutualFundsBySchemeName (@PathVariable String schemeName)
    {
        return mutualFundService.getMutualFundsBySchemeName(schemeName);
    }

    /**
     * Retrieves mutual funds by scheme type.
     *
     * <p>This endpoint allows clients to retrieve mutual fund details
     * that match the given {@code schemeType} (e.g., "Equity", "Debt").</p>
     *
     * @param schemeType The type of mutual fund to search for.
     * @return A list of mutual funds matching the scheme type.
     */
    @GetMapping("/schemeType/{schemeType}")
    public List<MutualFund> getMutualFundsBySchemeType (@PathVariable String schemeType)
    {
        return mutualFundService.getMutualFundsBySchemeType(schemeType);
    }
}


