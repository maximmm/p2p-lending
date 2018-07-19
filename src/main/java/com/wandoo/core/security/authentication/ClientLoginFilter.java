package com.wandoo.core.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ClientLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final AuthenticationService authenticationService;

    public ClientLoginFilter(String loginUrl, AuthenticationManager authenticationManager,
                             AuthenticationService authenticationService) {
        super(loginUrl);
        setAuthenticationManager(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication) {
        authenticationService.addAuthenticationToken(response, authentication.getName());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        ClientCredentials credentials = getClientCredentials(request);
        return getAuthenticationManager().authenticate(fromCredentials(credentials));
    }

    private ClientCredentials getClientCredentials(HttpServletRequest request) throws IOException {
        return new ObjectMapper().readValue(request.getInputStream(), ClientCredentials.class);
    }

    private Authentication fromCredentials(ClientCredentials credentials) {
        return new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword());
    }
}
