package com.wandoo.client.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.wandoo.client.ClientBean.from;
import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class ClientRegistrationController {

    private final ClientRegistrationService clientRegistrationService;

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@Valid @RequestBody ClientRegistrationRequest request) {
        return ok(from(clientRegistrationService.register(request)));
    }

}
