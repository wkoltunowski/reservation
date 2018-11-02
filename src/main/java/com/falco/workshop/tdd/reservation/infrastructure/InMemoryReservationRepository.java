package com.falco.workshop.tdd.reservation.infrastructure;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.PatientReservation;
import com.falco.workshop.tdd.reservation.domain.ReservationRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//@Component
public class InMemoryReservationRepository implements ReservationRepository {
    private List<PatientReservation> patientReservations = new ArrayList<>();

    @Override
    public void save(PatientReservation patientReservation) {
        patientReservations.add(patientReservation);
    }

    @Override
    public List<PatientReservation> findReservations(DateInterval interval) {
        return patientReservations.stream()
                .filter(r -> interval.intersects(r.details().slot().interval()))
                .collect(Collectors.toList());
    }
}
