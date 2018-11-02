package com.falco.workshop.tdd.reservation.domain;

import java.time.LocalDateTime;
import java.util.List;

public interface FreeSlotRepository {
    List<Slot> find(DateInterval interval);

    Slot findById(ScheduleId id, LocalDateTime start);

    void delete(Slot slot);

    void saveAll(List<Slot> slots);
}
