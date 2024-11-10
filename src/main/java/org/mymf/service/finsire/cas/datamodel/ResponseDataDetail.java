package org.mymf.service.finsire.cas.datamodel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDataDetail
{
    private String amc;
    private String nav;
    private String isin;
    private String folio;
    private String scheme;
    private String amcName;
    private String isDemat;
    private String rtaCode;
    private String assetType;
    private String costValue;
    private String marketValue;
    private String lastTrxnDate;
    private String closingBalance;
}
