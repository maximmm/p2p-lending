package com.wandoo.core.security;

import com.wandoo.client.Client;
import com.wandoo.client.ClientId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;

@Getter
@Builder
public class ClientPrincipal implements Authentication, UserDetails {

    private ClientId clientId;

    private Collection<GrantedAuthority> authorities;

    private String password;

    private String username;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;

    private boolean enabled;

    private Object credentials;

    private Object details;

    private Object principal;

    private String name;

    @Setter
    private boolean authenticated;

    public static ClientPrincipal fromClient(Client client) {
        return ClientPrincipal.builder()
                .authorities(newArrayList(new SimpleGrantedAuthority("CLIENT")))
                .password(client.getPassword())
                .username(client.getUsername())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .credentials(client.getPassword())
                .details(client)
                .principal(new ClientId(client.getId()))
                .name(client.getUsername())
                .authenticated(true)
                .build();
    }
}
