package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.PatientReservation;
import com.falco.workshop.tdd.reservation.domain.PatientSlot;
import com.falco.workshop.tdd.reservation.domain.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientReservationService {
    private final SlotReservationService slotReservationService;
    private final ReservationRepository reservationRepository;

    @Autowired
    public PatientReservationService(ReservationRepository reservationRepository,
                                     SlotReservationService slotReservationService) {
        this.slotReservationService = slotReservationService;
        this.reservationRepository = reservationRepository;
    }

    public void reserve(PatientSlot patientSlot) {
        slotReservationService.reserveSlot(patientSlot.slot());
        saveReservation(patientSlot);
    }


    private void saveReservation(PatientSlot patientSlot) {
        PatientReservation reservation = PatientReservation.reservation(patientSlot);
        reservationRepository.save(reservation);
    }
}
