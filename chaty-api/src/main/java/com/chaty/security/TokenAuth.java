package com.chaty.security;

import lombok.Data;

@Data
public class TokenAuth<T> {

    public TokenAuth(String authToken) {
        this.authToken = authToken;
    }

    private String authToken;

    private T auth;

}
