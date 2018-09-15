package com.falco.workshop.tdd.reservation;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class Slot {
    private final ScheduleId id;
    private final DateInterval interval;

    public Slot(ScheduleId scheduleId, DateInterval dateInterval) {
        this.id = scheduleId;
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

    public ScheduleId id() {
        return id;
    }

    public DateInterval interval() {
        return interval;
    }
}
