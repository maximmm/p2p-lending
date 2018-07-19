package com.wandoo.loan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    Optional<Loan> findByLoanNumber(String loanNumber);

    boolean existsByLoanNumber(String loanNumber);

    Page<Loan> findByAvailableForInvestment(boolean availableForInvestment, Pageable pageable);

}
