package com.wandoo.client.registration;

import com.wandoo.client.ClientRepository;
import com.wandoo.core.validation.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.wandoo.core.validation.Validations.*;
import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class ClientRegistrationValidator {

    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public void validate(ClientRegistrationRequest request) {
        ValidationResult validationResult = new ValidationResult();

        lengthIn(5, 20).validate(request.getUsername(), "username", validationResult);
        length(10).validate(request.getPersonalId(), "personalId", validationResult);
        lengthIn(6, 30).and(match(request.getPasswordRepeated(), "passwordRepeated"))
                .validate(request.getPassword(), "password", validationResult);

        validationResult.throwIfFailed();

        validateExistence(request, validationResult);
    }

    private void validateExistence(ClientRegistrationRequest request, ValidationResult validationResult) {
        String username = request.getUsername();
        String personalId = request.getPersonalId();

        if (clientRepository.existsByUsernameOrPersonalId(username, personalId)) {
            validationResult.addErrorAndThrow("client",
                    format("Client with username '%s' or personalId '%s' already exists.", username, personalId));
        }
    }

}
