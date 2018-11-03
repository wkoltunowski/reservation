package com.falco.workshop.tdd.reservation.domain;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;

public class TimeInterval {
    private final LocalTime start;
    private final LocalTime end;

    private TimeInterval(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    public DateInterval toDateInterval(LocalDate day) {
        return DateInterval.parse(day, this);
    }

    public LocalTime start() {
        return this.start;
    }

    public LocalTime end() {
        return this.end;
    }

    @Override
    public boolean equals(Object o) {
        return reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return start.toString() + "-" + end.toString();
    }

    public static TimeInterval parse(String fromTo) {
        String[] fromToSplitted = fromTo.split("-");
        return new TimeInterval(LocalTime.parse(fromToSplitted[0]), LocalTime.parse(fromToSplitted[1]));
    }
}
