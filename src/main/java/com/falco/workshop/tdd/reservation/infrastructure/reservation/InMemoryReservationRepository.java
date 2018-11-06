package com.falco.workshop.tdd.reservation.infrastructure.reservation;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation;
import com.falco.workshop.tdd.reservation.domain.reservation.ReservationId;
import com.falco.workshop.tdd.reservation.domain.reservation.ReservationRepository;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation.reservation;
import static com.falco.workshop.tdd.reservation.domain.reservation.ReservationId.reservationId;
import static java.util.stream.Collectors.toList;

public class InMemoryReservationRepository implements ReservationRepository {
    private List<PatientReservation> patientReservations = new ArrayList<>();
    private Long next = 1L;

    @Override
    public PatientReservation save(PatientReservation patientReservation) {
        PatientReservation reservation;
        if (patientReservation.id() == null)
            reservation = reservation(reservationId(next++), patientReservation.details(), patientReservation.status());
        else
            reservation = patientReservation;
        patientReservations.removeIf(r -> r.id().equals(reservation.id()));
        patientReservations.add(reservation);
        return reservation;
    }

    @Override
    public List<PatientReservation> findReservations(DateInterval interval) {
        return patientReservations.stream()
                .filter(r -> interval.intersects(r.details().slot().interval()))
                .collect(toList());
    }

    @Override
    public List<PatientReservation> findByScheduleId(ScheduleId scheduleId) {
        return patientReservations.stream().filter(r -> r.details().slot().id().equals(scheduleId)).collect(toList());
    }

    @Override
    public Page<PatientReservation> findAll(Pageable pageable) {
        Integer from = ObjectUtils.min(patientReservations.size() - 1, pageable.getPageNumber() * pageable.getPageSize());
        Integer to = ObjectUtils.min(patientReservations.size() - 1, from + pageable.getPageSize());
        return new PageImpl<>(patientReservations.subList(from, to + 1), pageable, patientReservations.size());
    }

    @Override
    public PatientReservation findById(ReservationId id) {
        return patientReservations.stream().filter(r -> r.id().equals(id)).findFirst().get();
    }

    @Override
    public List<PatientReservation> findByIds(List<ReservationId> reservationIds) {
        return patientReservations.stream().filter(r -> reservationIds.contains(r.id())).collect(toList());
    }
}
