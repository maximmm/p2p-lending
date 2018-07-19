package com.wandoo.loan;

import com.wandoo.client.Client;
import com.wandoo.client.ClientId;
import com.wandoo.client.ClientRepository;
import com.wandoo.loan.investment.Investment;
import com.wandoo.loan.investment.InvestmentCalculator;
import com.wandoo.loan.investment.InvestmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

import static java.lang.String.format;
import static org.springframework.data.domain.PageRequest.of;

@RequiredArgsConstructor
@Service
public class LoanService {

    private final ClientRepository clientRepository;
    private final LoanRepository loanRepository;
    private final InvestmentCalculator investmentCalculator;

    @Transactional(readOnly = true)
    public Page<Loan> getAvailableForInvestment(int page, int size) {
        return loanRepository.findByAvailableForInvestment(true, of(page, size));
    }

    @Transactional
    public Loan investInLoan(InvestmentRequest request, ClientId clientId) {
        Loan loan = loanRepository.findByLoanNumber(request.getLoanNumber())
                .orElseThrow(loanNotFound(request.getLoanNumber()));
        Client client = clientRepository.findById(clientId.getId()).orElseThrow(clientNotFound(clientId.getId()));

        Investment investment = investmentCalculator.calculate(request.getInvestmentAmount(), loan.getDueDate());
        loan.makeInvestment(investment);
        client.addInvestment(investment);

        return loan;
    }

    private static Supplier<IllegalStateException> loanNotFound(String loanNumber) {
        return () -> new IllegalStateException(format("Loan '%s' was not found.", loanNumber));
    }

    private static Supplier<IllegalStateException> clientNotFound(Long clientId) {
        return () -> new IllegalStateException(format("Client with id '%s' was not found.", clientId));
    }

}
