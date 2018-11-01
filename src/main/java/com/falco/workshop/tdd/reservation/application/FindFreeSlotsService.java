package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.ScheduleRepository;
import com.falco.workshop.tdd.reservation.domain.Slot;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class FindFreeSlotsService {
    private final ScheduleRepository scheduleRepository;

    public FindFreeSlotsService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<Slot> findFreeSlots(LocalDateTime startingFrom) {
        return this.scheduleRepository
                .findAll().stream()
                .flatMap(schedule -> schedule.findFreeSlots(startingFrom).stream())
                .collect(toList());
    }
}
