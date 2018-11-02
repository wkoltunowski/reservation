package com.falco.workshop.tdd.reservation.domain;

public class PatientReservation {
    private final PatientSlot details;
    private final ReservationId id;

    private PatientReservation(ReservationId id, PatientSlot details) {
        this.id = id;
        this.details = details;
    }

    public static PatientReservation reservation(PatientSlot patientSlot) {
        return new PatientReservation(ReservationId.newId(), patientSlot);
    }

    public ReservationId id() {
        return id;
    }

    public PatientSlot details() {
        return details;
    }
}
