package org.mymf.service;

import java.util.Base64;

import org.mymf.config.FinSireOAuthTokenInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    private final FinSireOAuthTokenInit finSireOAuthTokenInit;

    @Autowired
    public TokenService(FinSireOAuthTokenInit finSireOAuthTokenInit) {
        this.finSireOAuthTokenInit = finSireOAuthTokenInit;
    }

    public String getEncryptedToken() {
        String token = finSireOAuthTokenInit.getLenderSecret() + ":" + finSireOAuthTokenInit.getDistributorSecret();
        return Base64.getUrlEncoder().encodeToString(token.getBytes());
    }
}

