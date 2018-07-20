package com.wandoo.core.validation;

import org.apache.commons.lang3.StringUtils;

import static com.wandoo.core.validation.BasicValidation.from;
import static java.lang.String.format;
import static org.apache.commons.lang3.Range.between;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

public final class Validations {

    private static Validation<String> notBlank = from(StringUtils::isNotBlank, "Should not be blank.");

    public static Validation<String> lengthIn(int minLength, int maxLength) {
        return notBlank.and(from((str) -> between(minLength, maxLength).contains(trimToEmpty(str).length()),
                format("Should be between %d and %d chars long.", minLength, maxLength)));
    }

    public static Validation<String> length(int length) {
        return notBlank.and(from((str) -> trimToEmpty(str).length() == length,
                format("Should be %d chars long.", length)));
    }

    public static Validation<String> match(String strToMatch, String fieldToMatch) {
        return notBlank.and(from(str -> StringUtils.equals(trimToEmpty(str), trimToEmpty(strToMatch)),
                format("Should match with '%s' field.", fieldToMatch)));
    }
}