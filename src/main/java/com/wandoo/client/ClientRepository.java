package com.wandoo.client;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClientRepository extends CrudRepository<Client, Long> {

    Optional<Client> findByUsername(String username);

    boolean existsByUsernameOrPersonalId(String username, String personalId);

}
