package com.example.myapplication.database.converters;

import androidx.room.TypeConverter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeConverter {
    private final static DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @TypeConverter
    public static OffsetDateTime toOffsetDateTime(String value) {
        return value == null ? OffsetDateTime.now() : OffsetDateTime.parse(value, formatter);
    }

    @TypeConverter
    public static String fromOffsetDateTime(OffsetDateTime date) {
        return date == null ? "" : date.format(formatter);
    }
}
