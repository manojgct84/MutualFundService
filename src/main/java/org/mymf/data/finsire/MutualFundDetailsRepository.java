package org.mymf.data.finsire;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MutualFundDetailsRepository extends JpaRepository<MutualFundDetails, Long> {

    // Find a mutual fund's details by its scheme code
    MutualFundDetails findBySchemeCode(String schemeCode);
}

