package com.falco.workshop.tdd.reservation.domain;

import javax.persistence.Embeddable;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

@Embeddable
public class Slot {
    private ScheduleId scheduleId;
    private DateInterval interval;

    Slot() {
    }

    Slot(ScheduleId scheduleId, DateInterval dateInterval) {
        this.scheduleId = scheduleId;
        this.interval = dateInterval;
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

    public static Slot slot(ScheduleId scheduleId, DateInterval interval) {
        return new Slot(scheduleId, interval);
    }

    public static Slot slot(ScheduleId scheduleId, String dayFromTo) {
        return Slot.slot(scheduleId, DateInterval.parse(dayFromTo));
    }

    public ScheduleId id() {
        return scheduleId;
    }

    public DateInterval interval() {
        return interval;
    }
}
