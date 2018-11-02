package com.falco.workshop.tdd.reservation.domain;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.falco.workshop.tdd.reservation.domain.Slot.slot;

@Entity
public class DailyDoctorSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private ScheduleId id;
    private Duration visitDuration;
    private TimeInterval workingHours;

    DailyDoctorSchedule() {
    }

    DailyDoctorSchedule(ScheduleId scheduleId, TimeInterval workingHours, Duration visitDuration) {
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

    private List<Slot> dailySlots(LocalDateTime startFrom) {
        List<Slot> slots = new ArrayList<>();
        DateInterval scheduleInterval = workingHours.toDateInterval(startFrom.toLocalDate());
        DateInterval slotInterval = DateInterval.parse(scheduleInterval.start(), visitDuration);
        while (slotInterval.start().isBefore(startFrom)) {
            slotInterval = slotInterval.plus(visitDuration);
        }
        while (scheduleInterval.encloses(slotInterval)) {
            slots.add(slot(id, slotInterval));
            slotInterval = slotInterval.plus(visitDuration);
        }
        return slots;
    }

    public ScheduleId id() {
        return id;
    }

    public static DailyDoctorSchedule dailyDoctorSchedule(ScheduleId scheduleId, TimeInterval workingHours, Duration visitDuration) {
        return new DailyDoctorSchedule(scheduleId, workingHours, visitDuration);
    }
}
