package com.falco.workshop.tdd.reservation;

import com.falco.workshop.tdd.reservation.domain.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.falco.workshop.tdd.reservation.domain.DailyDoctorSchedule.dailyDoctorSchedule;
import static com.falco.workshop.tdd.reservation.domain.ReservationDetails.reservationDetails;
import static com.falco.workshop.tdd.reservation.domain.Slot.slot;
import static java.time.Duration.ofMinutes;
import static org.assertj.core.api.Assertions.assertThat;

public class ReservationAcceptanceTest {
    private static final ScheduleId DOC_SMITH = new ScheduleId("dr. Smith, John");
    private static final PatientId MALINOWSKI = new PatientId("Piotrowski, Jan");
    private static final PatientId KOWALSKI = new PatientId("Kowalski, Jan");
    private static final PatientId PIOTROWSKI = new PatientId("Malinowski, Jan");
    private ReservationApplication application;

    @Before
    public void setUp() {
        application = ReservationApplication.start();
    }

    @Test
    public void shouldFindSlots() {
        given(dailyDoctorSchedule(DOC_SMITH, TimeInterval.parse("08:00-09:00"), ofMinutes(15)));
        assertThat(findFreeSlots("2018-09-02 08:00")).containsExactly(
                slot(DOC_SMITH, "2018-09-02 08:00-08:15"),
                slot(DOC_SMITH, "2018-09-02 08:15-08:30"),
                slot(DOC_SMITH, "2018-09-02 08:30-08:45"),
                slot(DOC_SMITH, "2018-09-02 08:45-09:00"));
    }

    @Test
    public void shouldReserveSlot() {
        given(dailyDoctorSchedule(DOC_SMITH, TimeInterval.parse("08:00-09:15"), ofMinutes(15)));
        reserveSlot(slot(DOC_SMITH, "2018-09-02 09:00-09:15"), KOWALSKI);
        assertThat(findFreeSlots("2018-09-02 09:00")).isEmpty();
    }

    @Test
    public void shouldFindReservations() {
        given(dailyDoctorSchedule(DOC_SMITH, TimeInterval.parse("08:00-09:15"), ofMinutes(15)));
        reserveSlot(slot(DOC_SMITH, "2018-09-02 08:00-08:15"), KOWALSKI);
        reserveSlot(slot(DOC_SMITH, "2018-09-02 08:15-08:30"), PIOTROWSKI);
        reserveSlot(slot(DOC_SMITH, "2018-09-02 08:30-08:45"), MALINOWSKI);
        assertThat(findReservations("2018-09-02")).containsOnly(
                reservationDetails(KOWALSKI, slot(DOC_SMITH, "2018-09-02 08:00-08:15")),
                reservationDetails(PIOTROWSKI, slot(DOC_SMITH, "2018-09-02 08:15-08:30")),
                reservationDetails(MALINOWSKI, slot(DOC_SMITH, "2018-09-02 08:30-08:45"))
        );
    }

    private List<ReservationDetails> findReservations(String day) {
        return application.findReservationsFor(day);
    }

    private void reserveSlot(Slot slot, PatientId patientId) {
        application.reserveSlot(slot, patientId);
    }

    private List<Slot> findFreeSlots(String startingFrom) {
        return application.findFreeSlots(LocalDateTime.parse(startingFrom.split(" ")[0] + "T" + startingFrom.split(" ")[1]));
    }

    private void given(DailyDoctorSchedule schedule) {
        this.application.defineSchedule(schedule);
    }
}

