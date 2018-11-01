package com.falco.workshop.tdd.reservation.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DailyDoctorSchedule {
    private final ScheduleId id;
    private final Duration visitDuration;
    private final TimeInterval workingHours;
    private List<Slot> reservedSlots = new ArrayList<>();

    public DailyDoctorSchedule(ScheduleId scheduleId, TimeInterval workingHours, Duration visitDuration) {
        this.id = scheduleId;
        this.visitDuration = visitDuration;
        this.workingHours = workingHours;
    }

    public List<Slot> findFreeSlots(LocalDateTime startingFrom) {
        List<Slot> slots = new ArrayList<>();

        DateInterval scheduleInterval = workingHours.toDateInterval(startingFrom.toLocalDate());
        DateInterval slotInterval = DateInterval.parse(scheduleInterval.start(), visitDuration);
        Slot slot = slotFor(slotInterval);
        while (slot.interval().start().isBefore(startingFrom)) {
            slot = Slot.slot(slot.id(), slot.interval().plus(visitDuration));
        }

        while (scheduleInterval.encloses(slot.interval()) && !reservedSlots.contains(slot)) {
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

    public void reserveSlot(Slot slot) {
        reservedSlots.add(slot);
    }
}
