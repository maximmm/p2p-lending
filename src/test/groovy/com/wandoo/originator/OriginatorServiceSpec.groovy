package com.wandoo.originator

import com.wandoo.loan.Loan
import com.wandoo.loan.LoanAccountEntry
import com.wandoo.loan.LoanRepository
import spock.lang.Specification

import static com.wandoo.core.util.DateTimeUtil.toDate
import static com.wandoo.loan.Loan.Status.OPEN
import static com.wandoo.loan.LoanAccountEntry.Type.MAIN
import static com.wandoo.loan.LoanAccountEntry.Type.PAYMENT
import static java.math.BigDecimal.valueOf
import static java.time.LocalDate.now
import static java.util.Optional.empty
import static java.util.Optional.of

class OriginatorServiceSpec extends Specification {

    private static final String LOAN_NUMBER = "1234567890"
    private static final String ORIGINATOR = "Originator"
    private static final BigDecimal AMOUNT = valueOf(1_000L)
    private static final BigDecimal PAYMENT_AMOUNT = valueOf(300L)
    private static final Date START_DATE = toDate(now().minusMonths(1))
    private static final Date DUE_DATE = toDate(now().plusMonths(11))

    def repository = Mock(LoanRepository)
    def service = new OriginatorService(repository)

    def "Should successfully receive loan"() {
        when:
        def result = service.receiveLoan(prepareLoanRequest())

        then:
        1 * repository.existsByLoanNumber(LOAN_NUMBER) >> false

        and:
        1 * repository.save(_ as Loan) >> { arguments ->
            verifyLoan(arguments[0] as Loan)
        }
        verifyLoan(result)

        and:
        noExceptionThrown()
    }

    def "Should throw IllegalStateException during loan receiving if such already exists"() {
        when:
        service.receiveLoan(prepareLoanRequest())

        then:
        1 * repository.existsByLoanNumber(LOAN_NUMBER) >> true
        0 * repository.save(_ as Loan)

        and:
        def exception = thrown(IllegalStateException)
        exception.message == "Loan '$LOAN_NUMBER' already exists."
    }

    def "Should successfully pay for loan"() {
        when:
        def result = service.pay(preparePaymentRequest())

        then:
        1 * repository.findByLoanNumber(LOAN_NUMBER) >> of(loan())

        and:
        verifyLoan(result)

        and:
        with(result.getLoanAccountEntries()) {
            size() == 2
            with(it[1] as LoanAccountEntry) {
                type == PAYMENT
                amount == PAYMENT_AMOUNT
            }
        }

        and:
        noExceptionThrown()
    }

    def "Should throw IllegalStateException during payment receiving if corresponding loan does not exist"() {
        when:
        service.pay(preparePaymentRequest())

        then:
        1 * repository.findByLoanNumber(LOAN_NUMBER) >> empty()

        and:
        def exception = thrown(IllegalStateException)
        exception.message == "Loan '$LOAN_NUMBER' was not found."
    }

    def loan() {
        new Loan().with {
            loanNumber = LOAN_NUMBER
            originator = ORIGINATOR
            startDate = START_DATE
            dueDate = DUE_DATE
            status = OPEN
            loanAccountEntries = [new LoanAccountEntry(MAIN, AMOUNT)] as Set
            it
        }
    }

    def verifyLoan(def loan) {
        with(loan as Loan) {
            assert loanNumber == LOAN_NUMBER
            assert originator == ORIGINATOR
            assert startDate == START_DATE
            assert dueDate == DUE_DATE
            assert status == OPEN
            with(loanAccountEntries[0] as LoanAccountEntry) {
                assert type == MAIN
                assert amount == AMOUNT
            }
        }
        loan
    }

    def prepareLoanRequest() {
        Stub(OriginatorLoanRequest) {
            getLoanNumber() >> LOAN_NUMBER
            getOriginator() >> ORIGINATOR
            getAmount() >> AMOUNT
            getStartDate() >> START_DATE
            getDueDate() >> DUE_DATE
        }
    }

    def preparePaymentRequest() {
        Stub(OriginatorPaymentRequest) {
            getLoanNumber() >> LOAN_NUMBER
            getPaymentAmount() >> PAYMENT_AMOUNT
        }
    }

}
