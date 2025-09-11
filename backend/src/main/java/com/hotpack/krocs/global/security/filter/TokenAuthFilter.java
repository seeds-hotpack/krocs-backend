package com.hotpack.krocs.global.security.filter;

import com.hotpack.krocs.global.security.auth.TokenAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class TokenAuthFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public TokenAuthFilter(
        AuthenticationManager authenticationManager,
        AuthenticationEntryPoint authenticationEntryPoint
    ) {
        this.authenticationManager = authenticationManager;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        var current = SecurityContextHolder.getContext().getAuthentication();
        if (current != null && current.isAuthenticated()
            && !(current instanceof AnonymousAuthenticationToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            resolveToken(request).ifPresent(token -> {
                if (log.isDebugEnabled()) {
                    String masked = token.length() <= 8 ? "***"
                        : token.substring(0, 4) + "***" + token.substring(token.length() - 4);
                    log.debug("Authenticating with bearer token: {}", masked);
                }
                var authRequest = TokenAuthentication.unauthenticated(token);
                var authResult = authenticationManager.authenticate(authRequest);
                SecurityContextHolder.getContext().setAuthentication(authResult);
            });
        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, ex);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("AUTH-TOKEN".equals(cookie.getName()) && StringUtils.hasText(
                    cookie.getValue())) {
                    return Optional.of(cookie.getValue());
                }
            }
        } else {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (header != null) {
                String value = header.trim();
                if (value.toLowerCase().startsWith("bearer ")) {
                    String token = value.substring(7).trim();
                    return StringUtils.hasText(token) ? Optional.of(token) : Optional.empty();
                }
            }
        }

        return Optional.empty();
    }
}
