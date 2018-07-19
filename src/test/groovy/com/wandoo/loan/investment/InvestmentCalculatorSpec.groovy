package com.wandoo.loan.investment

import spock.lang.Specification

import static com.wandoo.core.util.DateTimeUtil.toDate
import static java.math.BigDecimal.valueOf
import static java.time.LocalDate.now

class InvestmentCalculatorSpec extends Specification {

    private static final BigDecimal INVESTMENT_AMOUNT = valueOf(350L)
    private static final Date RETURN_DATE = toDate(now().plusMonths(11))

    def ratePerYear = valueOf(0.12d)

    def calculator = new InvestmentCalculator(ratePerYear: ratePerYear)

    def "Should calculate and create investment"() {
        when:
        def result = calculator.calculate(INVESTMENT_AMOUNT, RETURN_DATE)

        then:
        with (result) {
            getInvestmentAmount() == INVESTMENT_AMOUNT
            getInvestmentDate() == toDate(now())
            getReturnAmount() == valueOf(388.50d)
            getReturnDate() == RETURN_DATE
        }
    }

}
