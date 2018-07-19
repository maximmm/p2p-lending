package com.wandoo.core.validation;

@FunctionalInterface
public interface Validation<K> {

    void validate(K param, String field, ValidationResult validationResult);

    default Validation<K> and(Validation<K> other) {
        return (param, field, validationResult) -> {
            this.validate(param, field, validationResult);
            other.validate(param, field, validationResult);
        };
    }

}
