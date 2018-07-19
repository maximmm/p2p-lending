package com.wandoo.client.registration

import com.wandoo.client.Client
import com.wandoo.client.ClientRepository
import com.wandoo.core.security.authentication.AuthenticationService
import com.wandoo.core.validation.ValidationException
import com.wandoo.core.validation.ValidationResult
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

import javax.servlet.http.HttpServletResponse

class ClientRegistrationServiceSpec extends Specification {

    private static final String USERNAME = "username"
    private static final String PERSONAL_ID = "1234567890"
    private static final String PASSWORD = "password"
    private static final String ENCODED_PASSWORD = "encodedPassword"

    def repository = Mock(ClientRepository)
    def authenticationService = Mock(AuthenticationService)
    def validator = Mock(ClientRegistrationValidator)
    def passwordEncoder = Mock(PasswordEncoder)
    def response = Stub(HttpServletResponse)

    def service = new ClientRegistrationService(repository, authenticationService, validator, passwordEncoder, response)

    ClientRegistrationRequest request

    def setup() {
        request = prepareRequest()
    }

    def "Should successfully register new client"() {
        when:
        def result = service.register(request)

        then:
        1 * validator.validate(request)
        1 * repository.save(_ as Client) >> { arguments ->
            with (arguments[0] as Client) {
                username == USERNAME
                personalId == PERSONAL_ID
                password == ENCODED_PASSWORD
                investments == [] as Set
            }
            arguments[0]
        }
        1 * passwordEncoder.encode(request.getPassword()) >> ENCODED_PASSWORD
        1 * authenticationService.addAuthenticationToken(response, USERNAME)

        and:
        with (result) {
            username == USERNAME
            personalId == PERSONAL_ID
            password == ENCODED_PASSWORD
        }
    }

    def "Should throw validation exception"() {
        when:
        service.register(request)

        then:
        1 * validator.validate(request) >> { arguments ->
            with(arguments[0] as ClientRegistrationRequest) {
                username == USERNAME
                personalId == PERSONAL_ID
                password == PASSWORD
                passwordRepeated == PASSWORD
            }
            throw new ValidationException(Stub(ValidationResult))
        }
        0 * repository.save(_ as Client)
        0 * passwordEncoder.encode(_ as String)
        0 * authenticationService.addAuthenticationToken(response, _ as String)

        and:
        thrown(ValidationException)
    }

    def prepareRequest() {
        new ClientRegistrationRequest().with {
            username = USERNAME
            personalId = PERSONAL_ID
            password = PASSWORD
            passwordRepeated = PASSWORD
            it
        }
    }

}
