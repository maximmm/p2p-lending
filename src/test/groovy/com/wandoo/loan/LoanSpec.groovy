package com.wandoo.loan

import com.wandoo.loan.investment.Investment
import spock.lang.Specification

import static com.wandoo.loan.Loan.Status.OPEN
import static com.wandoo.loan.Loan.Status.PAID
import static com.wandoo.loan.LoanAccountEntry.Type.*
import static java.math.BigDecimal.valueOf

class LoanSpec extends Specification {

    private static final String LOAN_NUMBER = "1234567890"
    private static final BigDecimal MAIN_AMOUNT = valueOf(1_200L)
    private static final BigDecimal PAYMENT_AMOUNT = valueOf(400L)
    private static final BigDecimal INVESTMENT_AMOUNT = valueOf(300L)

    def investment = Stub(Investment) {
        getInvestmentAmount() >> INVESTMENT_AMOUNT
    }

    Loan loan

    def setup() {
        loan = new Loan().with {
                    loanNumber = LOAN_NUMBER
                    status = OPEN
                    availableForInvestment = true
                    addLoanAccountEntry(new LoanAccountEntry(MAIN, MAIN_AMOUNT))
                    it
                }
    }

    def "Should make one investment"() {
        when:
        loan.makeInvestment(investment)

        then:
        with (loan) {
            getAmountToBeInvested() == valueOf(900)
            getLoanAccountEntries().size() == 2
            getLoanAccountEntries().find { it.type == INVESTMENT }.amount == INVESTMENT_AMOUNT
            isAvailableForInvestment()
        }
    }

    def "Should fully invest in loan"() {
        when:
        4.times { loan.makeInvestment(investment) }

        then:
        with (loan) {
            getAmountToBeInvested() == valueOf(0)
            getLoanAccountEntries().size() == 5
            getLoanAccountEntries().findAll() { it.type == INVESTMENT }*.amount.sum() == MAIN_AMOUNT
            !isAvailableForInvestment()
        }
    }

    def "Should throw IllegalStateException if loan is not available for investment"() {
        given:
        loan.setAvailableForInvestment(false)

        when:
        loan.makeInvestment(investment)

        then:
        with (loan) {
            getAmountToBeInvested() == MAIN_AMOUNT
            getLoanAccountEntries().size() == 1
            !isAvailableForInvestment()
        }

        and:
        def exception = thrown(IllegalStateException)
        exception.message == "Loan '$LOAN_NUMBER' is not applicable for investment."
    }

    def "Should throw IllegalStateException if investment amount is too high"() {
        when:
        loan.makeInvestment(investment)

        then:
        investment.getInvestmentAmount() >> valueOf(2_000L)

        and:
        with (loan) {
            getAmountToBeInvested() == MAIN_AMOUNT
            getLoanAccountEntries().size() == 1
            isAvailableForInvestment()
        }

        and:
        def exception = thrown(IllegalStateException)
        exception.message == "Investment amount for loan '$LOAN_NUMBER' is too high. Amount to invest is '$MAIN_AMOUNT'."
    }

    def "Should make one payment"() {
        when:
        loan.makePayment(PAYMENT_AMOUNT)

        then:
        with (loan) {
            getAmountToBePaid() == valueOf(800)
            getLoanAccountEntries().size() == 2
            getLoanAccountEntries().find { it.type == PAYMENT }.amount == PAYMENT_AMOUNT
            !isPaid()
        }
    }

    def "Should fully repay the loan"() {
        when:
        3.times { loan.makePayment(PAYMENT_AMOUNT) }

        then:
        with (loan) {
            getAmountToBePaid() == valueOf(0)
            getLoanAccountEntries().size() == 4
            getLoanAccountEntries().findAll() { it.type == PAYMENT }*.amount.sum() == MAIN_AMOUNT
            isPaid()
        }
    }

    def "Should throw IllegalStateException if loan is already paid"() {
        given:
        loan.setStatus(PAID)

        when:
        loan.makePayment(PAYMENT_AMOUNT)

        then:
        with (loan) {
            getAmountToBePaid() == MAIN_AMOUNT
            getLoanAccountEntries().size() == 1
            isPaid()
        }

        and:
        def exception = thrown(IllegalStateException)
        exception.message == "Loan '$LOAN_NUMBER' is already paid."
    }

    def "Should throw IllegalStateException if payment amount is too high"() {
        when:
        loan.makePayment(valueOf(2_000L))

        then:
        with (loan) {
            getAmountToBePaid() == MAIN_AMOUNT
            getLoanAccountEntries().size() == 1
            !isPaid()
        }

        and:
        def exception = thrown(IllegalStateException)
        exception.message == "Payment amount for loan '$LOAN_NUMBER' is too high. Amount to pay is '$MAIN_AMOUNT'."
    }

}
