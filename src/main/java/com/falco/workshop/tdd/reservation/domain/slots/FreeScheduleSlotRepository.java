package com.falco.workshop.tdd.reservation.domain.slots;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;

import java.util.List;

public interface FreeScheduleSlotRepository {
    List<FreeScheduleSlot> findIntersecting(DateInterval interval);

    List<FreeScheduleSlot> findByScheduleIdIntersecting(ScheduleId id, DateInterval interval);

    List<FreeScheduleSlot> findByScheduleId(ScheduleId id);

    void delete(FreeScheduleSlot scheduleSlot);

    void saveAll(List<FreeScheduleSlot> scheduleSlots);

    void deleteByScheduleId(ScheduleId id);
}
