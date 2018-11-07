package com.falco.workshop.tdd.reservation.infrastructure;

import com.falco.workshop.tdd.reservation.application.PatientReservationService;
import com.falco.workshop.tdd.reservation.application.slots.SlotsEvents;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SynchronousSlotsEvents implements SlotsEvents {
    private final PatientReservationService patientReservationService;

    @Autowired
    public SynchronousSlotsEvents(PatientReservationService patientReservationService) {
        this.patientReservationService = patientReservationService;
    }

    @Override
    public void slotsRegenerated(ScheduleId id) {
        patientReservationService.reReservePatientReservations(id);
    }
}
