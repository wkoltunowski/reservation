package com.falco.workshop.tdd.reservation.infrastructure;

import com.falco.workshop.tdd.reservation.domain.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.falco.workshop.tdd.reservation.domain.PatientReservation.reservation;
import static com.falco.workshop.tdd.reservation.domain.PatientSlot.patientSlot;
import static com.falco.workshop.tdd.reservation.domain.ReservationId.newId;
import static com.falco.workshop.tdd.reservation.domain.Slot.slot;

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
