package com.falco.workshop.tdd.reservation.domain;

import java.util.List;

public interface ReservationRepository {
    void save(PatientReservation patientReservation);

    List<PatientReservation> findReservations(DateInterval interval);

    List<PatientReservation> findByScheduleId(ScheduleId scheduleId);
}
