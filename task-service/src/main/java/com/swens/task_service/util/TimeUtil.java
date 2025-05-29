package com.swens.task_service.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeUtil {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    // Formatter for display (IST with zone info)
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(IST_ZONE);

    // Formatter for parsing input strings (no zone info)
    private static final DateTimeFormatter PARSER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Returns current UTC Instant.
     */
    public static Instant nowUTC() {
        return Instant.now();
    }

    /**
     * Formats a UTC Instant into IST date-time string.
     * @param instant UTC instant
     * @return formatted IST date-time string or null if instant is null
     */
    public static String formatInstantToIST(Instant instant) {
        if (instant == null) return null;
        ZonedDateTime istDateTime = instant.atZone(IST_ZONE);
        return FORMATTER.format(istDateTime);
    }

    /**
     * Parses a date-time string in IST timezone to UTC Instant.
     * The input format expected is "yyyy-MM-dd HH:mm:ss" in IST timezone.
     * @param isoDateTimeString date-time string in "yyyy-MM-dd HH:mm:ss" format (IST)
     * @return UTC Instant or null if input invalid or blank
     */
    public static Instant parseISTToInstant(String isoDateTimeString) {
        if (isoDateTimeString == null || isoDateTimeString.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(isoDateTimeString);  // parses ISO 8601 strings like "2025-06-15T14:30:00Z"
        } catch (DateTimeParseException e) {
            return null;
        }
    }

}
