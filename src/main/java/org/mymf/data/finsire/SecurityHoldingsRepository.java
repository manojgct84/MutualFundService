package org.mymf.data.finsire;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityHoldingsRepository extends JpaRepository<SecurityHolding, Long>
{

    // Find security holdings by the mutual fund's scheme code
    List<SecurityHolding> findBySchemeCode(String schemeCode);
}

