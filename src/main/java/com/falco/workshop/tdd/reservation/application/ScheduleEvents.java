package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;

public interface ScheduleEvents {
    void scheduleDefined(ScheduleId id);

    void scheduleUpdated(ScheduleId id);

    void scheduleCancelled(ScheduleId id);
}
