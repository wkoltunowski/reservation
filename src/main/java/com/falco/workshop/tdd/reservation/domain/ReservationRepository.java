package com.falco.workshop.tdd.reservation.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReservationRepository {
    PatientReservation save(PatientReservation patientReservation);

    List<PatientReservation> findReservations(DateInterval interval);

    List<PatientReservation> findByScheduleId(ScheduleId scheduleId);

    Page<PatientReservation> findAll(Pageable pageable);

    PatientReservation findById(ReservationId id);
}
