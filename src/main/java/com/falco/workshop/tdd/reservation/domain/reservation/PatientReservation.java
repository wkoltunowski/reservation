package com.falco.workshop.tdd.reservation.domain.reservation;

public class PatientReservation {
    private final ReservationId reservationId;
    private PatientSlot details;
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
