package com.falco.workshop.tdd.reservation;

import com.falco.workshop.tdd.reservation.application.DefineScheduleService;
import com.falco.workshop.tdd.reservation.application.FindFreeSlotsService;
import com.falco.workshop.tdd.reservation.application.PatientReservationService;
import com.falco.workshop.tdd.reservation.domain.*;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientId;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientSlot;
import com.falco.workshop.tdd.reservation.domain.reservation.ReservationRepository;
import com.falco.workshop.tdd.reservation.domain.schedule.DailyDoctorSchedule;
import com.falco.workshop.tdd.reservation.domain.slots.FreeSlot;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static com.falco.workshop.tdd.reservation.domain.DateInterval.fromTo;
import static com.falco.workshop.tdd.reservation.domain.reservation.PatientSlot.patientSlot;
import static java.util.stream.Collectors.toList;

public class ReservationApplication {
    private final ReservationRepository reservationRepository;
    private final PatientReservationService patientReservationService;
    private final FindFreeSlotsService findFreeSlotsService;
    private final DefineScheduleService defineScheduleService;
    private final ConfigurableApplicationContext context;

    public ReservationApplication() {
        context = SpringApplication.run(SpringApplicationRunner.class);
        reservationRepository = context.getBean(ReservationRepository.class);
        patientReservationService = context.getBean(PatientReservationService.class);
        findFreeSlotsService = context.getBean(FindFreeSlotsService.class);
        defineScheduleService = context.getBean(DefineScheduleService.class);
    }

    public static ReservationApplication start() {
        return new ReservationApplication();
    }

    public void stop() {
        context.close();
    }

    public void defineSchedule(DailyDoctorSchedule schedule) {
        defineScheduleService.defineSchedule(schedule);
    }

    public List<FreeSlot> findFreeSlots(LocalDateTime startingFrom) {
        return this.findFreeSlotsService.findFreeSlots(fromTo(startingFrom, startingFrom.toLocalDate().plusDays(1).atStartOfDay()));
    }

    public void reserveSlot(PatientId patientId, FreeSlot freeSlot) {
        patientReservationService.reserve(patientSlot(patientId, freeSlot));
    }

    public List<PatientSlot> findReservationsFor(String day) {
        return reservationRepository.findReservations(fromTo(day + " 00:00-23:59")).stream().map(PatientReservation::details).collect(toList());
    }
}
