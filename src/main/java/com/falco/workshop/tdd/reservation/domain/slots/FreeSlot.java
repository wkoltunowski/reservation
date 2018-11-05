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

public class FreeSlot {
    private final ScheduleId scheduleId;
    private final DateInterval interval;

    private FreeSlot(ScheduleId scheduleId, DateInterval dateInterval) {
        this.scheduleId = scheduleId;
        this.interval = dateInterval;
    }

    public ScheduleId id() {
        return scheduleId;
    }

    public DateInterval interval() {
        return interval;
    }

    public List<FreeSlot> splitBy(FreeSlot freeSlot) {
        FreeSlot before = FreeSlot.slot(scheduleId, DateInterval.fromTo(interval.start(), freeSlot.interval().start()));
        FreeSlot after = FreeSlot.slot(scheduleId, DateInterval.fromTo(freeSlot.interval().end(), (interval.end())));
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

    public List<FreeSlot> splitBy(Duration duration) {
        DateInterval slotInterval = DateInterval.fromTo(interval.start(), interval.start().plus(duration));
        List<FreeSlot> freeSlots = new ArrayList<>();
        while (interval().encloses(slotInterval)) {
            freeSlots.add(FreeSlot.slot(scheduleId, slotInterval));
            slotInterval = DateInterval.fromTo(slotInterval.end(), slotInterval.end().plus(duration));
        }
        return freeSlots;
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

    public static FreeSlot slot(ScheduleId scheduleId, DateInterval interval) {
        return new FreeSlot(scheduleId, interval);
    }

    public static FreeSlot slot(ScheduleId scheduleId, String dayFromTo) {
        return FreeSlot.slot(scheduleId, DateInterval.fromTo(dayFromTo));
    }
}
