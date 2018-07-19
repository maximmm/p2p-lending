package com.wandoo.originator;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class OriginatorPaymentRequest {

    @NotBlank
    @Size(min = 6, max = 10)
    private String loanNumber;

    @NotNull
    @Positive
    private BigDecimal paymentAmount;

}
