package org.mymf.data;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MutualFundRepository extends JpaRepository<MutualFund, Long> {
    List<MutualFund> findBySchemeNameContaining(String schemeName);

    List<MutualFund> findBySchemeType(String schemeType);

    List<MutualFund> findBySchemeCode(String schemeCode);
}

