package com.wandoo.originator;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OriginatorLoanRequest {

    @NotBlank
    @Size(min = 6, max = 10)
    private String loanNumber;

    @NotBlank
    @Size(min = 4, max = 20)
    private String originator;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    @PastOrPresent
    private Date startDate;

    @NotNull
    @Future
    private Date dueDate;

}
