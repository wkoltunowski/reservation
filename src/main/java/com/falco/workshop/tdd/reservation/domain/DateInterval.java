package com.falco.workshop.tdd.reservation.domain;

import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class DateInterval {
    private final LocalDateTime start;
    private final LocalDateTime end;

    private DateInterval(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public boolean encloses(DateInterval slotInterval) {
        return range().encloses(slotInterval.range());
    }

    private Range<LocalDateTime> range() {
        return Ranges.closedOpen(start, end);
    }

    public boolean intersects(DateInterval interval) {
        return range().isConnected(interval.range()) && !range().intersection(interval.range()).isEmpty();
    }

    public DateInterval plus(Duration duration) {
        return new DateInterval(start.plus(duration), end.plus(duration));
    }

    public boolean contains(LocalDateTime dateTime) {
        return range().contains(dateTime);
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
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }

    public static DateInterval fromTo(LocalDate day, TimeInterval timeInterval) {
        return new DateInterval(day.atTime(timeInterval.start()), day.atTime(timeInterval.end()));
    }

    public static DateInterval fromTo(String dayFromTo) {
        return fromTo(LocalDate.parse(dayFromTo.split(" ")[0]), TimeInterval.fromTo(dayFromTo.split(" ")[1]));
    }

    public static DateInterval fromTo(LocalDateTime start, Duration duration) {
        return new DateInterval(start, start.plus(duration));
    }

    public static DateInterval fromTo(LocalDateTime start, LocalDateTime end) {
        return new DateInterval(start, end);
    }

    public LocalDateTime start() {
        return start;
    }

    public LocalDateTime end() {
        return end;
    }

    public boolean isEmpty() {
        return !end.isAfter(start);
    }
}
