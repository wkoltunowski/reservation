package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlot;
import com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlotRepository;
import com.falco.workshop.tdd.reservation.domain.slots.VisitSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Component
public class SlotReservationService {
    private final FreeScheduleSlotRepository freeScheduleSlotRepository;

    @Autowired
    public SlotReservationService(FreeScheduleSlotRepository freeScheduleSlotRepository) {
        this.freeScheduleSlotRepository = freeScheduleSlotRepository;
    }

    public void reserveSlot(VisitSlot scheduleSlot) {
        List<FreeScheduleSlot> oldScheduleSlots = freeScheduleSlotRepository.findByScheduleIdIntersecting(scheduleSlot.id(), scheduleSlot.interval());
        checkArgument(oldScheduleSlots.size() == 1, "ScheduleSlot already taken!");
        FreeScheduleSlot oldScheduleSlot = oldScheduleSlots.get(0);
        checkArgument(oldScheduleSlot.interval().encloses(scheduleSlot.interval()), "ScheduleSlot already taken!");
        freeScheduleSlotRepository.delete(oldScheduleSlot);
        freeScheduleSlotRepository.saveAll(oldScheduleSlot.cutInterval(scheduleSlot.interval()));
    }
}
