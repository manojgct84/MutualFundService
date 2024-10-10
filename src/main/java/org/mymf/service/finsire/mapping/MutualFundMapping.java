package org.mymf.service.finsire.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import org.mymf.data.finsire.MutualFundDetails;

public interface MutualFundMapping
{
    public void mapDetails (JsonNode dataNode);
}
