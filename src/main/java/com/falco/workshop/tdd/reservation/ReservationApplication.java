package com.falco.workshop.tdd.reservation;

import com.falco.workshop.tdd.reservation.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class ReservationApplication {
    private DailyDoctorSchedule schedule;
    private Map<ReservationId, ReservationDetails> reservations = new HashMap<>();

    public static ReservationApplication start() {
        return new ReservationApplication();
    }

    public void defineSchedule(DailyDoctorSchedule schedule) {
        this.schedule = schedule;
    }

    public List<Slot> findFreeSlots(LocalDateTime startingFrom) {
        return schedule.findFreeSlots(startingFrom);
    }

    public ReservationId reserveSlot(Slot slot, PatientId patientId) {
        ReservationId reservationId = ReservationId.newId();
        schedule.reserveSlot(slot);
        reservations.put(reservationId, ReservationDetails.reservationDetails(patientId, slot));
        return reservationId;
    }

    public List<ReservationDetails> findReservationsFor(String day) {
        return reservations.values().stream().filter(r -> r.slot().interval().start().toLocalDate().equals(LocalDate.parse(day))).collect(toList());
    }
}
