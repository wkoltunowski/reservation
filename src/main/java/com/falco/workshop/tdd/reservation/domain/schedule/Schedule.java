package com.falco.workshop.tdd.reservation.domain.schedule;


import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.TimeInterval;
import com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlot;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.falco.workshop.tdd.reservation.domain.schedule.ScheduleStatus.INITIAL;
import static com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlot.freeScheduleSlot;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class Schedule {
    private final ScheduleId scheduleId;
    private Duration visitDuration;
    private TimeInterval workingHours;
    private ScheduleStatus status;

    private Schedule(ScheduleId scheduleId, TimeInterval workingHours, Duration visitDuration, ScheduleStatus status) {
        this.scheduleId = scheduleId;
        this.visitDuration = visitDuration;
        this.workingHours = workingHours;
        this.status = status;
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

    public ScheduleStatus status() {
        return status;
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

    private List<FreeScheduleSlot> dailySlots(LocalDate day) {
        return singletonList(freeScheduleSlot(scheduleId, workingHours.toDateInterval(day)));
    }

    public void cancel() {
        this.status = ScheduleStatus.CANCELLED;
    }

    public void updateVisitDuration(Duration visitDuration) {
        this.visitDuration = visitDuration;
    }

    public void updateWorkingHours(TimeInterval workingHours) {
        this.workingHours = workingHours;
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

    public static Schedule schedule(ScheduleId scheduleId, TimeInterval workingHours, Duration visitDuration, ScheduleStatus status) {
        return new Schedule(scheduleId, workingHours, visitDuration, status);
    }

    public static Schedule newSchedule(TimeInterval workingHours, Duration visitDuration) {
        return new Schedule(null, workingHours, visitDuration, INITIAL);
    }
}
