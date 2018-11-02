package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.PatientSlot;
import com.falco.workshop.tdd.reservation.domain.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.falco.workshop.tdd.reservation.domain.PatientReservation.reservation;

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
        reservationRepository.save(reservation(patientSlot));
    }


}
