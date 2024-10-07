package org.mymf.data.finsire;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectorWiseHoldingRepository extends JpaRepository<SectorWiseHolding, Long> {

    // Find sector-wise holdings by the mutual fund's scheme code
    List<SectorWiseHolding> findBySchemeCode(String schemeCode);
}

