package com.mtvu.websocketserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author mvu
 * @project chat-socket
 **/
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeRequests(authorize -> authorize
                .requestMatchers("/websocket", "/websocket/**").permitAll()
                .anyRequest().denyAll());
        return http.build();
    }


    @Bean
    public AuthenticationProvider getJwsAuthenticationProvider(JwtDecoder decoder) {
        var provider = new JwtAuthenticationProvider(decoder);
        var converter = new JwtAuthenticationConverter();
        provider.setJwtAuthenticationConverter(converter);
        return provider;
    }
}
