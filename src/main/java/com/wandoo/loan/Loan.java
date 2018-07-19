package com.wandoo.loan;

import com.wandoo.core.domain.BaseEntity;
import com.wandoo.loan.investment.Investment;
import lombok.*;
import lombok.EqualsAndHashCode.Exclude;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;
import static com.wandoo.loan.Loan.Status.OPEN;
import static com.wandoo.loan.Loan.Status.PAID;
import static com.wandoo.loan.LoanAccountEntry.Type.*;
import static java.math.BigDecimal.ZERO;
import static javax.persistence.CascadeType.*;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.SEQUENCE;
import static javax.persistence.TemporalType.DATE;
import static lombok.AccessLevel.NONE;

@Data
@NoArgsConstructor
@ToString(exclude = { "loanAccountEntries", "investments" })
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "LOANS")
public class Loan extends BaseEntity {

    public enum Status {
        OPEN, PAID
    }

    @Setter(NONE)
    @Id
    @SequenceGenerator(name = "loansSequence", sequenceName = "loans_seq", allocationSize = 1)
    @GeneratedValue(generator = "loansSequence", strategy = SEQUENCE)
    @Column(name = "ID")
    private Long id;

    @Column(name = "LOAN_NUMBER")
    private String loanNumber;

    @Column(name = "ORIGINATOR")
    private String originator;

    @Temporal(DATE)
    @Column(name = "START_DATE")
    private Date startDate;

    @Temporal(DATE)
    @Column(name = "DUE_DATE")
    private Date dueDate;

    @Enumerated(STRING)
    @Column(name = "STATUS")
    private Status status = OPEN;

    @Exclude
    @OneToMany(mappedBy = "loan", cascade = ALL)
    private Set<LoanAccountEntry> loanAccountEntries = newHashSet();

    @Exclude
    @OneToMany(mappedBy = "loan", cascade = { PERSIST, MERGE, REFRESH, DETACH })
    private Set<Investment> investments = newHashSet();

    @Column(name = "AVAILABLE_FOR_INVESTMENT")
    private boolean availableForInvestment;

    public void makeInvestment(Investment investment) {
        checkState(isAvailableForInvestment(), "Loan '%s' is not applicable for investment.", loanNumber);
        checkState(investment.getInvestmentAmount().compareTo(getAmountToBeInvested()) <= 0,
                "Investment amount for loan '%s' is too high. Amount to invest is '%s'.", loanNumber, getAmountToBeInvested());

        addLoanAccountEntry(new LoanAccountEntry(INVESTMENT, investment.getInvestmentAmount()));
        addInvestment(investment);
        setAvailableForInvestment(getAmountToBeInvested().compareTo(ZERO) > 0);
    }

    public void makePayment(BigDecimal paymentAmount) {
        checkState(!isPaid(), "Loan '%s' is already paid.", loanNumber);
        checkState(paymentAmount.compareTo(getAmountToBePaid()) <= 0,
                "Payment amount for loan '%s' is too high. Amount to pay is '%s'.", loanNumber, getAmountToBePaid());

        if (paymentAmount.compareTo(getAmountToBePaid()) == 0) {
            setStatus(PAID);
        }

        addLoanAccountEntry(new LoanAccountEntry(PAYMENT, paymentAmount));
    }

    public void addLoanAccountEntry(LoanAccountEntry loanAccountEntry) {
        loanAccountEntries.add(loanAccountEntry);
        loanAccountEntry.setLoan(this);
    }

    private void addInvestment(Investment investment) {
        investments.add(investment);
        investment.setLoan(this);
    }

    public BigDecimal getAmountToBePaid() {
        return getMainAmount().subtract(getPaymentAmount());
    }

    public BigDecimal getAmountToBeInvested() {
        return getMainAmount().subtract(getInvestmentAmount());
    }

    private boolean isOpen() {
        return status == OPEN;
    }

    private boolean isPaid() {
        return status == PAID;
    }

    public BigDecimal getMainAmount() {
        return getAccountEntryAmount(MAIN);
    }

    private BigDecimal getPaymentAmount() {
        return getAccountEntryAmount(PAYMENT);
    }

    private BigDecimal getInvestmentAmount() {
        return getAccountEntryAmount(INVESTMENT);
    }

    private BigDecimal getAccountEntryAmount(LoanAccountEntry.Type accountType) {
        return loanAccountEntries.stream()
                .filter(loanAccountEntry -> loanAccountEntry.getType() == accountType)
                .map(LoanAccountEntry::getAmount)
                .reduce(ZERO, BigDecimal::add);
    }
}
