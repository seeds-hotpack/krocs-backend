package com.hotpack.krocs.global.security.auth;

import com.hotpack.krocs.domain.auth.dto.UserSession;
import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class TokenAuthentication extends AbstractAuthenticationToken {

    private final Object principal;
    private final String credentials; // unused

    public static TokenAuthentication unauthenticated(String rawToken) {
        return new TokenAuthentication(rawToken, null, null);
    }

    public static TokenAuthentication authenticated(UserSession session,
        Collection<? extends GrantedAuthority> authorities) {
        return new TokenAuthentication(session, null, authorities);
    }

    private TokenAuthentication(Object principal, String credentials,
        Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(authorities != null);
    }

    @Override
    public String getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}

