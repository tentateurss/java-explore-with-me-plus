package ru.practicum.stats.util;

import java.time.format.DateTimeFormatter;

public class DateTimeFormatters  {

    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter STANDARD =
            DateTimeFormatter.ofPattern(PATTERN);

    private DateTimeFormatters () {

    }
}