package org.mymf.data.finsire.risk;


import java.math.BigDecimal;

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

@Entity
@Table(name = "categorylevel_risk")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryLevelRisk
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_code")
    private String schemeCode;

    @Column(name = "annual_sd")
    private BigDecimal annualSd;

    @Column(name = "beta")
    private BigDecimal beta;

    @Column(name = "sharpe_ratio")
    private BigDecimal sharpeRatio;

    @Column(name = "jensens_alpha")
    private BigDecimal jensensAlpha;

    @Column(name = "asset_sub_category")
    private String assetSubCategory;

    @Column(name = "plan_name")
    private String planName;

    @Column(name = "range")
    private String range;


}
