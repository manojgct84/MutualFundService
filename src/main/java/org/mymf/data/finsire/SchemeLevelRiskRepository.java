package org.mymf.data.finsire;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchemeLevelRiskRepository extends JpaRepository<DataMetrics, Long> {

    // Find scheme-level risks by the mutual fund's scheme code
    List<DataMetrics> findBySchemeCode(String schemeCode);
}

