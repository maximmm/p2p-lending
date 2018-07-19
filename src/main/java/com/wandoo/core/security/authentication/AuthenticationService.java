package com.wandoo.core.security.authentication;

import com.wandoo.client.ClientRepository;
import com.wandoo.core.security.ClientPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.removeStart;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final String BEARER = "Bearer ";

    private final TokenProvider tokenProvider;
    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public Optional<Authentication> getAuthentication(HttpServletRequest request) {
        return ofNullable(request.getHeader(AUTHORIZATION))
                .map(token -> removeStart(token, BEARER))
                .flatMap(tokenProvider::fetchUsername)
                .flatMap(clientRepository::findByUsername)
                .map(ClientPrincipal::fromClient);
    }

    public void addAuthenticationToken(HttpServletResponse response, String username) {
        String token = tokenProvider.generateToken(username);
        response.addHeader(AUTHORIZATION, BEARER.concat(token));
    }
}
