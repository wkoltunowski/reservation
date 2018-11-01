package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.*;

public class SlotReservationService {
    private final ScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;

    public SlotReservationService(ScheduleRepository scheduleRepository, ReservationRepository reservationRepository) {
        this.scheduleRepository = scheduleRepository;
        this.reservationRepository = reservationRepository;
    }

    public ReservationId reserveSlot(Reservation reservation) {
        DailyDoctorSchedule schedule = scheduleRepository.findById(reservation.details().slot().id());
        schedule.reserveSlot(reservation.details().slot());
        scheduleRepository.update(schedule);

        reservationRepository.save(reservation);
        return reservation.id();

    }
}
