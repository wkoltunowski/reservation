package com.falco.workshop.tdd.reservation.domain;

import java.util.List;

public interface ReservationRepository {
    void save(Reservation reservation);

    List<Reservation> findReservations(DateInterval interval);
}
