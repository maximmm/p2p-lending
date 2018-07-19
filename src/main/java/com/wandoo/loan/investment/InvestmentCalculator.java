package com.wandoo.loan.investment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Period;
import java.util.Date;

import static com.wandoo.core.util.DateTimeUtil.toDate;
import static com.wandoo.core.util.DateTimeUtil.toLocalDate;
import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;
import static java.time.LocalDate.now;
import static java.time.Period.between;

@Component
public class InvestmentCalculator {

    @Value("${investment.rate.per.year:0.12}")
    private BigDecimal ratePerYear;

    public Investment calculate(BigDecimal investmentAmount, Date returnDate) {
        BigDecimal returnAmount = calculateReturnAmount(investmentAmount, returnDate);
        return createInvestment(investmentAmount, returnAmount, returnDate);
    }

    private Investment createInvestment(BigDecimal investmentAmount, BigDecimal returnAmount, Date loanDueDate) {
        Investment investment = new Investment();
        investment.setInvestmentAmount(investmentAmount);
        investment.setInvestmentDate(toDate(now()));
        investment.setReturnDate(loanDueDate);
        investment.setReturnAmount(returnAmount);
        return investment;
    }

    private BigDecimal calculateReturnAmount(BigDecimal investmentAmount, Date loanDueDate) {
        BigDecimal ratePerMonth = ratePerYear.divide(valueOf(12), HALF_UP);
        Period period = between(now(), toLocalDate(loanDueDate));
        BigDecimal calculatedRate = ratePerMonth.multiply(valueOf(period.toTotalMonths()));
        BigDecimal calculatedAmount = investmentAmount.multiply(calculatedRate);
        return investmentAmount.add(calculatedAmount);
    }

}
