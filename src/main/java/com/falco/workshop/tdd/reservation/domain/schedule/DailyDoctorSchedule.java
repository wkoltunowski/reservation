package com.falco.workshop.tdd.reservation.domain.schedule;


import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.slots.FreeSlot;
import com.falco.workshop.tdd.reservation.domain.TimeInterval;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class DailyDoctorSchedule {
    private final ScheduleId id;
    private final Duration visitDuration;
    private final TimeInterval workingHours;

    private DailyDoctorSchedule(ScheduleId scheduleId, TimeInterval workingHours, Duration visitDuration) {
        this.id = scheduleId;
        this.visitDuration = visitDuration;
        this.workingHours = workingHours;
    }

    public ScheduleId id() {
        return id;
    }

    public TimeInterval workingHours() {
        return workingHours;
    }

    public Duration visitDuration() {
        return visitDuration;
    }

    public List<FreeSlot> generateSlots(DateInterval interval) {
        List<FreeSlot> freeSlots = new ArrayList<>();
        LocalDate day = interval.start().toLocalDate();
        while (!day.isAfter(interval.end().toLocalDate())) {
            freeSlots.addAll(dailySlots(day).stream().filter(s -> interval.encloses(s.interval())).collect(toList()));
            day = day.plusDays(1);
        }
        return freeSlots;
    }

    private List<FreeSlot> dailySlots(LocalDate day) {
        return singletonList(FreeSlot.slot(id, workingHours.toDateInterval(day)));
    }

    public static DailyDoctorSchedule dailyDoctorSchedule(ScheduleId scheduleId, TimeInterval workingHours, Duration visitDuration) {
        return new DailyDoctorSchedule(scheduleId, workingHours, visitDuration);
    }

    public static DailyDoctorSchedule dailyDoctorSchedule(TimeInterval workingHours, Duration visitDuration) {
        return new DailyDoctorSchedule(null, workingHours, visitDuration);
    }
}
