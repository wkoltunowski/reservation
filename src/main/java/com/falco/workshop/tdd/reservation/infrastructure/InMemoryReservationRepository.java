package com.falco.workshop.tdd.reservation.infrastructure;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.Reservation;
import com.falco.workshop.tdd.reservation.domain.ReservationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryReservationRepository implements ReservationRepository {
    private List<Reservation> reservations = new ArrayList<>();

    @Override
    public void save(Reservation reservation) {
        reservations.add(reservation);
    }

    @Override
    public List<Reservation> findReservations(DateInterval interval) {
        return reservations.stream()
                .filter(r -> interval.intersects(r.details().slot().interval()))
                .collect(Collectors.toList());
    }
}
