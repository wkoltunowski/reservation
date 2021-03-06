package com.falco.workshop.tdd.reservation.domain.slots;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FreeScheduleSlotRepository {
    Page<FreeScheduleSlot> findIntersecting(DateInterval interval, Pageable pageable);

    Optional<FreeScheduleSlot> findByScheduleIdEnclosing(ScheduleId id, DateInterval interval);

    List<FreeScheduleSlot> findByScheduleId(ScheduleId id);

    void delete(FreeScheduleSlot scheduleSlot);

    void saveAll(List<FreeScheduleSlot> scheduleSlots);

    void deleteByScheduleId(ScheduleId id);
}
