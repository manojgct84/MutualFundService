package org.mymf.service.finsire.cas.datamodel;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ToString
public class Transaction
{
    private double tax;
    private double sttTax;
    private double totalTax;
    private String trxnDate;
    private String trxnDesc;
    private String trxnMode;
    private double stampDuty;
    private double trxnUnits;
    private String checkDigit;
    private String postedDate;
    private double trxnAmount;
    private double trxnCharge;
    private String trxnTypeFlag;
    private double purchasePrice;
}
