package com.falco.workshop.tdd.reservation.domain;

public class Reservation {
    private final ReservationDetails details;
    private final ReservationId id;

    public Reservation(ReservationId id, ReservationDetails details) {
        this.id = id;
        this.details = details;
    }

    public static Reservation reservation(ReservationDetails reservationDetails) {
        return new Reservation(ReservationId.newId(), reservationDetails);
    }

    public ReservationId id() {
        return id;
    }

    public ReservationDetails details() {
        return details;
    }
}
