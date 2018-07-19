package com.wandoo.core.security.authentication

import com.wandoo.client.Client
import com.wandoo.client.ClientRepository
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static java.util.Optional.empty
import static java.util.Optional.of
import static org.springframework.http.HttpHeaders.AUTHORIZATION

class AuthenticationServiceSpec extends Specification {

    private static final String TOKEN = "dummyToken"
    private static final String USERNAME = "dummyUsername"

    def request = Mock(HttpServletRequest)
    def response = Mock(HttpServletResponse)

    def tokenProvider = Mock(TokenProvider)
    def clientRepository = Mock(ClientRepository)

    def service = new AuthenticationService(tokenProvider, clientRepository)

    def "Should return authenticated client"() {
        when:
        def result = service.getAuthentication(request)

        then:
        1 * request.getHeader(AUTHORIZATION) >> "Bearer $TOKEN"
        1 * tokenProvider.fetchUsername(TOKEN) >> of(USERNAME)
        1 * clientRepository.findByUsername(USERNAME) >> of(
                Stub(Client) {
                    getId() >> 1L
                    getUsername() >> USERNAME
                }
        )

        and:
        with(result.get()) {
            getPrincipal().getId() == 1L
            getUsername() == USERNAME
        }
    }

    def "Should not authenticate client if token is not presented"() {
        when:
        service.getAuthentication(request)

        then:
        1 * request.getHeader(AUTHORIZATION) >> null
        0 * tokenProvider.fetchUsername(TOKEN)
        0 * clientRepository.findByUsername(USERNAME)
    }

    def "Should not authenticate client if could not fetch username from token"() {
        when:
        service.getAuthentication(request)

        then:
        1 * request.getHeader(AUTHORIZATION) >> "Bearer $TOKEN"
        1 * tokenProvider.fetchUsername(TOKEN) >> empty()
        0 * clientRepository.findByUsername(USERNAME)
    }

    def "Should not authenticate client if could not find client by username"() {
        when:
        def result = service.getAuthentication(request)

        then:
        1 * request.getHeader(AUTHORIZATION) >> "Bearer $TOKEN"
        1 * tokenProvider.fetchUsername(TOKEN) >> of(USERNAME)
        1 * clientRepository.findByUsername(USERNAME) >> empty()

        and:
        !result.isPresent()
    }

    def "Should add authentication header"() {
        when:
        service.addAuthenticationToken(response, USERNAME)

        then:
        1 * tokenProvider.generateToken(USERNAME) >> TOKEN
        1 * response.addHeader(AUTHORIZATION, "Bearer $TOKEN")
    }
}
