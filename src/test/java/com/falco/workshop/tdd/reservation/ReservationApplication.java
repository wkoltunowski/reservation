package com.falco.workshop.tdd.reservation;

import com.falco.workshop.tdd.reservation.application.DefineScheduleService;
import com.falco.workshop.tdd.reservation.application.FindFreeSlotsService;
import com.falco.workshop.tdd.reservation.application.PatientReservationService;
import com.falco.workshop.tdd.reservation.application.SlotReservationService;
import com.falco.workshop.tdd.reservation.domain.reservation.*;
import com.falco.workshop.tdd.reservation.domain.schedule.DailyDoctorSchedule;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.slots.FreeSlot;
import com.falco.workshop.tdd.reservation.infrastructure.reservation.InMemoryReservationRepository;
import com.falco.workshop.tdd.reservation.infrastructure.schedule.InMemoryScheduleRepository;
import com.falco.workshop.tdd.reservation.infrastructure.slots.InMemoryFreeSlotRepository;
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
        InMemoryFreeSlotRepository freeSlotRepository = new InMemoryFreeSlotRepository();
        InMemoryScheduleRepository scheduleRepository = new InMemoryScheduleRepository();
        return new ReservationApplication(
                reservationRepository,
                new PatientReservationService(reservationRepository, new SlotReservationService(freeSlotRepository)),
                new FindFreeSlotsService(freeSlotRepository, scheduleRepository),
                new DefineScheduleService(scheduleRepository, freeSlotRepository, reservationRepository),
                () -> {
                }
        );
    }

    public void stop() {
        stop.run();
    }

    public void defineSchedule(DailyDoctorSchedule schedule) {
        defineScheduleService.defineSchedule(schedule);
    }

    public List<FreeSlot> findFreeSlots(LocalDateTime startingFrom) {
        return findFreeSlotsService.findFreeSlots(fromTo(startingFrom, startingFrom.toLocalDate().plusDays(1).atStartOfDay()));
    }

    public ReservationId reserveSlot(PatientId patientId, FreeSlot freeSlot) {
        return patientReservationService.reserve(patientSlot(patientId, freeSlot)).id();
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

    public void updateSchedule(DailyDoctorSchedule schedule) {
        defineScheduleService.updateSchedule(schedule);
    }
}
