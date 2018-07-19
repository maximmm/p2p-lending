package com.wandoo.core.security

import com.wandoo.client.Client
import com.wandoo.client.ClientRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import spock.lang.Specification

import static java.util.Optional.empty
import static java.util.Optional.of

class ClientPrincipalServiceSpec extends Specification {

    private static final String USERNAME = "Exists"
    private static final String USERNAME_NOT_EXISTS = "NotExists"

    def repository = Mock(ClientRepository)

    def service = new ClientPrincipalService(repository)

    def "Should load correct client"() {
        when:
        def result = service.loadUserByUsername(USERNAME)

        then:
        1 * repository.findByUsername(USERNAME) >> of(
            Stub(Client) {
                getId() >> 1L
                getUsername() >> USERNAME
            }
        )

        and:
        with(result) {
            getPrincipal().getId() == 1L
            getUsername() == USERNAME
        }
    }

    def "Should throw UsernameNotFoundException when client was not found"() {
        when:
        service.loadUserByUsername(USERNAME_NOT_EXISTS)

        then:
        1 * repository.findByUsername(USERNAME_NOT_EXISTS) >> empty()

        and:
        def exception = thrown(UsernameNotFoundException)
        exception.message == "Client 'NotExists' was not found"
    }

}
