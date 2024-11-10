package org.mymf.service.finsire.cas.datamodel;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetDetail
{
    @JsonProperty("asset_detail_id")
    private String assetDetailId;
    private String name;
    @JsonProperty("unit_price")
    private double unitPrice;
    private int ltv;
    @JsonProperty("available_units")
    private double availableUnits;
    @JsonProperty("total_price")
    private double totalPrice;
    private boolean sip;
    @JsonProperty("response_data")
    private ResponseDataDetail responseData;
    private List<Transaction> transactions;
}
