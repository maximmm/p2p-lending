package com.wandoo.core.validation;

import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

@RequiredArgsConstructor
public class BasicValidation<T> implements Validation<T> {

    private final Predicate<T> predicate;
    private final String errorMessage;

    static <T> BasicValidation<T> from(Predicate<T> predicate, String onErrorMessage) {
        return new BasicValidation<T>(predicate, onErrorMessage);
    }

    @Override
    public void validate(T param, String field, ValidationResult validationResult) {
        if (!predicate.test(param))
            validationResult.addError(field, errorMessage);
    }

}