package com.serhat.taskFlow.service;

import com.serhat.taskFlow.interfaces.DateRangeParser;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DefaultDateRangeParser implements DateRangeParser {

    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Override
    public LocalDateTime parseStartDate(String startDate) {
        if (startDate == null || startDate.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(startDate, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            return LocalDate.parse(startDate, DATE_ONLY_FORMATTER).atStartOfDay(); // "dd-MM-yyyy" -> 00:00:00
        }
    }

    @Override
    public LocalDateTime parseEndDate(String endDate) {
        if (endDate == null || endDate.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(endDate, DATE_TIME_FORMATTER); // "dd-MM-yyyy HH:mm"
        } catch (Exception e) {
            return LocalDate.parse(endDate, DATE_ONLY_FORMATTER).atTime(23, 59); // "dd-MM-yyyy" -> 23:59:00
        }
    }
}