package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.application.slots.ReserveFreeSlotService;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientId;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientSlot;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlot;
import com.falco.workshop.tdd.reservation.infrastructure.reservation.InMemoryReservationRepository;
import com.falco.workshop.tdd.reservation.infrastructure.slots.InMemoryFreeScheduleSlotRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static com.falco.workshop.tdd.reservation.domain.DateInterval.fromTo;
import static com.falco.workshop.tdd.reservation.domain.reservation.PatientId.patientId;
import static com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation.reservation;
import static com.falco.workshop.tdd.reservation.domain.reservation.PatientSlot.patientSlot;
import static com.falco.workshop.tdd.reservation.domain.reservation.ReservationStatus.CANCELLED;
import static com.falco.workshop.tdd.reservation.domain.reservation.ReservationStatus.RESERVED;
import static com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId.scheduleId;
import static com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlot.freeScheduleSlot;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class PatientReservationServiceTest {
    private static final ScheduleId SCHEDULE_1 = scheduleId(1);
    private static final PatientId KOWALSKI = patientId("pat_1");
    private PatientReservationService reservationService;
    private InMemoryReservationRepository reservationRepository;
    private InMemoryFreeScheduleSlotRepository slotRepository;

    @Before
    public void setUp() {
        reservationRepository = new InMemoryReservationRepository();
        slotRepository = new InMemoryFreeScheduleSlotRepository();
        reservationService = new PatientReservationService(
                reservationRepository,
                new ReserveFreeSlotService(slotRepository));
    }

    @Test
    public void shouldReserveAvailableSlot() {
        given(freeScheduleSlot(SCHEDULE_1, fromTo("2018-09-10 08:00-16:00")));
        PatientReservation reservation = reserve(patientSlot(KOWALSKI, SCHEDULE_1, fromTo("2018-09-10 08:00-08:15")));
        assertThat(reservation.details()).isEqualTo(patientSlot(KOWALSKI, SCHEDULE_1, fromTo("2018-09-10 08:00-08:15")));
        assertThat(reservation.status()).isEqualTo(RESERVED);
        assertThat(reservation).isEqualTo(reservationRepository.findById(reservation.id()));
    }

    @Test
    public void shouldCancelReservationsWhenNoSlotsAvailable() {
        given(freeScheduleSlot(SCHEDULE_1, fromTo("2018-09-10 08:00-16:00")));
        PatientReservation r1 = reservationService.reserve(patientSlot(KOWALSKI, SCHEDULE_1, fromTo("2018-09-10 08:00-08:15")));
        slotRepository.deleteByScheduleId(SCHEDULE_1);

        reservationService.reReservePatientReservations(SCHEDULE_1);

        assertThat(reservationRepository.findAll(PageRequest.of(0, 100))).containsOnly(
                reservation(r1.id(), patientSlot(KOWALSKI, SCHEDULE_1, fromTo("2018-09-10 08:00-08:15")), CANCELLED)
        );
    }

    @Test
    public void shouldReReserveLeaveReservation() {
        given(freeScheduleSlot(SCHEDULE_1, fromTo("2018-09-10 08:00-16:00")));
        PatientReservation r1 = reserve(patientSlot(KOWALSKI, SCHEDULE_1, fromTo("2018-09-10 08:00-08:15")));
        slotRepository.deleteByScheduleId(SCHEDULE_1);
        given(freeScheduleSlot(SCHEDULE_1, fromTo("2018-09-10 08:00-10:00")));

        reservationService.reReservePatientReservations(SCHEDULE_1);

        assertThat(reservationRepository.findAll(PageRequest.of(0, 100))).containsOnly(
                reservation(r1.id(), patientSlot(KOWALSKI, SCHEDULE_1, fromTo("2018-09-10 08:00-08:15")), RESERVED)
        );
    }

    @Test
    public void shouldReReserveReserveSlot() {
        given(freeScheduleSlot(SCHEDULE_1, fromTo("2018-09-10 08:00-16:00")));
        reserve(patientSlot(KOWALSKI, SCHEDULE_1, fromTo("2018-09-10 08:00-08:15")));
        slotRepository.deleteByScheduleId(SCHEDULE_1);
        given(freeScheduleSlot(SCHEDULE_1, fromTo("2018-09-10 08:00-10:00")));

        reservationService.reReservePatientReservations(SCHEDULE_1);

        assertThat(slotRepository.findByScheduleIdEnclosing(SCHEDULE_1, fromTo("2018-09-10 08:00-08:15"))).isEqualTo(Optional.empty());
    }

    private PatientReservation reserve(PatientSlot patientSlot) {
        return reservationService.reserve(patientSlot);
    }

    private void given(FreeScheduleSlot... slots) {
        slotRepository.saveAll(asList(slots));
    }
}