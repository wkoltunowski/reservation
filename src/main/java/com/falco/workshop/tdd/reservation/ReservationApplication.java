package com.falco.workshop.tdd.reservation;

import com.falco.workshop.tdd.reservation.domain.*;
import com.falco.workshop.tdd.reservation.infrastructure.InMemoryReservationRepository;
import com.falco.workshop.tdd.reservation.infrastructure.InMemoryScheduleRepository;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ReservationApplication {
    private ScheduleRepository scheduleRepository = new InMemoryScheduleRepository();
    private ReservationRepository reservationRepository = new InMemoryReservationRepository();

    public static ReservationApplication start() {
        return new ReservationApplication();
    }

    public void defineSchedule(DailyDoctorSchedule schedule) {
        this.scheduleRepository.save(schedule);
    }

    public List<Slot> findFreeSlots(LocalDateTime startingFrom) {
        return this.scheduleRepository
                .findAll().stream()
                .flatMap(schedule -> schedule.findFreeSlots(startingFrom).stream())
                .collect(toList());
    }

    public ReservationId reserveSlot(Slot slot, PatientId patientId) {
        DailyDoctorSchedule schedule = scheduleRepository.findById(slot.id());
        schedule.reserveSlot(slot);
        scheduleRepository.update(schedule);
        Reservation reservation = Reservation.reservation(ReservationDetails.reservationDetails(patientId, slot));
        reservationRepository.save(reservation);
        return reservation.id();
    }

    public List<ReservationDetails> findReservationsFor(String day) {
        return reservationRepository.findReservations(DateInterval.parse(day + " 00:00-23:59")).stream().map(Reservation::details).collect(toList());
    }
}
