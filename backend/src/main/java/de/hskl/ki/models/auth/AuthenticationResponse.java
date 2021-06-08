package de.hskl.ki.models.auth;

import java.util.Date;

public class AuthenticationResponse {
    private final String accessToken;
    private final Date tokenExpiration;
    private final String tokenType = "Bearer";

    public AuthenticationResponse(String accessToken, Date tokenExpiration) {
        this.accessToken = accessToken;
        this.tokenExpiration = tokenExpiration;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Date getTokenExpiration() {
        return tokenExpiration;
    }

    public String getTokenType() {
        return tokenType;
    }
}
