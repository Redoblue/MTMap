package com.hltc.mtmap.bean;

/**
 * Created by Redoblue on 2015/4/16.
 */
public class TokenInfo {

    private String token;
    private long expiresIn = 0;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
