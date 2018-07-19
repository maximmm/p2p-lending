package com.wandoo.originator;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.wandoo.loan.LoanBean.from;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/originator")
@RequiredArgsConstructor
public class OriginatorController {

    private final OriginatorService originatorService;

    @PostMapping("/loan")
    public ResponseEntity<?> receiveLoan(@Valid @RequestBody OriginatorLoanRequest request) {
        return ok(from(originatorService.receiveLoan(request)));
    }

    @PostMapping("/payment")
    public ResponseEntity<?> payForLoan(@Valid @RequestBody OriginatorPaymentRequest request) {
        return ok(from(originatorService.pay(request)));
    }

}
