package com.wandoo.loan

import com.wandoo.client.ClientId
import com.wandoo.core.configuration.BaseControllerSpec
import com.wandoo.loan.investment.InvestmentRequest
import org.springframework.data.domain.PageImpl
import spock.lang.Unroll

import static com.wandoo.loan.Loan.Status.OPEN
import static java.math.BigDecimal.valueOf
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class LoanControllerSpec extends BaseControllerSpec {

    def service = Mock(LoanService)
    def controller = new LoanController(service)
    def mockMvc = standaloneSetup(controller).build()

    InvestmentRequest investmentRequest

    def setup() {
        investmentRequest = loanInvestmentRequest()
    }

    def "Should return list of loans available for investment"() {
        given:
        def page = 0
        def pageSize = 5
        def loansCount = 3

        when:
        def result = mockMvc.perform(get("/loan/investment/all?page=$page&size=$pageSize")).andReturn()

        then:
        1 * service.getAvailableForInvestment(page, pageSize) >> new PageImpl<Loan>([Stub(Loan)] * loansCount)

        and:
        with (result.getResponse()) {
            with (fromJson(getContentAsString(), List.class)) {
                size == loansCount
            }
            getStatus() == OK.value()
        }
    }

    def "Should return response when loan was invested"() {
        when:
        def result = mockMvc.perform(post('/loan/investment')
                .contentType(APPLICATION_JSON)
                .content(toJson(loanInvestmentRequest())))
                .andReturn()

        then:
        1 * service.investInLoan(loanInvestmentRequest(), _ as ClientId) >> loanWithInvestmentStub()

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
                amountToBeInvested == MAIN_AMOUNT.subtract(INVESTMENT_AMOUNT)
                availableForInvestment == true
            }
            getStatus() == OK.value()
        }
    }

    @Unroll
    def "Should fail investment with codes '#codes' when loanNumber is '#loanNumber'"() {
        given:
        investmentRequest.loanNumber = loanNumber

        when:
        def result = mockMvc.perform(post('/loan/investment')
                .contentType(APPLICATION_JSON)
                .content(toJson(investmentRequest)))
                .andReturn()

        then:
        0 * service.investInLoan(investmentRequest, _ as ClientId)

        and:
        result.getResponse().getStatus() == BAD_REQUEST.value()

        and:
        verify("loanNumber", codes, result.getResolvedException())

        where:
        loanNumber  | codes
        "123"       | ["Size"]
        ""          | ["Size", "NotBlank"]
    }

    @Unroll
    def "Should fail investment with codes '#codes' when investment amount is '#investmentAmount'"() {
        given:
        investmentRequest.investmentAmount = investmentAmount

        when:
        def result = mockMvc.perform(post('/loan/investment')
                .contentType(APPLICATION_JSON)
                .content(toJson(investmentRequest)))
                .andReturn()

        then:
        0 * service.investInLoan(investmentRequest, _ as ClientId)

        and:
        result.getResponse().getStatus() == BAD_REQUEST.value()

        and:
        verify("investmentAmount", codes, result.getResolvedException())

        where:
        investmentAmount    | codes
        null                | ["NotNull"]
        valueOf(-100L)      | ["Positive"]
    }

    def loanInvestmentRequest() {
        new InvestmentRequest().with {
            loanNumber = LOAN_NUMBER
            investmentAmount = INVESTMENT_AMOUNT
            it
        }
    }

}
