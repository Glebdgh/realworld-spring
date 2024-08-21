package com.realworld.springmongo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthenticationWebFilter webFilter, EndpointsSecurityConfig endpointsConfig) {
        var authorizeExchange = http.authorizeExchange();
        return endpointsConfig.apply(authorizeExchange)
                .and()
                .addFilterAt(webFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic().disable()
                .cors().configurationSource(corsConfigurationSource()) // Активуємо CORS
                .and()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4100", "http://node-app-service.default.svc.cluster.local:4100"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    EndpointsSecurityConfig endpointsConfig() {
        return http -> http
                .pathMatchers(HttpMethod.POST, "/api/users", "/api/users/login").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/profiles/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/articles/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/tags/**").permitAll()
                .anyExchange().authenticated();
    }

    @FunctionalInterface
    public interface EndpointsSecurityConfig {
        ServerHttpSecurity.AuthorizeExchangeSpec apply(ServerHttpSecurity.AuthorizeExchangeSpec http);
    }
}

