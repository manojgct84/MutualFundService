package org.mymf.data.finsire;


import java.time.LocalDate;

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

@Entity
@Table(name = "nav_history_details")
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NavHistoryDetails
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_code")
    private String schemeCode;

    @Column(name = "nav_date")
    private LocalDate navDate;

    @Column(name = "nav_value")
    private Double navValue;

}

