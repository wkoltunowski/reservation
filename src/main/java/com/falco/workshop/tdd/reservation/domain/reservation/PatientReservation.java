package com.falco.workshop.tdd.reservation.domain.reservation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PatientReservation {
    private final ReservationId reservationId;
    private final PatientSlot details;
    private ReservationStatus status;

    private PatientReservation(ReservationId reservationId, PatientSlot details, ReservationStatus status) {
        this.reservationId = reservationId;
        this.details = details;
        this.status = status;
    }

    public ReservationId id() {
        return reservationId;
    }

    public PatientSlot details() {
        return details;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return "PatientReservation[" + "reservationId:" + reservationId.id() + ", " + "details:" + details + ", status:" + status + "]";
    }

    public static PatientReservation reservation(PatientSlot patientSlot) {
        return reservation(null, patientSlot, ReservationStatus.RESERVED);
    }

    public static PatientReservation reservation(ReservationId reservationId, PatientSlot patientSlot, ReservationStatus status) {
        return new PatientReservation(reservationId, patientSlot, status);
    }

    public ReservationStatus status() {
        return status;
    }
}
