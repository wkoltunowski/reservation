package com.falco.workshop.tdd.reservation;

import com.falco.workshop.tdd.reservation.application.*;
import com.falco.workshop.tdd.reservation.application.slots.FindFreeSlotsService;
import com.falco.workshop.tdd.reservation.application.slots.GenerateFreeSlotsService;
import com.falco.workshop.tdd.reservation.application.slots.ReserveFreeSlotService;
import com.falco.workshop.tdd.reservation.infrastructure.SynchronousScheduleEvents;
import com.falco.workshop.tdd.reservation.infrastructure.SynchronousSlotsEvents;
import com.falco.workshop.tdd.reservation.domain.TimeInterval;
import com.falco.workshop.tdd.reservation.domain.reservation.*;
import com.falco.workshop.tdd.reservation.domain.schedule.Schedule;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.slots.VisitSlot;
import com.falco.workshop.tdd.reservation.infrastructure.reservation.InMemoryReservationRepository;
import com.falco.workshop.tdd.reservation.infrastructure.schedule.InMemoryScheduleRepository;
import com.falco.workshop.tdd.reservation.infrastructure.slots.InMemoryFreeScheduleSlotRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;
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

    private final Runnable stop;


    private ReservationApplication(ReservationRepository reservationRepository,
                                   PatientReservationService patientReservationService,
                                   FindFreeSlotsService findFreeSlotsService,
                                   DefineScheduleService defineScheduleService,
                                   Runnable stop) {
        this.reservationRepository = reservationRepository;
        this.patientReservationService = patientReservationService;
        this.findFreeSlotsService = findFreeSlotsService;
        this.defineScheduleService = defineScheduleService;
        this.stop = stop;
    }

    public static ReservationApplication startSpring() {
        ConfigurableApplicationContext context = SpringApplication.run(SpringApplicationRunner.class);
        return new ReservationApplication(
                context.getBean(ReservationRepository.class),
                context.getBean(PatientReservationService.class),
                context.getBean(FindFreeSlotsService.class),
                context.getBean(DefineScheduleService.class),
                context::close
        );
    }

    public static ReservationApplication startInMemory() {
        InMemoryReservationRepository reservationRepository = new InMemoryReservationRepository();
        InMemoryFreeScheduleSlotRepository freeSlotRepository = new InMemoryFreeScheduleSlotRepository();
        InMemoryScheduleRepository scheduleRepository = new InMemoryScheduleRepository();
        PatientReservationService patientReservationService = new PatientReservationService(reservationRepository, new ReserveFreeSlotService(freeSlotRepository));
        return new ReservationApplication(
                reservationRepository,
                patientReservationService,
                new FindFreeSlotsService(freeSlotRepository, scheduleRepository),
                new DefineScheduleService(scheduleRepository,
                        new SynchronousScheduleEvents(
                                new GenerateFreeSlotsService(freeSlotRepository, scheduleRepository, new SynchronousSlotsEvents(patientReservationService)))),
                () -> {
                }
        );
    }

    public void stop() {
        stop.run();
    }

    public void defineSchedule(Schedule schedule) {
        defineScheduleService.defineSchedule(schedule);
    }

    public List<VisitSlot> findFreeSlots(LocalDateTime startingFrom) {
        return findFreeSlotsService.findFreeSlots(fromTo(startingFrom, startingFrom.toLocalDate().plusDays(1).atStartOfDay()), 50);
    }

    public ReservationId reserveSlot(PatientId patientId, VisitSlot scheduleSlot) {
        return patientReservationService.reserve(patientSlot(patientId, scheduleSlot)).id();
    }

    public List<PatientSlot> findPatientSlots(String day) {
        return reservationRepository.findReservations(fromTo(day + " 00:00-23:59")).stream().map(PatientReservation::details).collect(toList());
    }

    public List<PatientReservation> findPatientReservations(String day) {
        return reservationRepository.findReservations(fromTo(day + " 00:00-23:59"));
    }

    public void deleteSchedule(ScheduleId scheduleId) {
        defineScheduleService.deleteSchedule(scheduleId);
    }

    public void updateSchedule(ScheduleId id, TimeInterval workingHours, Duration visitDuration) {
        defineScheduleService.updateSchedule(id, workingHours, visitDuration);
    }
}
