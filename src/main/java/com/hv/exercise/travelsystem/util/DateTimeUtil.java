package com.hv.exercise.travelsystem.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class DateTimeUtil {

    public static LocalDateTime convertStringToLocalDateTime(String dateString, String patten) {
        return convertStringToLocalDateTime(dateString, DateTimeFormatter.ofPattern(patten));
    }

    public static LocalDateTime convertStringToLocalDateTime(String dateString, DateTimeFormatter dateTimeFormatter) {
        return LocalDateTime.parse(dateString, dateTimeFormatter);
    }

    public static String convertLocalDateTimeToString(LocalDateTime input, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return input.format(formatter);
    }

    public static long getDurationInSecond(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        // Get the duration in seconds
        return duration.getSeconds();
    }
}
