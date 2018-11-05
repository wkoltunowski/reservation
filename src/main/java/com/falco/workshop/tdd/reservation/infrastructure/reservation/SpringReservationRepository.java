package com.falco.workshop.tdd.reservation.infrastructure.reservation;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation;
import com.falco.workshop.tdd.reservation.domain.reservation.ReservationId;
import com.falco.workshop.tdd.reservation.domain.reservation.ReservationRepository;
import com.falco.workshop.tdd.reservation.domain.reservation.ReservationStatus;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.falco.workshop.tdd.reservation.domain.DateInterval.fromTo;
import static com.falco.workshop.tdd.reservation.domain.reservation.PatientId.patientId;
import static com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation.reservation;
import static com.falco.workshop.tdd.reservation.domain.reservation.PatientSlot.patientSlot;
import static com.falco.workshop.tdd.reservation.domain.reservation.ReservationId.reservationId;
import static com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId.scheduleId;
import static com.falco.workshop.tdd.reservation.domain.slots.VisitSlot.visitSlot;
import static com.google.common.collect.ImmutableList.copyOf;
import static java.util.stream.Collectors.toList;

@Component
public class SpringReservationRepository implements ReservationRepository {
    @Autowired
    private CrudReservationRepository crud;

    @Override
    public PatientReservation save(PatientReservation patientReservation) {
        return crud.save(new ReservationEntity(patientReservation)).toPatientReservation();
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
        return crud.findAll(pageable).map(ReservationEntity::toPatientReservation);
    }

    @Override
    public PatientReservation findById(ReservationId id) {
        return crud.findById(id.id()).get().toPatientReservation();
    }

    private List<PatientReservation> reservations(Iterable<ReservationEntity> js) {
        return copyOf(js).stream().map(ReservationEntity::toPatientReservation).collect(toList());
    }
}

interface CrudReservationRepository extends PagingAndSortingRepository<ReservationEntity, Long> {
    @Query("SELECT p FROM ReservationEntity p WHERE p.start <= :end and p.end >= :start")
    Iterable<ReservationEntity> findByStartEnd(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p FROM ReservationEntity p WHERE p.scheduleId = :scheduleId")
    Iterable<ReservationEntity> findByScheduleId(@Param("scheduleId") Long scheduleId);
}

@Entity
class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reservationId;
    private Long scheduleId;
    private String patientId;
    private LocalDateTime start;
    private LocalDateTime end;
    private ReservationStatus status;

    private ReservationEntity() {
    }

    ReservationEntity(PatientReservation patientReservation) {
        this.reservationId = Optional.ofNullable(patientReservation.id()).map(ReservationId::id).orElse(null);
        this.scheduleId = patientReservation.details().slot().id().id();
        this.patientId = patientReservation.details().patient().id();
        this.start = patientReservation.details().slot().interval().start();
        this.end = patientReservation.details().slot().interval().end();
        this.status = patientReservation.status();
    }

    public Long getReservationId() {
        return reservationId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public String getPatientId() {
        return patientId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public PatientReservation toPatientReservation() {
        return reservation(
                reservationId(reservationId),
                patientSlot(patientId(patientId), visitSlot(scheduleId(scheduleId), fromTo(start, end))),
                status
        );
    }
}
