package com.falco.workshop.tdd.reservation.infrastructure;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.PatientReservation;
import com.falco.workshop.tdd.reservation.domain.ReservationId;
import com.falco.workshop.tdd.reservation.domain.ReservationRepository;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SpringReservationRepository implements ReservationRepository {
    @Autowired
    private CrudReservationRepository crudReservationRepository;

    @Override
    public void save(PatientReservation patientReservation) {
        crudReservationRepository.save(patientReservation);
    }

    @Override
    public List<PatientReservation> findReservations(DateInterval interval) {
        return ImmutableList.copyOf(crudReservationRepository.findByStartEnd(interval.start(), interval.end()))
                .stream()
                .filter(r -> interval.intersects(r.details().slot().interval()))
                .collect(Collectors.toList());
    }
}

@Component
interface CrudReservationRepository extends CrudRepository<PatientReservation, ReservationId> {
    @Query("SELECT p FROM PatientReservation p WHERE p.details.slot.interval.start <= :end and p.details.slot.interval.end >= :start")
    Iterable<PatientReservation> findByStartEnd(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
