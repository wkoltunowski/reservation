package com.falco.workshop.tdd.reservation.domain.slots;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;

import java.util.List;

public interface FreeSlotRepository {
    List<Slot> find(DateInterval interval);

    Slot findById(ScheduleId id, DateInterval interval);

    void delete(Slot slot);

    void saveAll(List<Slot> slots);
}
