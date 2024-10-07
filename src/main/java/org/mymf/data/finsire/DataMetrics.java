package org.mymf.data.finsire;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.mymf.data.finsire.TimeRangeMetrics;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "data_metrics")
public class DataMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_code")
    private String schemeCode;

    @OneToMany(mappedBy = "dataMetrics", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeRangeMetrics> timeRangeMetrics;  // List of metrics for various time ranges
}

