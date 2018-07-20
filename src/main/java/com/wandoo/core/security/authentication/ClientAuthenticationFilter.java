package com.wandoo.core.security.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RequiredArgsConstructor
public class ClientAuthenticationFilter extends GenericFilterBean {

    private final AuthenticationService authenticationService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        getContext().setAuthentication(
                authenticationService.getAuthentication((HttpServletRequest) request)
                        .orElse(null)
        );

        filterChain.doFilter(request, response);
    }

}