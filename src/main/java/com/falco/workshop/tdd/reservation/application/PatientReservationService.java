package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.PatientReservation;
import com.falco.workshop.tdd.reservation.domain.ReservationId;
import com.falco.workshop.tdd.reservation.domain.ReservationRepository;

public class PatientReservationService {
    private final SlotReservationService slotReservationService;
    private final ReservationRepository reservationRepository;

    public PatientReservationService(ReservationRepository reservationRepository,
                                     SlotReservationService slotReservationService) {
        this.slotReservationService = slotReservationService;
        this.reservationRepository = reservationRepository;
    }

    public ReservationId reserve(PatientReservation patientReservation) {
        slotReservationService.reserveSlot(patientReservation.details().slot());
        return saveReservation(patientReservation);
    }


    private ReservationId saveReservation(PatientReservation patientReservation) {
        reservationRepository.save(patientReservation);
        return patientReservation.id();
    }
}
