package com.mtvu.websocketserver.mocks;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationUserMocks {

    public static Authentication getAuth(String user, String token) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, token,
                        Collections.emptyList());

        JwtAuthenticationToken oa2Auth = mock(JwtAuthenticationToken.class);
        when(oa2Auth.getPrincipal()).thenReturn(user);
        when(oa2Auth.getCredentials()).thenReturn(token);
        when(oa2Auth.isAuthenticated()).thenReturn(true);

        return auth;
    }
}
