package com.wandoo.client.registration

import com.wandoo.client.ClientRepository
import com.wandoo.core.validation.ValidationException
import spock.lang.Specification
import spock.lang.Unroll

import static org.apache.commons.lang3.StringUtils.EMPTY

class ClientRegistrationValidatorSpec extends Specification {

    private static final String USERNAME = "username"
    private static final String PERSONAL_ID = "1234567890"
    private static final String PASSWORD = "password"

    def repository = Mock(ClientRepository)
    def validator = new ClientRegistrationValidator(repository)

    ClientRegistrationRequest request

    def setup() {
        request = prepareRegistrationRequest()
    }

    def "Should validate registration request without throwing validation exception"(){
        when:
        validator.validate(request)

        then:
        1 * repository.existsByUsernameOrPersonalId(USERNAME, PERSONAL_ID)

        and:
        noExceptionThrown()
    }

    @Unroll
    def "Should have '#errorMessages' error messages for username '#username'"() {
        when:
        validator.validate(request)

        then:
        request.getUsername() >> username

        and:
        0 * repository.existsByUsernameOrPersonalId(_ as String, _ as String)

        and:
        def exception = thrown(ValidationException)
        verify(exception.getValidationResult(), "username", errorsCount, errorMessages)

        where:
        username                    | errorsCount | errorMessages
        null                        | 2           | ["Should not be blank.", "Should be between 5 and 20 chars long."]
        EMPTY                       | 2           | ["Should not be blank.", "Should be between 5 and 20 chars long."]
        "       "                   | 2           | ["Should not be blank.", "Should be between 5 and 20 chars long."]
        "123"                       | 1           | ["Should be between 5 and 20 chars long."]
        "MORE_THAN_20_CHARACTERS"   | 1           | ["Should be between 5 and 20 chars long."]
    }

    @Unroll
    def "Should have '#errorMessages' error messages for personalId '#personalId'"() {
        when:
        validator.validate(request)

        then:
        request.getPersonalId() >> personalId

        and:
        0 * repository.existsByUsernameOrPersonalId(_ as String, _ as String)

        and:
        def exception = thrown(ValidationException)

        and:
        verify(exception.getValidationResult(), "personalId", errorsCount, errorMessages)


        where:
        personalId      | errorsCount | errorMessages
        null            | 2           | ["Should not be blank.", "Should be 10 chars long."]
        EMPTY           | 2           | ["Should not be blank.", "Should be 10 chars long."]
        "           "   | 2           | ["Should not be blank.", "Should be 10 chars long."]
        "123"           | 1           | ["Should be 10 chars long."]
        "123456789 "    | 1           | ["Should be 10 chars long."]
    }

    @Unroll
    def "Should have '#errorMessages' error messages for password '#password' and passwordRepeated '#passwordRepeated'"() {
        when:
        validator.validate(request)

        then:
        0 * repository.existsByUsernameOrPersonalId(_ as String, _ as String)

        and:
        request.getPassword() >> password
        request.getPasswordRepeated() >> passwordRepeated

        and:
        def exception = thrown(ValidationException)

        and:
        verify(exception.getValidationResult(), "password", errorsCount, errorMessages)

        where:
        password    | passwordRepeated  | errorsCount   | errorMessages
        null        | null              | 2             | ["Should not be blank.", "Should be between 6 and 30 chars long."]
        EMPTY       | "        "        | 2             | ["Should not be blank.", "Should be between 6 and 30 chars long."]
        "password"  | "password1"       | 1             | ["Should match with 'passwordRepeated' field."]
    }

    @Unroll
    def "Should throw ValidationException when client exists by username or personalId"() {
        when:
        validator.validate(request)

        then:
        1 * repository.existsByUsernameOrPersonalId(_ as String, _ as String) >> true

        and:
        def exception = thrown(ValidationException)
        verify(exception.getValidationResult(), "client", 1,
                ["Client with username 'username' or personalId '1234567890' already exists."])

    }

    void verify(def validationResult, def field, def errorsCount, def errorMessages) {
        with (validationResult.getErrors().get(field)) {
            assert size() == errorsCount
            assert it == errorMessages as Set
        }
    }

    def prepareRegistrationRequest() {
        Stub(ClientRegistrationRequest) {
            getUsername() >> USERNAME
            getPersonalId() >> PERSONAL_ID
            getPassword() >> PASSWORD
            getPasswordRepeated() >> PASSWORD
        }
    }

}
