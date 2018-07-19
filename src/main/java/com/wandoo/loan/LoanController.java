package com.wandoo.loan;

import com.wandoo.client.ClientId;
import com.wandoo.loan.investment.InvestmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.wandoo.loan.LoanBean.from;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/loan")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping(path = "/investment/all", params = { "page", "size" })
    public ResponseEntity<?> listAvailableLoansForInvestment(@RequestParam("page") int page,
                                                             @RequestParam("size") int size) {
        return ok(loanService.getAvailableForInvestment(page, size).map(LoanBean::from));
    }

    @PostMapping("/investment")
    public ResponseEntity<?> investInLoan(@Valid @RequestBody InvestmentRequest request,
                                          @AuthenticationPrincipal ClientId clientId) {
        return ok(from(loanService.investInLoan(request, clientId)));
    }

}
