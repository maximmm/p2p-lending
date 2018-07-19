package com.wandoo.client.registration

import com.wandoo.client.ClientBean
import com.wandoo.configuration.BaseControllerSpec
import spock.lang.Unroll

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class ClientRegistrationControllerSpec extends BaseControllerSpec {

    def service = Mock(ClientRegistrationService)
    def controller = new ClientRegistrationController(service)
    def mockMvc = standaloneSetup(controller).build()

    ClientRegistrationRequest request

    def setup() {
        request = prepareRequest()
    }

    def "Should return response when registration is successful"() {
        when:
        def result = mockMvc.perform(post('/registration')
                .contentType(APPLICATION_JSON)
                .content(toJson(request)))
                .andReturn()

        then:
        1 * service.register(request) >> clientStub()

        and:
        with (result.getResponse()) {
            with (fromJson(getContentAsString(), ClientBean.class)) {
                username == USERNAME
                personalId == PERSONAL_ID
            }
            getStatus() == OK.value()
        }
    }

    @Unroll
    def "Should fail with codes '#codes' when username is '#username'"() {
        given:
        request.username = username

        when:
        def result = mockMvc.perform(post('/registration')
                .contentType(APPLICATION_JSON)
                .content(toJson(request)))
                .andReturn()

        then:
        0 * service.register(request)

        and:
        result.getResponse().getStatus() == BAD_REQUEST.value()

        and:
        verify("username", codes, result.getResolvedException())

        where:
        username    | codes
        "123"       | ["Size"]
        ""          | ["Size", "NotBlank"]
    }

    @Unroll
    def "Should fail with codes '#codes' when personalId is '#personalId'"() {
        given:
        request.personalId = personalId

        when:
        def result = mockMvc.perform(post('/registration')
                .contentType(APPLICATION_JSON)
                .content(toJson(request)))
                .andReturn()

        then:
        0 * service.register(request)

        and:
        result.getResponse().getStatus() == BAD_REQUEST.value()

        and:
        verify("personalId", codes, result.getResolvedException())

        where:
        personalId  | codes
        "123"       | ["Size"]
        ""          | ["Size", "NotBlank"]
    }

    @Unroll
    def "Should fail when password field '#property' is empty"() {
        given:
        request."${property}" = ""

        when:
        def result = mockMvc.perform(post('/registration')
                .contentType(APPLICATION_JSON)
                .content(toJson(request)))
                .andReturn()

        then:
        0 * service.register(request)

        and:
        result.getResponse().getStatus() == BAD_REQUEST.value()

        and:
        verify(property, ["Size", "NotBlank"], result.getResolvedException())

        where:
        property << ["password", "passwordRepeated"]
    }

    @Unroll
    def "Should fail when password field '#property' is too small"() {
        given:
        request."${property}" = "12345"

        when:
        def result = mockMvc.perform(post('/registration')
                .contentType(APPLICATION_JSON)
                .content(toJson(request)))
                .andReturn()

        then:
        0 * service.register(request)

        and:
        result.getResponse().getStatus() == BAD_REQUEST.value()

        and:
        verify(property, ["Size"], result.getResolvedException())

        where:
        property << ["password", "passwordRepeated"]
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
