package org.mymf.data.finsire;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscreteReturnsRepository extends JpaRepository<DiscreteReturns, Long> {
    // Find Discrete Returns by scheme code
    List<DiscreteReturns> findBySchemeCode(String schemeCode);
}

