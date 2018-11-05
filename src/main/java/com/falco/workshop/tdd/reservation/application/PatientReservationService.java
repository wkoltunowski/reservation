package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientSlot;
import com.falco.workshop.tdd.reservation.domain.reservation.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation.reservation;

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

    public PatientReservation reserve(PatientSlot patientSlot) {
        slotReservationService.reserveSlot(patientSlot.slot());
        PatientReservation reservation = reservation(patientSlot);
        return reservationRepository.save(reservation);
    }
}
