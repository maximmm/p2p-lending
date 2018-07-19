package com.wandoo.core.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ValidationException extends RuntimeException {

    private final ValidationResult validationResult;

}
