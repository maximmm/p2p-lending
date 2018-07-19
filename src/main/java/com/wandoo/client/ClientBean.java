package com.wandoo.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientBean {

    private String username;

    private String personalId;

    public static ClientBean from(Client client) {
        return new ClientBean(client.getUsername(), client.getPersonalId());
    }

}
