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

import java.math.BigDecimal;

@Entity
@Table(name = "discrete_returns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscreteReturns
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_code")
    private String schemeCode;

    @Column(name = "year")
    private String year;

    @Column(name = "Q1")
    private BigDecimal q1;  // Quarterly return for Q1

    @Column(name = "Q2")
    private BigDecimal q2;  // Quarterly return for Q2

    @Column(name = "H1")
    private BigDecimal h1;  // Half-year return for H1

    @Column(name = "Q3")
    private BigDecimal q3;  // Quarterly return for Q3

    @Column(name = "Q4")
    private BigDecimal q4;  // Quarterly return for Q4

    @Column(name = "H2")
    private BigDecimal h2;  // Half-year return for H2

    @Column(name = "annual")
    private BigDecimal annual;  // Annual return

    @Column(name = "YTD")
    private BigDecimal ytd;  // Year-to-date return, if present
}

