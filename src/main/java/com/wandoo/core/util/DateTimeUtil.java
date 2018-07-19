package com.wandoo.core.util;

import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

import static java.time.Instant.ofEpochMilli;
import static java.time.ZoneId.systemDefault;
import static java.util.Date.from;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class DateTimeUtil {

    public static LocalDate toLocalDate(Date date) {
        return ofEpochMilli(date.getTime()).atZone(systemDefault()).toLocalDate();
    }

    public static Date toDate(LocalDate localDate) {
        return from(localDate.atStartOfDay(systemDefault()).toInstant());
    }

}
