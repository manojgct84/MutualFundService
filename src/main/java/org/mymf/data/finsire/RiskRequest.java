package org.mymf.data.finsire;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiskRequest
{
    private String assetSubCategory;
    private String planName;
    private String from;
    private String ranges;
}
