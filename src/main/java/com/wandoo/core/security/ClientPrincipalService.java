package com.wandoo.core.security;

import com.wandoo.client.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class ClientPrincipalService implements UserDetailsService {

    private final ClientRepository clientRepository;

    @Override
    @Transactional(readOnly = true)
    public ClientPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        return clientRepository.findByUsername(username)
                .map(ClientPrincipal::fromClient)
                .orElseThrow(userNotFound(username));
    }

    private static Supplier<UsernameNotFoundException> userNotFound(String username) {
        return () -> new UsernameNotFoundException(format("User '%s' not found", username));
    }

}
