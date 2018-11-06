package com.falco.workshop.tdd.reservation.infrastructure;

import com.falco.workshop.tdd.reservation.application.ScheduleEvents;
import com.falco.workshop.tdd.reservation.application.slots.GenerateFreeSlotsService;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SynchronousScheduleEvents implements ScheduleEvents {
    private final GenerateFreeSlotsService slotGenerationService;

    @Autowired
    public SynchronousScheduleEvents(GenerateFreeSlotsService slotGenerationService) {
        this.slotGenerationService = slotGenerationService;
    }

    @Override
    public void scheduleDefined(ScheduleId id) {
        slotGenerationService.regenerateSlots(id);
    }

    @Override
    public void scheduleUpdated(ScheduleId id) {
        slotGenerationService.regenerateSlots(id);
    }

    @Override
    public void scheduleCancelled(ScheduleId id) {
        slotGenerationService.regenerateSlots(id);
    }
}
