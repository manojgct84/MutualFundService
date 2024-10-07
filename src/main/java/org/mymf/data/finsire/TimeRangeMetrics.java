package org.mymf.data.finsire;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "time_range_metrics")
public class TimeRangeMetrics
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "time_range")
    private String timeRange;  // E.g., 1M, 3M, 6M, 1Y, 3Y, etc.

    @Column(name = "annual_sd")
    private double annualSd;

    @Column(name = "beta")
    private double beta;

    @Column(name = "sharpe_ratio")
    private double sharpeRatio;

    @Column(name = "treynor_ratio")
    private double treynorRatio;

    @Column(name = "jensens_alpha")
    private double jensensAlpha;

    @Column(name = "information_ratio")
    private double informationRatio;

    @Column(name = "sortino_ratio")
    private double sortinoRatio;

    @Column(name = "r_squared")
    private double rSquared;

    @ManyToOne
    @JoinColumn(name = "data_metrics_id")
    private DataMetrics dataMetrics;  // Reference back to DataMetrics
}
