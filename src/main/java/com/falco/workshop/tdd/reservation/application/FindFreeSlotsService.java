package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleRepository;
import com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlotRepository;
import com.falco.workshop.tdd.reservation.domain.slots.VisitSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class FindFreeSlotsService {
    private final FreeScheduleSlotRepository freeScheduleSlotRepository;
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public FindFreeSlotsService(FreeScheduleSlotRepository freeScheduleSlotRepository, ScheduleRepository scheduleRepository) {
        this.freeScheduleSlotRepository = freeScheduleSlotRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public List<VisitSlot> findFreeSlots(DateInterval interval) {
        return this.freeScheduleSlotRepository.findIntersecting(interval).stream()
                .map(s -> s.createVisitSlots(scheduleRepository.findById(s.id()).visitDuration()))
                .flatMap(Collection::stream)
                .collect(toList());
    }
}
