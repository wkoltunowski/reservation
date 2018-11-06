package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleEvents {

    private final PatientReservationService patientReservationService;
    private final SlotReservationService slotReservationService;

    @Autowired
    public ScheduleEvents(PatientReservationService patientReservationService,
                          SlotReservationService slotReservationService) {
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
        slotReservationService.regenerateSlots(id);
        patientReservationService.reReserveAllReservations(id);
    }
}
