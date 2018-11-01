package com.falco.workshop.tdd.reservation;

import com.falco.workshop.tdd.reservation.application.FindFreeSlotsService;
import com.falco.workshop.tdd.reservation.application.SlotReservationService;
import com.falco.workshop.tdd.reservation.domain.*;
import com.falco.workshop.tdd.reservation.infrastructure.InMemoryReservationRepository;
import com.falco.workshop.tdd.reservation.infrastructure.InMemoryScheduleRepository;

import java.time.LocalDateTime;
import java.util.List;

import static com.falco.workshop.tdd.reservation.domain.Reservation.reservation;
import static com.falco.workshop.tdd.reservation.domain.ReservationDetails.reservationDetails;
import static java.util.stream.Collectors.toList;

public class ReservationApplication {
    private ScheduleRepository scheduleRepository = new InMemoryScheduleRepository();
    private ReservationRepository reservationRepository = new InMemoryReservationRepository();
    private SlotReservationService slotReservationService = new SlotReservationService(scheduleRepository, reservationRepository);
    private FindFreeSlotsService findFreeSlotsService = new FindFreeSlotsService(scheduleRepository);

    public static ReservationApplication start() {
        return new ReservationApplication();
    }

    public void defineSchedule(DailyDoctorSchedule schedule) {
        this.scheduleRepository.save(schedule);
    }

    public List<Slot> findFreeSlots(LocalDateTime startingFrom) {
        return this.findFreeSlotsService.findFreeSlots(startingFrom);
    }

    public ReservationId reserveSlot(PatientId patientId, Slot slot) {
        return slotReservationService.reserveSlot(reservation(reservationDetails(patientId, slot)));
    }

    public List<ReservationDetails> findReservationsFor(String day) {
        return reservationRepository.findReservations(DateInterval.parse(day + " 00:00-23:59")).stream().map(Reservation::details).collect(toList());
    }
}
