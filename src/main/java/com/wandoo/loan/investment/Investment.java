package com.wandoo.loan.investment;

import com.wandoo.client.Client;
import com.wandoo.core.domain.BaseEntity;
import com.wandoo.loan.Loan;
import lombok.*;
import lombok.EqualsAndHashCode.Exclude;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.SEQUENCE;
import static javax.persistence.TemporalType.DATE;
import static lombok.AccessLevel.NONE;

@Data
@NoArgsConstructor
@ToString(exclude = { "client", "loan" } )
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "INVESTMENTS")
public class Investment extends BaseEntity {

    @Setter(NONE)
    @Id
    @SequenceGenerator(name = "investmentsSequence", sequenceName = "investments_seq", allocationSize = 1)
    @GeneratedValue(generator = "investmentsSequence", strategy = SEQUENCE)
    @Column(name = "ID")
    private Long id;

    @Exclude
    @ManyToOne(optional = false, fetch = LAZY)
    @JoinColumn(name = "CLIENT_ID")
    private Client client;

    @Exclude
    @ManyToOne(optional = false, fetch = LAZY)
    @JoinColumn(name = "LOAN_ID")
    private Loan loan;

    @Temporal(DATE)
    @Column(name = "INVESTMENT_DATE")
    private Date investmentDate;

    @Temporal(DATE)
    @Column(name = "RETURN_DATE")
    private Date returnDate;

    @Column(name = "INVESTMENT_AMOUNT")
    private BigDecimal investmentAmount;

    @Column(name = "RETURN_AMOUNT")
    private BigDecimal returnAmount;

}
