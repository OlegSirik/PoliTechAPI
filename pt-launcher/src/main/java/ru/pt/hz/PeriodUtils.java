package ru.pt.hz;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;

import ru.pt.exception.BadRequestException;

public class PeriodUtils {
    public static int comparePeriods(Period p1, Period p2) {
        // Опорная дата
        LocalDate base = LocalDate.now();

        // Вычисляем реальное количество дней
        long days1 = base.plus(p1).toEpochDay() - base.toEpochDay();
        long days2 = base.plus(p2).toEpochDay() - base.toEpochDay();

        return Long.compare(days1, days2);
    }

    public static LocalDate addPeriod(LocalDate date, Period period) {
        return date.plus(period);
    }

    public static String addPeriod(String date, String period) {
        // Adds a Period to an ISO_OFFSET_DATE_TIME string and returns the result as ISO_OFFSET_DATE_TIME string
        if (date == null || period == null) {
            throw new IllegalArgumentException("date and period must not be null");
        }
        java.time.OffsetDateTime odt = java.time.OffsetDateTime.parse(date);
        Period p = Period.parse(period);
        java.time.OffsetDateTime result = odt.plus(p);
        return result.toString();
    }

    public static boolean isPeriodValid(String period) {
        return Period.parse(period) != null;
    }

    public static String[] getPeriods(String periods) {
        if (periods == null) {
            throw new IllegalArgumentException("periods must not be null");
        }
        String[] parts = periods.split(",");
        for (String part : parts) {
            if (!isPeriodValid(part.trim())) {
                return new String[0];
            }
        }
        return parts;
    }

    public static boolean isDateInRange(OffsetDateTime date1, OffsetDateTime date2, String range) {
        if (date1 == null || date2 == null || range == null) {
            throw new IllegalArgumentException("date1, date2, and range must not be null");
        }
        String[] parts = range.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Range format is invalid, expected format: 'P0D-P15D'");
        }
        Period minPeriod, maxPeriod;
        try {
            minPeriod = Period.parse(parts[0]);
            maxPeriod = Period.parse(parts[1]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Range period format is invalid, expected ISO-8601 period, e.g. 'P0D-P15D'");
        }
        
        java.time.OffsetDateTime minDate = date1.plus(minPeriod);
        java.time.OffsetDateTime maxDate = date1.plus(maxPeriod);
        if (minDate.isAfter(date2) || maxDate.isBefore(date2)) {
            return false;
        }
        return true;

    }


}
