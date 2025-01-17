package com.chaty.security;

import org.springframework.security.core.AuthenticationException;

public interface TokenAuthService {

    TokenAuth<?> authenticate(String token) throws AuthenticationException;

}
