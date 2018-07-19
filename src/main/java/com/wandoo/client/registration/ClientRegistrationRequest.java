package com.wandoo.client.registration;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ClientRegistrationRequest {

    @NotBlank
    @Size(min = 5, max = 20)
    private String username;

    @NotBlank
    @Size(min = 10, max = 10)
    private String personalId;

    @NotBlank
    @Size(min = 6, max = 30)
    private String password;

    @NotBlank
    @Size(min = 6, max = 30)
    private String passwordRepeated;

}
