package com.wandoo.core.validation;

import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

@RequiredArgsConstructor
public class BasicValidation<K> implements Validation<K> {

    private final Predicate<K> predicate;
    private final String errorMessage;

    static <K> BasicValidation<K> from(Predicate<K> predicate, String onErrorMessage) {
        return new BasicValidation<K>(predicate, onErrorMessage);
    }

    @Override
    public void validate(K param, String field, ValidationResult validationResult) {
        if (!predicate.test(param))
            validationResult.addError(field, errorMessage);
    }

}