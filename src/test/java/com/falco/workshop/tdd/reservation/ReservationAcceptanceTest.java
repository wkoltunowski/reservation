package com.falco.workshop.tdd.reservation;

import com.falco.workshop.tdd.reservation.domain.*;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientId;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientSlot;
import com.falco.workshop.tdd.reservation.domain.schedule.DailyDoctorSchedule;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.slots.FreeSlot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.falco.workshop.tdd.reservation.domain.schedule.DailyDoctorSchedule.dailyDoctorSchedule;
import static com.falco.workshop.tdd.reservation.domain.reservation.PatientSlot.patientSlot;
import static com.falco.workshop.tdd.reservation.domain.slots.FreeSlot.slot;
import static java.time.Duration.ofMinutes;
import static org.assertj.core.api.Assertions.assertThat;

public class ReservationAcceptanceTest {
    private static final ScheduleId DOC_SMITH = ScheduleId.scheduleId(1);
    private static final PatientId MALINOWSKI = new PatientId("Malinowski, Jan");
    private static final PatientId KOWALSKI = new PatientId("Kowalski, Jan");
    private static final PatientId PIOTROWSKI = new PatientId("Piotrowski, Jan");
    private ReservationApplication application;

    @Before
    public void setUp() {
        application = ReservationApplication.start();
    }

    @After
    public void tearDown() {
        application.stop();
    }

    @Test
    public void shouldFindSlots() {
        given(dailyDoctorSchedule(DOC_SMITH, TimeInterval.fromTo("08:00-09:00"), ofMinutes(15)));
        assertThat(findFreeSlots("2018-09-02 08:00")).containsExactly(
                slot(DOC_SMITH, "2018-09-02 08:00-08:15"),
                slot(DOC_SMITH, "2018-09-02 08:15-08:30"),
                slot(DOC_SMITH, "2018-09-02 08:30-08:45"),
                slot(DOC_SMITH, "2018-09-02 08:45-09:00"));
    }

    @Test
    public void shouldReserveSlot() {
        given(dailyDoctorSchedule(DOC_SMITH, TimeInterval.fromTo("08:00-09:15"), ofMinutes(15)));
        reserveSlot(KOWALSKI, slot(DOC_SMITH, "2018-09-02 09:00-09:15"));
        assertThat(findFreeSlots("2018-09-02 09:00")).isEmpty();
    }

    @Test
    public void shouldFindReservations() {
        given(dailyDoctorSchedule(DOC_SMITH, TimeInterval.fromTo("08:00-09:15"), ofMinutes(15)));
        reserveSlot(KOWALSKI, slot(DOC_SMITH, "2018-09-02 08:00-08:15"));
        reserveSlot(PIOTROWSKI, slot(DOC_SMITH, "2018-09-02 08:15-08:30"));
        reserveSlot(MALINOWSKI, slot(DOC_SMITH, "2018-09-02 08:30-08:45"));
        assertThat(findReservations("2018-09-02")).containsOnly(
                patientSlot(KOWALSKI, slot(DOC_SMITH, "2018-09-02 08:00-08:15")),
                patientSlot(PIOTROWSKI, slot(DOC_SMITH, "2018-09-02 08:15-08:30")),
                patientSlot(MALINOWSKI, slot(DOC_SMITH, "2018-09-02 08:30-08:45"))
        );
    }

    private List<PatientSlot> findReservations(String day) {
        return application.findReservationsFor(day);
    }

    private void reserveSlot(PatientId patientId, FreeSlot freeSlot) {
        application.reserveSlot(patientId, freeSlot);
    }

    private List<FreeSlot> findFreeSlots(String startingFrom) {
        return application.findFreeSlots(LocalDateTime.parse(startingFrom.split(" ")[0] + "T" + startingFrom.split(" ")[1]));
    }

    private void given(DailyDoctorSchedule schedule) {
        this.application.defineSchedule(schedule);
    }
}

