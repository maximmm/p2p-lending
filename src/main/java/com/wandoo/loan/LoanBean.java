package com.wandoo.loan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanBean {

    private String loanNumber;

    private String originator;

    private Date startDate;

    private Date dueDate;

    private String status;

    private BigDecimal mainAmount;

    private BigDecimal amountToBePaid;

    private BigDecimal amountToBeInvested;

    private boolean availableForInvestment;

    public static LoanBean from(Loan loan) {
        return LoanBean.builder()
                .loanNumber(loan.getLoanNumber())
                .originator(loan.getOriginator())
                .startDate(loan.getStartDate())
                .dueDate(loan.getDueDate())
                .status(loan.getStatus().name())
                .mainAmount(loan.getMainAmount())
                .amountToBePaid(loan.getAmountToBePaid())
                .amountToBeInvested(loan.getAmountToBeInvested())
                .availableForInvestment(loan.isAvailableForInvestment())
                .build();
    }

}
