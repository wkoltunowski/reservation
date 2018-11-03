package com.falco.workshop.tdd.reservation.domain;

public class PatientReservation {
    private final ReservationId reservationId;
    private PatientSlot details;
    private ReservationStatus status;

    private PatientReservation(ReservationId reservationId, PatientSlot details) {
        this.reservationId = reservationId;
        this.details = details;
        this.status = ReservationStatus.RESERVED;
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
        return new PatientReservation(null, patientSlot);
    }

    public static PatientReservation reservation(ReservationId reservationId, PatientSlot patientSlot) {
        return new PatientReservation(reservationId, patientSlot);
    }
}
