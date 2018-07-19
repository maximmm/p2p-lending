package com.wandoo.client.registration;

import com.wandoo.client.Client;
import com.wandoo.client.ClientRepository;
import com.wandoo.core.security.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class ClientRegistrationService {

    private final ClientRepository clientRepository;
    private final AuthenticationService authenticationService;
    private final ClientRegistrationValidator registrationValidator;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletResponse response;

    @Transactional
    public Client register(ClientRegistrationRequest request) {
        registrationValidator.validate(request);

        Client client = clientRepository.save(createClient(request));
        authenticationService.addAuthenticationToken(response, client.getUsername());

        return client;
    }

    private Client createClient(ClientRegistrationRequest request) {
        Client client = new Client();
        client.setUsername(request.getUsername());
        client.setPersonalId(request.getPersonalId());
        client.setPassword(passwordEncoder.encode(request.getPassword()));
        return client;
    }

}
