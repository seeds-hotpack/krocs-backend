package com.hotpack.krocs.global.config;

import com.hotpack.krocs.global.security.auth.TokenAuthenticationProvider;
import com.hotpack.krocs.global.security.filter.TokenAuthFilter;
import com.hotpack.krocs.global.security.handler.RestAccessDeniedHandler;
import com.hotpack.krocs.global.security.handler.RestAuthenticationEntryPoint;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenAuthenticationProvider tokenAuthenticationProvider;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(tokenAuthenticationProvider);
    }

    @Bean
    public TokenAuthFilter tokenAuthFilter(AuthenticationManager authenticationManager) {
        return new TokenAuthFilter(authenticationManager, authenticationEntryPoint);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenAuthFilter tokenAuthFilter) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**", "/v3/api-docs/**",
                                "/actuator/health",
                                "/health", "/error"
                        ).permitAll()
                        .requestMatchers("/auth/me").authenticated()
                        .anyRequest().authenticated()
                )
                .httpBasic(AbstractHttpConfigurer::disable);

        http.addFilterBefore(tokenAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(
                "https://www.krocs.life",
                "http://localhost:3000"
        ));
        config.addAllowedOriginPattern("*");
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*") );
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
