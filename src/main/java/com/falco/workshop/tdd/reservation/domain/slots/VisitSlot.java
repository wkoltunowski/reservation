package com.falco.workshop.tdd.reservation.domain.slots;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;

import static com.falco.workshop.tdd.reservation.domain.DateInterval.fromTo;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class VisitSlot {
    private final ScheduleId scheduleId;
    private final DateInterval interval;

    private VisitSlot(ScheduleId scheduleId, DateInterval dateInterval) {
        this.scheduleId = scheduleId;
        this.interval = dateInterval;
    }

    public ScheduleId id() {
        return scheduleId;
    }

    public DateInterval interval() {
        return interval;
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

    public static VisitSlot visitSlot(ScheduleId scheduleId, DateInterval interval) {
        return new VisitSlot(scheduleId, interval);
    }

    public static VisitSlot visitSlot(ScheduleId scheduleId, String dayFromTo) {
        return visitSlot(scheduleId, fromTo(dayFromTo));
    }
}
