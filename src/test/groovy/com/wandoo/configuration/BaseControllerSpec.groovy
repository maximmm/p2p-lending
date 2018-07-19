package com.wandoo.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.wandoo.client.Client
import com.wandoo.loan.Loan
import spock.lang.Specification

import static com.wandoo.core.util.DateTimeUtil.toDate
import static com.wandoo.loan.Loan.Status.OPEN
import static java.math.BigDecimal.valueOf
import static java.time.LocalDate.now
import static org.assertj.core.util.Sets.newHashSet

abstract class BaseControllerSpec extends Specification {

    protected static final String USERNAME = "username"
    protected static final String PERSONAL_ID = "1234567890"
    protected static final String PASSWORD = "password"

    protected static final String LOAN_NUMBER = "1234567890"
    protected static final String ORIGINATOR = "Originator"
    protected static final BigDecimal AMOUNT = valueOf(1_000L)
    protected static final BigDecimal PAYMENT_AMOUNT = valueOf(300L)
    protected static final Date START_DATE = toDate(now().minusMonths(1))
    protected static final Date DUE_DATE = toDate(now().plusMonths(11))

    def clientStub() {
        Stub(Client) {
            getId() >> 1L
            getUsername() >> USERNAME
            getPersonalId() >> PERSONAL_ID
            getPassword() >> PASSWORD
            getInvestments() >> newHashSet()
        }
    }

    def loanStub() {
        dummyLoan(AMOUNT, AMOUNT, AMOUNT)
    }

    def loanWithPaymentStub() {
        dummyLoan(AMOUNT, AMOUNT.subtract(PAYMENT_AMOUNT), AMOUNT)
    }

    def dummyLoan(def mainAmount, def amountToBePaid, def amountToBeInvested) {
        Stub(Loan) {
            getId() >> 1L
            getLoanNumber() >> LOAN_NUMBER
            getOriginator() >> ORIGINATOR
            getStartDate() >> START_DATE
            getDueDate() >> DUE_DATE
            getStatus() >> OPEN
            getMainAmount() >> mainAmount
            getAmountToBePaid() >> amountToBePaid
            getAmountToBeInvested() >> amountToBeInvested
            isAvailableForInvestment() >> true
        }
    }

    void verify(def field, def codes, def exception) {
        def errorCodes = exception.getBindingResult().getFieldErrors().codes
        assert errorCodes.size() == codes.size()
        assert errorCodes.flatten().containsAll(
                    codes.collect { "$it.$field" as String }
        )
    }

    def toJson(Object obj) {
        new ObjectMapper().writeValueAsString(obj);
    }

    def fromJson(String obj, Class clazz) {
        new ObjectMapper().readValue(obj, clazz);
    }
}
