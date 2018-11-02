package com.falco.workshop.tdd.reservation.domain;

import javax.persistence.Embeddable;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

@Embeddable
public class TimeInterval {
    private LocalTime start;
    private LocalTime end;

    TimeInterval() {
    }

    TimeInterval(LocalTime start, LocalTime end) {
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
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }

    public static TimeInterval parse(String fromTo) {
        String[] fromToSplitted = fromTo.split("-");
        return new TimeInterval(LocalTime.parse(fromToSplitted[0]), LocalTime.parse(fromToSplitted[1])
        );
    }
}
