package com.falco.workshop.tdd.reservation.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PatientReservation {
    private PatientSlot details;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private ReservationId reservationId;

    PatientReservation() {
    }

    private PatientReservation(ReservationId reservationId, PatientSlot details) {
        this.reservationId = reservationId;
        this.details = details;
    }

    public static PatientReservation reservation(PatientSlot patientSlot) {
        return new PatientReservation(ReservationId.newId(), patientSlot);
    }

    public ReservationId id() {
        return reservationId;
    }

    public PatientSlot details() {
        return details;
    }
}
