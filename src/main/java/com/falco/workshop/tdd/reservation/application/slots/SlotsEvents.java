package com.falco.workshop.tdd.reservation.application.slots;

import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;

public interface SlotsEvents {
    void slotsRegenerated(ScheduleId id);
}
