package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleEvents {

    private final FreeScheduleSlotRepository freeScheduleSlotRepository;
    private final PatientReservationService patientReservationService;
    private final SlotReservationService slotReservationService;

    @Autowired
    public ScheduleEvents(FreeScheduleSlotRepository freeScheduleSlotRepository,
                          PatientReservationService patientReservationService,
                          SlotReservationService slotReservationService) {
        this.freeScheduleSlotRepository = freeScheduleSlotRepository;
        this.patientReservationService = patientReservationService;
        this.slotReservationService = slotReservationService;
    }

    public void scheduleDefined(ScheduleId id) {
        slotReservationService.regenerateSlots(id);
    }

    public void scheduleUpdated(ScheduleId id) {
        slotReservationService.regenerateSlots(id);
        patientReservationService.reReserveAllReservations(id);
    }

    public void scheduleCancelled(ScheduleId id) {
        freeScheduleSlotRepository.deleteByScheduleId(id);
        patientReservationService.reReserveAllReservations(id);
    }
}
