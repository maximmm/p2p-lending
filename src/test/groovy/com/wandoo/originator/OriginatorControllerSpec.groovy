package com.wandoo.originator

import com.wandoo.core.configuration.BaseControllerSpec
import com.wandoo.loan.LoanBean
import spock.lang.Unroll

import static com.wandoo.core.util.DateTimeUtil.toDate
import static com.wandoo.loan.Loan.Status.OPEN
import static java.math.BigDecimal.valueOf
import static java.time.LocalDate.now
import static org.apache.commons.lang3.StringUtils.EMPTY
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class OriginatorControllerSpec extends BaseControllerSpec {

    def service = Mock(OriginatorService)
    def controller = new OriginatorController(service)
    def mockMvc = standaloneSetup(controller).build()

    OriginatorLoanRequest loanRequest
    OriginatorPaymentRequest paymentRequest

    def setup() {
        loanRequest = prepareLoanRequest()
        paymentRequest = preparePaymentRequest()
    }

    def "Should return response when loan is received"() {
        when:
        def result = mockMvc.perform(post('/originator/loan')
                .contentType(APPLICATION_JSON)
                .content(toJson(loanRequest)))
                .andReturn()

        then:
        1 * service.receiveLoan(loanRequest) >> loanStub()

        and:
        with (result.getResponse()) {
            with (fromJson(getContentAsString(), LoanBean.class)) {
                loanNumber == LOAN_NUMBER
                originator == ORIGINATOR
                startDate == START_DATE
                dueDate == DUE_DATE
                status == OPEN.name()
                mainAmount == MAIN_AMOUNT
                amountToBePaid == MAIN_AMOUNT
                amountToBeInvested == MAIN_AMOUNT
                availableForInvestment == true
            }
            getStatus() == OK.value()
        }
    }

    @Unroll
    def "Should fail with codes '#codes' when loanNumber is '#loanNumber'"() {
        given:
        loanRequest.loanNumber = loanNumber

        when:
        def result = mockMvc.perform(post('/originator/loan')
                .contentType(APPLICATION_JSON)
                .content(toJson(loanRequest)))
                .andReturn()

        then:
        0 * service.receiveLoan(loanRequest)

        and:
        result.getResponse().getStatus() == BAD_REQUEST.value()

        and:
        verify("loanNumber", codes, result.getResolvedException())

        where:
        loanNumber  | codes
        "123"       | ["Size"]
        EMPTY       | ["Size", "NotBlank"]
    }

    @Unroll
    def "Should fail with codes '#codes' when originator is '#originator'"() {
        given:
        loanRequest.originator = originator

        when:
        def result = mockMvc.perform(post('/originator/loan')
                .contentType(APPLICATION_JSON)
                .content(toJson(loanRequest)))
                .andReturn()

        then:
        0 * service.receiveLoan(loanRequest)

        and:
        result.getResponse().getStatus() == BAD_REQUEST.value()

        and:
        verify("originator", codes, result.getResolvedException())

        where:
        originator  | codes
        "123"       | ["Size"]
        EMPTY       | ["Size", "NotBlank"]
    }

    @Unroll
    def "Should fail with codes '#codes' when amount is '#amount'"() {
        given:
        loanRequest.amount = amount

        when:
        def result = mockMvc.perform(post('/originator/loan')
                .contentType(APPLICATION_JSON)
                .content(toJson(loanRequest)))
                .andReturn()

        then:
        0 * service.receiveLoan(loanRequest)

        and:
        result.getResponse().getStatus() == BAD_REQUEST.value()

        and:
        verify("amount", codes, result.getResolvedException())

        where:
        amount          | codes
        null            | ["NotNull"]
        valueOf(-100L)  | ["Positive"]
    }

    @Unroll
    def "Should fail with codes '#codes' when startDate is '#startDate'"() {
        given:
        loanRequest.startDate = startDate

        when:
        def result = mockMvc.perform(post('/originator/loan')
                .contentType(APPLICATION_JSON)
                .content(toJson(loanRequest)))
                .andReturn()

        then:
        0 * service.receiveLoan(loanRequest)

        and:
        result.getResponse().getStatus() == BAD_REQUEST.value()

        and:
        verify("startDate", codes, result.getResolvedException())

        where:
        startDate                   | codes
        null                        | ["NotNull"]
        toDate(now().plusMonths(1)) | ["PastOrPresent"]
    }

    @Unroll
    def "Should fail with codes '#codes' when dueDate is '#dueDate'"() {
        given:
        loanRequest.dueDate = dueDate

        when:
        def result = mockMvc.perform(post('/originator/loan')
                .contentType(APPLICATION_JSON)
                .content(toJson(loanRequest)))
                .andReturn()

        then:
        0 * service.receiveLoan(loanRequest)

        and:
        result.getResponse().getStatus() == BAD_REQUEST.value()

        and:
        verify("dueDate", codes, result.getResolvedException())

        where:
        dueDate                     | codes
        null                        | ["NotNull"]
        toDate(now())               | ["Future"]
        toDate(now().minusDays(1))  | ["Future"]
    }

    def "Should return response when loan received payment"() {
        when:
        def result = mockMvc.perform(post('/originator/payment')
                .contentType(APPLICATION_JSON)
                .content(toJson(paymentRequest)))
                .andReturn()

        then:
        1 * service.pay(paymentRequest) >> loanWithPaymentStub()

        and:
        with (result.getResponse()) {
            with (fromJson(getContentAsString(), LoanBean.class)) {
                loanNumber == LOAN_NUMBER
                originator == ORIGINATOR
                startDate == START_DATE
                dueDate == DUE_DATE
                status == OPEN.name()
                mainAmount == MAIN_AMOUNT
                amountToBePaid == MAIN_AMOUNT.subtract(PAYMENT_AMOUNT)
                amountToBeInvested == MAIN_AMOUNT
                availableForInvestment == true
            }
            getStatus() == OK.value()
        }
    }

    @Unroll
    def "Should fail payment with codes '#codes' when loanNumber is '#loanNumber'"() {
        given:
        paymentRequest.loanNumber = loanNumber

        when:
        def result = mockMvc.perform(post('/originator/payment')
                .contentType(APPLICATION_JSON)
                .content(toJson(paymentRequest)))
                .andReturn()

        then:
        0 * service.pay(paymentRequest)

        and:
        result.getResponse().getStatus() == BAD_REQUEST.value()

        and:
        verify("loanNumber", codes, result.getResolvedException())

        where:
        loanNumber  | codes
        "123"       | ["Size"]
        EMPTY       | ["Size", "NotBlank"]
    }

    @Unroll
    def "Should fail payment with codes '#codes' when paymentAmount is '#paymentAmount'"() {
        given:
        paymentRequest.paymentAmount = paymentAmount

        when:
        def result = mockMvc.perform(post('/originator/payment')
                .contentType(APPLICATION_JSON)
                .content(toJson(paymentRequest)))
                .andReturn()

        then:
        0 * service.pay(paymentRequest)

        and:
        result.getResponse().getStatus() == BAD_REQUEST.value()

        and:
        verify("paymentAmount", codes, result.getResolvedException())

        where:
        paymentAmount   | codes
        null            | ["NotNull"]
        valueOf(-100L)  | ["Positive"]
    }

    def prepareLoanRequest() {
        new OriginatorLoanRequest().with {
            loanNumber = LOAN_NUMBER
            originator = ORIGINATOR
            amount = MAIN_AMOUNT
            startDate = START_DATE
            dueDate = DUE_DATE
            it
        }
    }

    def preparePaymentRequest() {
        new OriginatorPaymentRequest().with {
            loanNumber = LOAN_NUMBER
            paymentAmount = PAYMENT_AMOUNT
            it
        }
    }
}
