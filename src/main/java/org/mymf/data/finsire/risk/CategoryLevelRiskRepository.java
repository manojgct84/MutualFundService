package org.mymf.data.finsire.risk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryLevelRiskRepository extends JpaRepository<CategoryLevelRisk, Long> {

    // Find category-level risks by the mutual fund's scheme code
    List<CategoryLevelRisk> findBySchemeCode(String schemeCode);
}

