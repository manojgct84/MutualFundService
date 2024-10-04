package org.mymf.data;


import java.time.LocalDate;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NAVHistory
{
    private LocalDate navDate;
    private double navValue;

}

