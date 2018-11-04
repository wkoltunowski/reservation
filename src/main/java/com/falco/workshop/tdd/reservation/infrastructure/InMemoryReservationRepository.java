package com.falco.workshop.tdd.reservation.infrastructure;

import com.falco.workshop.tdd.reservation.domain.*;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

//@Component
public class InMemoryReservationRepository implements ReservationRepository {
    private List<PatientReservation> patientReservations = new ArrayList<>();

    @Override
    public PatientReservation save(PatientReservation patientReservation) {
        patientReservations.add(patientReservation);
        return patientReservation;
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
}
