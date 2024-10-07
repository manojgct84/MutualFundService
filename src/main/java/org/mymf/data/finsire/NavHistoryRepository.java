package org.mymf.data.finsire;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NavHistoryRepository extends JpaRepository<NavHistoryDetails, Long> {

    // Find NAV history records by the mutual fund's scheme code
    List<NavHistoryDetails> findBySchemeCode(String schemeCode);
}

