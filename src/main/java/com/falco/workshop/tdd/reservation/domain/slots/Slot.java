package com.falco.workshop.tdd.reservation.domain.slots;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class Slot {
    private final ScheduleId scheduleId;
    private final DateInterval interval;

    private Slot(ScheduleId scheduleId, DateInterval dateInterval) {
        this.scheduleId = scheduleId;
        this.interval = dateInterval;
    }

    public ScheduleId id() {
        return scheduleId;
    }

    public DateInterval interval() {
        return interval;
    }

    public List<Slot> splitBy(Slot slot) {
        Slot before = Slot.slot(scheduleId, DateInterval.parse(interval.start(), slot.interval().start()));
        Slot after = Slot.slot(scheduleId, DateInterval.parse(slot.interval().end(), (interval.end())));
        if (!before.isEmpty() && !after.isEmpty()) {
            return ImmutableList.of(before, after);
        }
        if (!before.isEmpty()) {
            return ImmutableList.of(before);
        }
        if (!after.isEmpty()) {
            return ImmutableList.of(after);
        }
        return ImmutableList.of();
    }

    private boolean isEmpty() {
        return interval().isEmpty();
    }

    public List<Slot> splitBy(Duration duration) {
        DateInterval slotInterval = DateInterval.parse(interval.start(), interval.start().plus(duration));
        List<Slot> slots = new ArrayList<>();
        while (interval().encloses(slotInterval)) {
            slots.add(Slot.slot(scheduleId, slotInterval));
            slotInterval = DateInterval.parse(slotInterval.end(), slotInterval.end().plus(duration));
        }
        return slots;
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
}
