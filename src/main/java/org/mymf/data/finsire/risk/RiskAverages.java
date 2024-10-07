package org.mymf.data.finsire.risk;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public
class RiskAverages {
    private BigDecimal annualSd;       // Maps to "annual_sd"
    private BigDecimal beta;
    private BigDecimal sharpeRatio;
    private BigDecimal jensensAlpha;
}
