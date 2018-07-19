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
                .orElseThrow(clientNotFound(username));
    }

    private static Supplier<UsernameNotFoundException> clientNotFound(String username) {
        return () -> new UsernameNotFoundException(format("Client '%s' was not found", username));
    }

}
