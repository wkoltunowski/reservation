package com.falco.workshop.tdd.reservation.domain.reservation;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReservationRepository {
    PatientReservation save(PatientReservation patientReservation);

    List<PatientReservation> findReservations(DateInterval interval);

    List<PatientReservation> findByScheduleId(ScheduleId scheduleId);

    Page<PatientReservation> findAll(Pageable pageable);

    PatientReservation findById(ReservationId id);

    List<PatientReservation> findByIds(List<ReservationId> reservationIds);
}
