package com.falco.workshop.tdd.reservation.domain.slots;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;

import java.util.List;

public interface FreeSlotRepository {
    List<FreeSlot> find(DateInterval interval);

    List<FreeSlot> findById(ScheduleId id, DateInterval interval);

    void delete(FreeSlot freeSlot);

    void saveAll(List<FreeSlot> freeSlots);
}
