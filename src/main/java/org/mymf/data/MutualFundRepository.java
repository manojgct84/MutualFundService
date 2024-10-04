package org.mymf.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * The {@code MutualFundRepository} interface is responsible for interacting with the
 * database to perform operations on mutual fund data. It extends the {@code JpaRepository}
 * interface, which provides standard methods for saving, updating, deleting, and retrieving
 * mutual fund records.
 *
 * <p>In addition to the basic CRUD operations, this interface defines custom methods for
 * finding mutual funds by scheme name and scheme type.</p>
 *
 * <p>It is a part of Spring Data JPA, which automatically provides the implementation
 * for these methods.</p>
 *
 * @author [Manojkumar]
 * @version 1.0
 * @since 2024-10-03
 */
public interface MutualFundRepository extends JpaRepository<MutualFund, Long>
{

    /**
     * Finds mutual funds by a partial match of the scheme name.
     *
     * @param schemeName The partial scheme name to search for.
     * @return A list of mutual funds that contain the provided scheme name.
     */
    List<MutualFund> findBySchemeNameContaining (String schemeName);

    /**
     * Finds mutual funds by scheme type.
     *
     * @param schemeType The type of scheme to search for.
     * @return A list of mutual funds that match the given scheme type.
     */
    List<MutualFund> findBySchemeType (String schemeType);

    /**
     * Finds mutual funds by scheme code.
     *
     * @param schemeCode The code of scheme to search for.
     * @return A list of mutual funds that match the given scheme code.
     */
    List<MutualFund> findBySchemeCode (String schemeCode);
}

