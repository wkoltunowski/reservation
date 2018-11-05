package com.falco.workshop.tdd.reservation;

import com.falco.workshop.tdd.reservation.domain.reservation.PatientId;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientSlot;
import com.falco.workshop.tdd.reservation.domain.reservation.ReservationId;
import com.falco.workshop.tdd.reservation.domain.schedule.Schedule;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.slots.VisitSlot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.falco.workshop.tdd.reservation.domain.TimeInterval.fromTo;
import static com.falco.workshop.tdd.reservation.domain.reservation.PatientId.patientId;
import static com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation.reservation;
import static com.falco.workshop.tdd.reservation.domain.reservation.PatientSlot.patientSlot;
import static com.falco.workshop.tdd.reservation.domain.reservation.ReservationStatus.CANCELLED;
import static com.falco.workshop.tdd.reservation.domain.reservation.ReservationStatus.RESERVED;
import static com.falco.workshop.tdd.reservation.domain.schedule.Schedule.schedule;
import static com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId.scheduleId;
import static com.falco.workshop.tdd.reservation.domain.slots.VisitSlot.visitSlot;
import static java.time.Duration.ofMinutes;
import static org.assertj.core.api.Assertions.assertThat;

public class ReservationAcceptanceTest {
    private static final ScheduleId DOC_SMITH = scheduleId(1);
    private static final PatientId MALINOWSKI = patientId("Malinowski, Jan");
    private static final PatientId KOWALSKI = patientId("Kowalski, Jan");
    private static final PatientId PIOTROWSKI = patientId("Piotrowski, Jan");
    private ReservationApplication application;

    @Before
    public void setUp() {
//        application = ReservationApplication.startSpring();
        application = ReservationApplication.startInMemory();
    }

    @After
    public void tearDown() {
        application.stop();
    }

    @Test
    public void shouldFindSlots() {
        given(schedule(DOC_SMITH, fromTo("08:00-09:00"), ofMinutes(15)));
        assertThat(findFreeSlots("2018-09-02 08:00")).containsExactly(
                visitSlot(DOC_SMITH, "2018-09-02 08:00-08:15"),
                visitSlot(DOC_SMITH, "2018-09-02 08:15-08:30"),
                visitSlot(DOC_SMITH, "2018-09-02 08:30-08:45"),
                visitSlot(DOC_SMITH, "2018-09-02 08:45-09:00"));
    }

    @Test
    public void shouldReserveSlot() {
        given(schedule(DOC_SMITH, fromTo("08:00-09:15"), ofMinutes(15)));
        reserveSlot(KOWALSKI, visitSlot(DOC_SMITH, "2018-09-02 09:00-09:15"));
        assertThat(findFreeSlots("2018-09-02 09:00")).isEmpty();
    }

    @Test
    public void shouldFindReservations() {
        given(schedule(DOC_SMITH, fromTo("08:00-09:15"), ofMinutes(15)));
        reserveSlot(KOWALSKI, visitSlot(DOC_SMITH, "2018-09-02 08:00-08:15"));
        reserveSlot(PIOTROWSKI, visitSlot(DOC_SMITH, "2018-09-02 08:15-08:30"));
        reserveSlot(MALINOWSKI, visitSlot(DOC_SMITH, "2018-09-02 08:30-08:45"));
        assertThat(findPatientSlots("2018-09-02")).containsOnly(
                patientSlot(KOWALSKI, visitSlot(DOC_SMITH, "2018-09-02 08:00-08:15")),
                patientSlot(PIOTROWSKI, visitSlot(DOC_SMITH, "2018-09-02 08:15-08:30")),
                patientSlot(MALINOWSKI, visitSlot(DOC_SMITH, "2018-09-02 08:30-08:45"))
        );
    }

    @Test
    public void shouldRemoveFreeSlotsOnScheduleDelete() {
        given(schedule(DOC_SMITH, fromTo("08:00-09:15"), ofMinutes(15)));
        reserveSlot(KOWALSKI, visitSlot(DOC_SMITH, "2018-09-02 08:00-08:15"));
        reserveSlot(PIOTROWSKI, visitSlot(DOC_SMITH, "2018-09-02 08:15-08:30"));
        reserveSlot(MALINOWSKI, visitSlot(DOC_SMITH, "2018-09-02 08:30-08:45"));
        application.deleteSchedule(DOC_SMITH);

        assertThat(findFreeSlots("2018-09-02 08:00")).isEmpty();
    }

