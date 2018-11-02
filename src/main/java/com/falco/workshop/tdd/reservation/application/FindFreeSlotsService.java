package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.ScheduleRepository;
import com.falco.workshop.tdd.reservation.domain.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class FindFreeSlotsService {
    private final ScheduleRepository scheduleRepository;

    @Autowired
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
