package com.falco.workshop.tdd.reservation;

import com.falco.workshop.tdd.reservation.application.DefineScheduleService;
import com.falco.workshop.tdd.reservation.application.FindFreeSlotsService;
import com.falco.workshop.tdd.reservation.application.PatientReservationService;
import com.falco.workshop.tdd.reservation.domain.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static com.falco.workshop.tdd.reservation.domain.PatientSlot.patientSlot;
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

    public List<Slot> findFreeSlots(LocalDateTime startingFrom) {
        return this.findFreeSlotsService.findFreeSlots(DateInterval.parse(startingFrom, startingFrom.toLocalDate().plusDays(1).atStartOfDay()));
    }

    public void reserveSlot(PatientId patientId, Slot slot) {
        patientReservationService.reserve(patientSlot(patientId, slot));
    }

    public List<PatientSlot> findReservationsFor(String day) {
        return reservationRepository.findReservations(DateInterval.parse(day + " 00:00-23:59")).stream().map(PatientReservation::details).collect(toList());
    }
}
