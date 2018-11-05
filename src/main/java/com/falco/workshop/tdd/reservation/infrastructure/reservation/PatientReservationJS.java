package com.falco.workshop.tdd.reservation.infrastructure.reservation;

import com.falco.workshop.tdd.reservation.domain.*;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientId;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation;
import com.falco.workshop.tdd.reservation.domain.reservation.ReservationId;
import com.falco.workshop.tdd.reservation.domain.reservation.ReservationStatus;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation.reservation;
import static com.falco.workshop.tdd.reservation.domain.reservation.PatientSlot.patientSlot;
import static com.falco.workshop.tdd.reservation.domain.reservation.ReservationId.newId;
import static com.falco.workshop.tdd.reservation.domain.slots.Slot.slot;

@Entity
public class PatientReservationJS {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reservationId;
    private Long scheduleId;
    private String patientId;
    private LocalDateTime start;
    private LocalDateTime end;
    private ReservationStatus status = ReservationStatus.RESERVED;

    private PatientReservationJS() {
    }

    public PatientReservationJS(PatientReservation patientReservation) {
        this.reservationId = Optional.ofNullable(patientReservation.id()).map(ReservationId::id).orElse(null);
        this.scheduleId = patientReservation.details().slot().id().id();
        this.patientId = patientReservation.details().patient().id();
        this.start = patientReservation.details().slot().interval().start();
        this.end = patientReservation.details().slot().interval().end();
        this.status = patientReservation.status();
    }

    public Long getReservationId() {
        return reservationId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public String getPatientId() {
        return patientId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public PatientReservation toPatientReservation() {
        return reservation(
                newId(reservationId),
                patientSlot(new PatientId(patientId),
                        slot(new ScheduleId(scheduleId), DateInterval.parse(start, end)))
        );
    }
}
