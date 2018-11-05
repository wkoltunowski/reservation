package com.falco.workshop.tdd.reservation.domain.schedule;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class ScheduleId implements Serializable {
    private Long scheduleId;

    public Long id() {
        return scheduleId;
    }

    private ScheduleId(long id) {
        this.scheduleId = id;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static ScheduleId scheduleId(int i) {
        return new ScheduleId(i);
    }

    public static ScheduleId scheduleId(long i) {
        return new ScheduleId(i);
    }
}
