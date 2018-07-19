package com.wandoo.client;

import com.wandoo.core.domain.BaseEntity;
import com.wandoo.loan.investment.Investment;
import lombok.*;
import lombok.EqualsAndHashCode.Exclude;

import javax.persistence.*;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static javax.persistence.CascadeType.*;
import static javax.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.NONE;

@Data
@NoArgsConstructor
@ToString(exclude = "investments")
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "CLIENTS")
public class Client extends BaseEntity {

    @Setter(NONE)
    @Id
    @SequenceGenerator(name = "clientsSequence", sequenceName = "clients_seq", allocationSize = 1)
    @GeneratedValue(generator = "clientsSequence", strategy = SEQUENCE)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PERSONAL_ID")
    private String personalId;

    @Column(name = "PASSWORD")
    private String password;

    @Exclude
    @OneToMany(mappedBy = "client", cascade = { PERSIST, MERGE, REFRESH, DETACH })
    private Set<Investment> investments = newHashSet();

    public void addInvestment(Investment investment) {
        investments.add(investment);
        investment.setClient(this);
    }

}
