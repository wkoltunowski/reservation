package com.falco.workshop.tdd.reservation;

import com.falco.workshop.tdd.reservation.application.FindFreeSlotsService;
import com.falco.workshop.tdd.reservation.application.PatientReservationService;
import com.falco.workshop.tdd.reservation.domain.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static com.falco.workshop.tdd.reservation.domain.PatientReservation.reservation;
import static com.falco.workshop.tdd.reservation.domain.PatientSlot.reservationDetails;
import static java.util.stream.Collectors.toList;

public class ReservationApplication {
    private final ScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;
    private final PatientReservationService patientReservationService;
    private final FindFreeSlotsService findFreeSlotsService;
    private final FreeSlotRepository freeSlotRepository;
    private final ConfigurableApplicationContext context;

    public ReservationApplication() {
        context = SpringApplication.run(SpringApplicationRunner.class);
        scheduleRepository = context.getBean(ScheduleRepository.class);
        reservationRepository = context.getBean(ReservationRepository.class);
        patientReservationService = context.getBean(PatientReservationService.class);
        findFreeSlotsService = context.getBean(FindFreeSlotsService.class);
        freeSlotRepository = context.getBean(FreeSlotRepository.class);
    }

    public static ReservationApplication start() {
        return new ReservationApplication();
    }

    public void defineSchedule(DailyDoctorSchedule schedule) {
        this.scheduleRepository.save(schedule);
        LocalDateTime start = LocalDate.of(2018, 1, 1).atTime(0, 0);
        this.freeSlotRepository.saveAll(IntStream.range(0, 366).mapToObj(i -> schedule.findFreeSlots(start.plusDays(i))).flatMap(Collection::stream).collect(toList()));
    }

    public List<Slot> findFreeSlots(LocalDateTime startingFrom) {
        return this.findFreeSlotsService.findFreeSlots(DateInterval.parse(startingFrom, startingFrom.toLocalDate().plusDays(1).atStartOfDay()));
    }

    public ReservationId reserveSlot(PatientId patientId, Slot slot) {
        return patientReservationService.reserve(reservation(reservationDetails(patientId, slot)));
    }

    public List<PatientSlot> findReservationsFor(String day) {
        return reservationRepository.findReservations(DateInterval.parse(day + " 00:00-23:59")).stream().map(PatientReservation::details).collect(toList());
    }

    public void stop() {
        context.close();
    }
}
