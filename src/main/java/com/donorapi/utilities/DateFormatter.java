package com.donorapi.utilities;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, d MMM, yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private DateFormatter() {
        // Prevent instantiation
    }

    public static String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }

    public static String formatTime(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }

    public static String formatTimeRange(LocalTime start, LocalTime end) {
        return formatTime(start) + " - " + formatTime(end);
    }

    public static String formatTimeRange(LocalDateTime start, LocalDateTime end) {
        return formatTime(start.toLocalTime()) + " - " + formatTime(end.toLocalTime());
    }
}
