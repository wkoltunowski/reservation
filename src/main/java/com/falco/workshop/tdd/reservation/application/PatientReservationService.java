package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.application.slots.ReserveFreeSlotService;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientSlot;
import com.falco.workshop.tdd.reservation.domain.reservation.ReservationRepository;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation.reservation;

@Component
public class PatientReservationService {
    private final ReserveFreeSlotService reserveFreeSlotService;
    private final ReservationRepository reservationRepository;

    @Autowired
    public PatientReservationService(ReservationRepository reservationRepository,
                                     ReserveFreeSlotService reserveFreeSlotService) {
        this.reserveFreeSlotService = reserveFreeSlotService;
        this.reservationRepository = reservationRepository;
    }

    public PatientReservation reserve(PatientSlot patientSlot) {
        reserveFreeSlotService.reserveSlot(patientSlot.slot());
        return reservationRepository.save(reservation(patientSlot));
    }


    public void syncReservationsWithSchedule(ScheduleId id) {
        List<PatientReservation> reservations = reservationRepository.findByScheduleId(id);

        Map<PatientSlot, PatientReservation> reservationBySlot = new HashMap<>();
        for (PatientReservation reservation : reservations) {
            reservationBySlot.put(reservation.details(), reservation);
        }
        List<PatientSlot> reservedSlots = reserveFreeSlotService.reserveSlots(reservationBySlot.keySet());

        Map<PatientSlot, PatientReservation> notReserved = new HashMap<>(reservationBySlot);
        for (PatientSlot reservedSlot : reservedSlots) {
            notReserved.remove(reservedSlot);
        }
        cancelAllReservations(notReserved.values());
    }

    private void cancelAllReservations(Collection<PatientReservation> reservations) {
        for (PatientReservation reservation : reservations) {
            reservation.cancel();
            reservationRepository.save(reservation);
        }
    }
}
