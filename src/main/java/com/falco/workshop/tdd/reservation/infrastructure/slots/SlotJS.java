package com.falco.workshop.tdd.reservation.infrastructure.slots;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.slots.Slot;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class SlotJS {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long scheduleId;
    private LocalDateTime start;
    private LocalDateTime end;


    SlotJS() {
    }

    public SlotJS(Slot slot) {
        this.scheduleId = slot.id().id();
        this.start = slot.interval().start();
        this.end = slot.interval().end();
    }

    public Long getId() {
        return id;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public Slot toSlot() {
        return Slot.slot(new ScheduleId(scheduleId), DateInterval.parse(start, end));
    }

    public Long id() {
        return id;
    }
}
