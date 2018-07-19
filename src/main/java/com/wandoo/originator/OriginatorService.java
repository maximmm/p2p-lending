package com.wandoo.originator;

import com.wandoo.loan.Loan;
import com.wandoo.loan.LoanAccountEntry;
import com.wandoo.loan.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkState;
import static com.wandoo.loan.LoanAccountEntry.Type.MAIN;
import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class OriginatorService {

    private final LoanRepository loanRepository;

    @Transactional
    public Loan pay(OriginatorPaymentRequest request) {
        String loanNumber = request.getLoanNumber();
        log.info("Received payment for loan '{}'", loanNumber);
        Loan loan = loanRepository.findByLoanNumber(loanNumber)
                .orElseThrow(loanNotFound(loanNumber));

        loan.makePayment(request.getPaymentAmount());
        return loan;
    }

    @Transactional
    public Loan receiveLoan(OriginatorLoanRequest request) {
        String loanNumber = request.getLoanNumber();
        log.info("Received loan '{}' from '{}'", loanNumber, request.getOriginator());

        checkState(!loanRepository.existsByLoanNumber(loanNumber),
                format("Loan '%s' already exists.", loanNumber));

        Loan loan = createMainLoan(request);
        return loanRepository.save(loan);
    }

    private Loan createMainLoan(OriginatorLoanRequest request) {
        Loan loan = new Loan();
        loan.setLoanNumber(request.getLoanNumber());
        loan.setOriginator(request.getOriginator());
        loan.setStartDate(request.getStartDate());
        loan.setDueDate(request.getDueDate());
        loan.setAvailableForInvestment(true);
        loan.addLoanAccountEntry(new LoanAccountEntry(MAIN, request.getAmount()));

        return loan;
    }

    private static Supplier<IllegalStateException> loanNotFound(String loanNumber) {
        return () -> new IllegalStateException(format("Loan '%s' was not found.", loanNumber));
    }

}
