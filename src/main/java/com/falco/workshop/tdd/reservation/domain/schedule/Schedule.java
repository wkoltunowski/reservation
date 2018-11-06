package com.falco.workshop.tdd.reservation.domain.schedule;


import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.TimeInterval;
import com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlot;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlot.freeScheduleSlot;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class Schedule {
    private final ScheduleId scheduleId;
    private final Duration visitDuration;
    private final TimeInterval workingHours;

    private Schedule(ScheduleId scheduleId, TimeInterval workingHours, Duration visitDuration) {
        this.scheduleId = scheduleId;
        this.visitDuration = visitDuration;
        this.workingHours = workingHours;
    }

    public ScheduleId id() {
        return scheduleId;
    }

    public TimeInterval workingHours() {
        return workingHours;
    }

    public Duration visitDuration() {
        return visitDuration;
    }

    public List<FreeScheduleSlot> generateSlots(DateInterval interval) {
        List<FreeScheduleSlot> scheduleSlots = new ArrayList<>();
        LocalDate day = interval.start().toLocalDate();
        while (!day.isAfter(interval.end().toLocalDate())) {
            scheduleSlots.addAll(dailySlots(day).stream().filter(s -> interval.encloses(s.interval())).collect(toList()));
            day = day.plusDays(1);
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
        return ToStringBuilder.reflectionToString(this, JSON_STYLE);
    }

    private List<FreeScheduleSlot> dailySlots(LocalDate day) {
        return singletonList(freeScheduleSlot(scheduleId, workingHours.toDateInterval(day)));
    }

    public static Schedule schedule(ScheduleId scheduleId, TimeInterval workingHours, Duration visitDuration) {
        return new Schedule(scheduleId, workingHours, visitDuration);
    }

    public static Schedule schedule(TimeInterval workingHours, Duration visitDuration) {
        return new Schedule(null, workingHours, visitDuration);
    }
}
