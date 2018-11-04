package com.falco.workshop.tdd.reservation.infrastructure;

import com.falco.workshop.tdd.reservation.application.SlotReservationService;
import com.falco.workshop.tdd.reservation.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.falco.workshop.tdd.reservation.domain.PatientReservation.reservation;
import static com.falco.workshop.tdd.reservation.domain.PatientSlot.patientSlot;
import static com.falco.workshop.tdd.reservation.domain.ReservationId.newId;
import static com.falco.workshop.tdd.reservation.domain.Slot.slot;
import static com.google.common.collect.ImmutableList.copyOf;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Component
public class SpringReservationRepository implements ReservationRepository {
    @Autowired
    private CrudReservationRepository crudReservationRepository;

    @Override
    public void save(PatientReservation patientReservation) {
        crudReservationRepository.save(new PatientReservationJS(patientReservation));
    }

    @Override
    public List<PatientReservation> findReservations(DateInterval interval) {
        return reservations(crudReservationRepository.findByStartEnd(interval.start(), interval.end()))
                .stream()
                .filter(r -> interval.intersects(r.details().slot().interval()))
                .collect(toList());
    }

    @Override
    public List<PatientReservation> findByScheduleId(ScheduleId scheduleId) {
        return reservations(crudReservationRepository.findByScheduleId(scheduleId.id()));
    }

    private List<PatientReservation> reservations(Iterable<PatientReservationJS> js) {
        return copyOf(js).stream().map(PatientReservationJS::toPatientReservation).collect(toList());
    }
}

@RepositoryRestResource(collectionResourceRel = "reservations", path = "reservations")
interface CrudReservationRepository extends PagingAndSortingRepository<PatientReservationJS, Long> {
    @Query("SELECT p FROM PatientReservationJS p WHERE p.start <= :end and p.end >= :start")
    Iterable<PatientReservationJS> findByStartEnd(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p FROM PatientReservationJS p WHERE p.scheduleId = :scheduleId")
    Iterable<PatientReservationJS> findByScheduleId(@Param("scheduleId") Long scheduleId);
}

@Component
class CrudReservationRepositoryEventListener extends AbstractRepositoryEventListener<PatientReservationJS> {
    @Autowired
    private FreeSlotRepository freeSlotRepository;
    @Autowired
    private SlotReservationService slotReservationService;

    @Override
    protected void onAfterCreate(PatientReservationJS entity) {
        slotReservationService.reserveSlot(entity.toPatientReservation().details().slot());
    }

    @Override
    protected void onAfterSave(PatientReservationJS entity) {
    }

    @Override
    protected void onAfterDelete(PatientReservationJS entity) {
        freeSlotRepository.saveAll(asList(entity.toPatientReservation().details().slot()));
    }
}

@Entity
class PatientReservationJS {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reservationId;
    private Long scheduleId;
    private String patientId;
    private LocalDateTime start;
    private LocalDateTime end;
    private ReservationStatus status = ReservationStatus.RESERVED;

    private PatientReservationJS() {
    }

    PatientReservationJS(PatientReservation patientReservation) {
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
                newId(reservationId),
                patientSlot(new PatientId(patientId),
                        slot(new ScheduleId(scheduleId), DateInterval.parse(start, end)))
        );
    }
}
