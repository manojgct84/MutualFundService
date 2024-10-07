package org.mymf.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class FinSireOAuthTokenInit
{
    @Value("${finsire.lenderSecret}")
    private String lenderSecret;
    @Value("${finsire.distributorSecret}")
    private String distributorSecret;

}
