package com.wandoo.loan;

import com.wandoo.core.domain.BaseEntity;
import lombok.*;
import lombok.EqualsAndHashCode.Exclude;

import javax.persistence.*;
import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.NONE;

@Data
@NoArgsConstructor
@ToString(exclude = "loan")
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "LOAN_ACCOUNT_ENTRIES")
public class LoanAccountEntry extends BaseEntity {

    public enum Type {
        MAIN, PAYMENT, INVESTMENT;
    }

    @Setter(NONE)
    @Id
    @SequenceGenerator(name = "loanAccountEntriesSequence", sequenceName = "loan_account_entries_seq", allocationSize = 1)
    @GeneratedValue(generator = "loanAccountEntriesSequence", strategy = SEQUENCE)
    @Column(name = "ID")
    private Long id;

    @Exclude
    @ManyToOne(optional = false, fetch = LAZY)
    @JoinColumn(name = "LOAN_ID")
    private Loan loan;

    @Enumerated(STRING)
    @Column(name = "TYPE")
    private Type type;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    public LoanAccountEntry(Type type, BigDecimal amount) {
        this.type = checkNotNull(type);
        this.amount = checkNotNull(amount);
    }

}
