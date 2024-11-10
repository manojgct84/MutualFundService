package org.mymf.data.finsire;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "security_holdings")
@Setter
@Getter
@ToString
public class SecurityHolding
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_Code")
    private String schemeCode;

    @Column(name = "market_value")
    private Double marketValue;

    @Column(name = "as_on_date")
    private String asOnDate;

    @Column(name = "security")
    private String security;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "coupon")
    private Double coupon;

    @Column(name = "agency")
    private String agency;

    @Column(name = "holding_perc")
    private Double holdingPercentage;

    @Column(name = "mkt_cap_category")
    private String marketCapCategory;

    @Column(name = "rating")
    private String rating;

    @Column(name = "sector")
    private String sector;

    @Column(name = "equiv_rating")
    private String equivRating;

    @Column(name = "isin_security")
    private String isinSecurity;
}

