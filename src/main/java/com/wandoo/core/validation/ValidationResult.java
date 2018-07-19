package com.wandoo.core.validation;

import lombok.Getter;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

@Getter
public class ValidationResult {

    private Map<String, Set<String>> errors = newHashMap();

    public void addError(String fieldName, String errorMessage) {
        errors.computeIfAbsent(fieldName, key -> newHashSet()).add(errorMessage);
    }

    public void addErrorAndThrow(String fieldName, String errorMessage) {
        addError(fieldName, errorMessage);
        throw new ValidationException(this);
    }

    public void throwIfFailed() {
        if (hasErrors())
            throw new ValidationException(this);
    }

    private boolean hasErrors() {
        return !errors.isEmpty();
    }

}
