package com.wandoo.loan

import com.wandoo.client.Client
import com.wandoo.client.ClientId
import com.wandoo.client.ClientRepository
import com.wandoo.loan.investment.Investment
import com.wandoo.loan.investment.InvestmentCalculator
import com.wandoo.loan.investment.InvestmentRequest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

import static com.wandoo.core.util.DateTimeUtil.toDate
import static java.math.BigDecimal.valueOf
import static java.time.LocalDate.now
import static java.util.Optional.empty
import static java.util.Optional.of

class LoanServiceSpec extends Specification {

    private static final Long CLIENT_ID = 1L
    private static final String LOAN_NUMBER = "1234567890"
    private static final BigDecimal INVESTMENT_AMOUNT = valueOf(150L)
    private static final Date LOAN_DUE_DATE = toDate(now().plusMonths(11))

    def clientRepository = Mock(ClientRepository)
    def loanRepository = Mock(LoanRepository)
    def investmentCalculator = Mock(InvestmentCalculator)
    def service = new LoanService(clientRepository, loanRepository, investmentCalculator)

    def loan = Mock(Loan) {
        getDueDate() >> LOAN_DUE_DATE
    }

    def client = Mock(Client)

    def clientId = new ClientId(CLIENT_ID)

    def request

    def setup() {
        request = investmentRequest()
    }

    def "Should return page of loans available for investment"() {
        given:
        def page = 0
        def pageSize = 5
        def loansCount = 3

        when:
        def result = service.getAvailableForInvestment(page, pageSize)

        then:
        1 * loanRepository.findByAvailableForInvestment(true, _ as PageRequest) >>
                new PageImpl<Loan>([Stub(Loan)] * loansCount)

        and:
        with (result) {
            getTotalElements() == loansCount
            getTotalPages() == 1
        }
    }

    def "Should invest in loan"() {
        given:
        def investment = Stub(Investment)

        when:
        def result = service.investInLoan(request, clientId)

        then:
        1 * loanRepository.findByLoanNumber(LOAN_NUMBER) >> of(loan)
        1 * clientRepository.findById(CLIENT_ID) >> of(client)
        1 * investmentCalculator.calculate(INVESTMENT_AMOUNT, LOAN_DUE_DATE) >> investment

        and:
        1 * loan.makeInvestment(investment)
        1 * client.addInvestment(investment)

        and:
        result.getDueDate() == LOAN_DUE_DATE
    }

    def "Should fail to invest if loan does not exist"() {
        when:
        service.investInLoan(request, clientId)

        then:
        1 * loanRepository.findByLoanNumber(LOAN_NUMBER) >> empty()
        0 * clientRepository.findById(CLIENT_ID)
        0 * investmentCalculator.calculate(INVESTMENT_AMOUNT, LOAN_DUE_DATE)

        and:
        def exception = thrown(IllegalStateException)
        exception.message == "Loan '$LOAN_NUMBER' was not found."
    }

    def "Should fail to invest if client does not exist"() {
        when:
        service.investInLoan(request, clientId)

        then:
        1 * loanRepository.findByLoanNumber(LOAN_NUMBER) >> of(loan)
        1 * clientRepository.findById(CLIENT_ID) >> empty()
        0 * investmentCalculator.calculate(INVESTMENT_AMOUNT, LOAN_DUE_DATE)

        and:
        0 * loan.makeInvestment(_ as Investment)

        and:
        def exception = thrown(IllegalStateException)
        exception.message == "Client with id '$CLIENT_ID' was not found."
    }

    def investmentRequest() {
        Stub(InvestmentRequest) {
            getLoanNumber() >> LOAN_NUMBER
            getInvestmentAmount() >> INVESTMENT_AMOUNT
        }
    }

}
