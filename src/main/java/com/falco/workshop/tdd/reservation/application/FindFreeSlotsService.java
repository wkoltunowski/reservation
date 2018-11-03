package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.FreeSlotRepository;
import com.falco.workshop.tdd.reservation.domain.ScheduleRepository;
import com.falco.workshop.tdd.reservation.domain.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class FindFreeSlotsService {
    private final FreeSlotRepository freeSlotRepository;
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public FindFreeSlotsService(FreeSlotRepository freeSlotRepository, ScheduleRepository scheduleRepository) {
        this.freeSlotRepository = freeSlotRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public List<Slot> findFreeSlots(DateInterval interval) {
        return this.freeSlotRepository.find(interval).stream()
                .map(s -> s.splitBy(scheduleRepository.findById(s.id()).visitDuration()))
                .flatMap(Collection::stream)
                .collect(toList());
    }
}
