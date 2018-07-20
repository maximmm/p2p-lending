package com.wandoo.core.validation;

@FunctionalInterface
public interface Validation<T> {

    void validate(T param, String field, ValidationResult validationResult);

    default Validation<T> and(Validation<T> other) {
        return (param, field, validationResult) -> {
            this.validate(param, field, validationResult);
            other.validate(param, field, validationResult);
        };
    }

}
