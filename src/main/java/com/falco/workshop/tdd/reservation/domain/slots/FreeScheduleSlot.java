package com.falco.workshop.tdd.reservation.domain.slots;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.falco.workshop.tdd.reservation.domain.DateInterval.fromTo;
import static com.falco.workshop.tdd.reservation.domain.slots.VisitSlot.visitSlot;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class FreeScheduleSlot {
    private final ScheduleId scheduleId;
    private final DateInterval interval;

    private FreeScheduleSlot(ScheduleId scheduleId, DateInterval dateInterval) {
        this.scheduleId = scheduleId;
        this.interval = dateInterval;
    }

    public ScheduleId id() {
        return scheduleId;
    }

    public DateInterval interval() {
        return interval;
    }

    public List<FreeScheduleSlot> cutInterval(DateInterval splitInterval) {
        FreeScheduleSlot before = freeScheduleSlot(scheduleId, fromTo(interval.start(), splitInterval.start()));
        FreeScheduleSlot after = freeScheduleSlot(scheduleId, fromTo(splitInterval.end(), interval.end()));
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

    public List<VisitSlot> createVisitSlots(Duration duration) {
        DateInterval slotInterval = fromTo(interval.start(), interval.start().plus(duration));
        List<VisitSlot> scheduleSlots = new ArrayList<>();
        while (interval().encloses(slotInterval)) {
            scheduleSlots.add(visitSlot(scheduleId, slotInterval));
            slotInterval = fromTo(slotInterval.end(), slotInterval.end().plus(duration));
        }
        return scheduleSlots;
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

    public static FreeScheduleSlot freeScheduleSlot(ScheduleId scheduleId, DateInterval interval) {
        return new FreeScheduleSlot(scheduleId, interval);
    }

    public static FreeScheduleSlot freeScheduleSlot(ScheduleId scheduleId, String dayFromTo) {
        return freeScheduleSlot(scheduleId, fromTo(dayFromTo));
    }
}
