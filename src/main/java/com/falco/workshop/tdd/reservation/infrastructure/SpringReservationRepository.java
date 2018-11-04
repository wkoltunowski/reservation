package com.falco.workshop.tdd.reservation.infrastructure;

import com.falco.workshop.tdd.reservation.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;
import static java.util.stream.Collectors.toList;

@Component
public class SpringReservationRepository implements ReservationRepository {
    @Autowired
    private CrudReservationRepository crud;

    @Override
    public PatientReservation save(PatientReservation patientReservation) {
        return crud.save(new PatientReservationJS(patientReservation)).toPatientReservation();
    }

    @Override
    public List<PatientReservation> findReservations(DateInterval interval) {
        return reservations(crud.findByStartEnd(interval.start(), interval.end()))
                .stream()
                .filter(r -> interval.intersects(r.details().slot().interval()))
                .collect(toList());
    }

    @Override
    public List<PatientReservation> findByScheduleId(ScheduleId scheduleId) {
        return reservations(crud.findByScheduleId(scheduleId.id()));
    }

    @Override
    public Page<PatientReservation> findAll(Pageable pageable) {
        return crud.findAll(pageable).map(PatientReservationJS::toPatientReservation);
    }

    @Override
    public PatientReservation findById(ReservationId id) {
        return crud.findById(id.id()).get().toPatientReservation();
    }

    private List<PatientReservation> reservations(Iterable<PatientReservationJS> js) {
        return copyOf(js).stream().map(PatientReservationJS::toPatientReservation).collect(toList());
    }
}

interface CrudReservationRepository extends PagingAndSortingRepository<PatientReservationJS, Long> {
    @Query("SELECT p FROM PatientReservationJS p WHERE p.start <= :end and p.end >= :start")
    Iterable<PatientReservationJS> findByStartEnd(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p FROM PatientReservationJS p WHERE p.scheduleId = :scheduleId")
    Iterable<PatientReservationJS> findByScheduleId(@Param("scheduleId") Long scheduleId);
}
