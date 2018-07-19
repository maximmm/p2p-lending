package com.wandoo.core.util;

import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.function.Supplier;

import static java.time.Instant.ofEpochMilli;
import static java.time.ZoneId.systemDefault;
import static java.util.Date.from;
import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class DateTimeUtil {

    public static LocalDate toLocalDate(Date javaDate) {
        return ofNullable(javaDate)
                .map(date -> ofEpochMilli(date.getTime()).atZone(systemDefault()).toLocalDate())
                .orElseThrow(dateCanNotBeNull());
    }

    public static Date toDate(LocalDate localDate) {
        return ofNullable(localDate)
                .map(date -> from(date.atStartOfDay(systemDefault()).toInstant()))
                .orElseThrow(dateCanNotBeNull());
    }

    private static Supplier<IllegalArgumentException> dateCanNotBeNull() {
        return () -> new IllegalArgumentException("Date to convert can not be null.");
    }

}
