package com.falco.workshop.tdd.reservation.application.slots;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleRepository;
import com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlot;
import com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlotRepository;
import com.falco.workshop.tdd.reservation.domain.slots.VisitSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    public List<VisitSlot> findFreeSlots(DateInterval interval, int visitsLimit) {
        List<VisitSlot> results = new ArrayList<>();
        Page<FreeScheduleSlot> page = freeScheduleSlotRepository.findIntersecting(interval, PageRequest.of(0, 2));
        while (results.size() < visitsLimit && page.hasContent()) {
            results.addAll(page.stream()
                    .map(s -> s.createVisitSlots(scheduleRepository.findById(s.id()).visitDuration()))
                    .flatMap(Collection::stream)
                    .limit(visitsLimit - results.size())
                    .collect(toList()));
            if (results.size()< visitsLimit && page.hasNext())
                page = freeScheduleSlotRepository.findIntersecting(interval, page.nextPageable());
            else
                page = Page.empty();
        }


        return results;
    }
}
