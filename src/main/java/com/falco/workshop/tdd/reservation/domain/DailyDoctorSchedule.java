package com.falco.workshop.tdd.reservation.domain;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DailyDoctorSchedule {
    private final ScheduleId id;
    private final Duration visitDuration;
    private final TimeInterval workingHours;

    public DailyDoctorSchedule(ScheduleId scheduleId, TimeInterval workingHours, Duration visitDuration) {
        this.id = scheduleId;
        this.visitDuration = visitDuration;
        this.workingHours = workingHours;
    }

    public List<Slot> generateSlots(DateInterval interval) {
        List<Slot> slots = new ArrayList<>();
        LocalDateTime day = interval.start();
        while (!day.isAfter(interval.end())) {
            slots.addAll(dailySlots(day));
            day = day.plusDays(1);
        }
        return slots;
    }

    private List<Slot> dailySlots(LocalDateTime day) {
        List<Slot> slots = new ArrayList<>();
        DateInterval scheduleInterval = workingHours.toDateInterval(day.toLocalDate());
        DateInterval slotInterval = DateInterval.parse(scheduleInterval.start(), visitDuration);
        Slot slot = slotFor(slotInterval);
        while (slot.interval().start().isBefore(day)) {
            slot = Slot.slot(slot.id(), slot.interval().plus(visitDuration));
        }

        while (scheduleInterval.encloses(slot.interval())) {
            slots.add(slot);
            slot = Slot.slot(slot.id(), slot.interval().plus(visitDuration));
        }
        return slots;
    }

    private Slot slotFor(DateInterval interval) {
        return Slot.slot(id, interval);
    }

    public static DailyDoctorSchedule dailyDoctorSchedule(ScheduleId scheduleId, TimeInterval workingHours, Duration visitDuration) {
        return new DailyDoctorSchedule(scheduleId, workingHours, visitDuration);
    }

    public ScheduleId id() {
        return id;
    }
}