    @Test
    public void shouldCancelReservationsOnScheduleDelete() {
        given(schedule(DOC_SMITH, fromTo("08:00-09:15"), ofMinutes(15)));
        ReservationId kowalski_0800 = reserveSlot(KOWALSKI, visitSlot(DOC_SMITH, "2018-09-02 08:00-08:15"));
        ReservationId piotrowski0815 = reserveSlot(PIOTROWSKI, visitSlot(DOC_SMITH, "2018-09-02 08:15-08:30"));
        ReservationId malinowski0830 = reserveSlot(MALINOWSKI, visitSlot(DOC_SMITH, "2018-09-02 08:30-08:45"));
        application.deleteSchedule(DOC_SMITH);

        assertThat(findPatientReservations("2018-09-02")).containsOnly(
                reservation(kowalski_0800, patientSlot(KOWALSKI, visitSlot(DOC_SMITH, "2018-09-02 08:00-08:15")), CANCELLED),
                reservation(piotrowski0815, patientSlot(PIOTROWSKI, visitSlot(DOC_SMITH, "2018-09-02 08:15-08:30")), CANCELLED),
                reservation(malinowski0830, patientSlot(MALINOWSKI, visitSlot(DOC_SMITH, "2018-09-02 08:30-08:45")), CANCELLED)
        );
    }

    @Test
    public void shouldCancelReservationsOnScheduleUpdate() {
        given(schedule(DOC_SMITH, fromTo("08:00-09:15"), ofMinutes(15)));
        ReservationId r1 = reserveSlot(KOWALSKI, visitSlot(DOC_SMITH, "2018-09-02 08:00-08:15"));
        ReservationId r2 = reserveSlot(PIOTROWSKI, visitSlot(DOC_SMITH, "2018-09-02 08:15-08:30"));
        ReservationId r3 = reserveSlot(MALINOWSKI, visitSlot(DOC_SMITH, "2018-09-02 08:30-08:45"));
        application.updateSchedule(schedule(DOC_SMITH, fromTo("08:00-08:15"), ofMinutes(15)));

        assertThat(findPatientReservations("2018-09-02")).containsOnly(
                reservation(r1, patientSlot(KOWALSKI, visitSlot(DOC_SMITH, "2018-09-02 08:00-08:15")), RESERVED),
                reservation(r2, patientSlot(PIOTROWSKI, visitSlot(DOC_SMITH, "2018-09-02 08:15-08:30")), CANCELLED),
                reservation(r3, patientSlot(MALINOWSKI, visitSlot(DOC_SMITH, "2018-09-02 08:30-08:45")), CANCELLED)
        );
    }

    @Test
    public void shouldRemoveFreeSlotsOnScheduleUpdate() {
        given(schedule(DOC_SMITH, fromTo("08:00-09:15"), ofMinutes(15)));
        application.updateSchedule(schedule(DOC_SMITH, fromTo("08:00-08:15"), ofMinutes(15)));

        assertThat(findFreeSlots("2018-09-02 08:00")).containsExactly(
                visitSlot(DOC_SMITH, "2018-09-02 08:00-08:15"));
    }

    private List<PatientReservation> findPatientReservations(String day) {
        return application.findPatientReservations(day);
    }

    private List<PatientSlot> findPatientSlots(String day) {
        return application.findPatientSlots(day);
    }

    private ReservationId reserveSlot(PatientId patientId, VisitSlot visitSlot) {
        return application.reserveSlot(patientId, visitSlot);
    }

    private List<VisitSlot> findFreeSlots(String startingFrom) {
        return application.findFreeSlots(LocalDateTime.parse(startingFrom.split(" ")[0] + "T" + startingFrom.split(" ")[1]));
    }

    private void given(Schedule schedule) {
        application.defineSchedule(schedule);
    }
}

