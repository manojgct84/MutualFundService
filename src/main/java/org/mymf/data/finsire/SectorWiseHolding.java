package org.mymf.data.finsire;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "sector_wise_holdings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SectorWiseHolding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Auto-generated ID for each record

    @Column(name = "scheme_Code")
    private String schemeCode;

    @Column(name = "as_on_date")
    private String asOnDate;  // Store the date

    @Column(name = "sector")
    private String sector;  // Sector name

    @Column(name = "holding_percentage")
    private BigDecimal holdingPerc;  // Holding percentage

    @Column(name = "market_value")
    private BigDecimal marketValue;  // Market value
}

