package com.falco.workshop.tdd.reservation.application.slots;

import com.falco.workshop.tdd.reservation.domain.schedule.Schedule;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleRepository;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleStatus;
import com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlot;
import com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static com.falco.workshop.tdd.reservation.domain.DateInterval.fromTo;
import static java.util.Collections.emptyList;

@Component
public class GenerateFreeSlotsService {
    private final FreeScheduleSlotRepository freeScheduleSlotRepository;
    private final ScheduleRepository scheduleRepository;
    private final SlotsEvents slotsEvents;

    @Autowired
    public GenerateFreeSlotsService(FreeScheduleSlotRepository freeScheduleSlotRepository,
                                    ScheduleRepository scheduleRepository,
                                    SlotsEvents slotsEvents) {
        this.freeScheduleSlotRepository = freeScheduleSlotRepository;
        this.scheduleRepository = scheduleRepository;
        this.slotsEvents = slotsEvents;
    }

    public void regenerateSlots(ScheduleId id) {
        freeScheduleSlotRepository.deleteByScheduleId(id);
        freeScheduleSlotRepository.saveAll(regenerate(scheduleRepository.findById(id)));
        slotsEvents.slotsRegenerated(id);
    }

    private List<FreeScheduleSlot> regenerate(Schedule schedule) {
        if (!schedule.status().equals(ScheduleStatus.CANCELLED))
            return schedule.generateSlots(fromTo(LocalDate.of(2018, 1, 1).atTime(0, 0), LocalDate.of(2019, 1, 1).atTime(0, 0)));
        else
            return emptyList();
    }
}
