package com.falco.workshop.tdd.reservation.infrastructure.reservation;

import com.falco.workshop.tdd.reservation.application.PatientReservationService;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientSlot;
import com.falco.workshop.tdd.reservation.domain.reservation.ReservationRepository;
import com.falco.workshop.tdd.reservation.domain.reservation.ReservationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.falco.workshop.tdd.reservation.domain.DateInterval.fromTo;
import static com.falco.workshop.tdd.reservation.domain.reservation.PatientId.patientId;
import static com.falco.workshop.tdd.reservation.domain.reservation.PatientSlot.patientSlot;
import static com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId.scheduleId;
import static com.falco.workshop.tdd.reservation.domain.slots.VisitSlot.visitSlot;
import static java.util.stream.Collectors.toList;

@Controller
@RequestMapping("/reservations")
public class ReservationsController {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PatientReservationService patientReservationService;

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PatientReservationJS create(@RequestBody final PatientReservationJS resource) {
        return new PatientReservationJS(patientReservationService.reserve(toPatientSlot(resource)));
    }

    private PatientSlot toPatientSlot(@RequestBody PatientReservationJS resource) {
        return patientSlot(
                patientId(resource.getPatientId()),
                visitSlot(scheduleId(resource.getScheduleId()), fromTo(resource.getStart(), resource.getEnd())));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<PatientReservationJS> findById(@PathVariable("id") final Long scheduleId) {
        return reservationRepository.findByScheduleId(scheduleId(scheduleId)).stream().map(PatientReservationJS::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Page<PatientReservationJS> findAllPageable(Pageable pageable) {
        return reservationRepository.findAll(pageable).map(PatientReservationJS::new);
    }
}

class PatientReservationJS {
    private Long reservationId;
    private Long scheduleId;
    private String patientId;
    private LocalDateTime start;
    private LocalDateTime end;
    private ReservationStatus status;

    private PatientReservationJS() {
    }

    public PatientReservationJS(PatientReservation reservation) {
        this.reservationId = reservation.id().id();
        PatientSlot slot = reservation.details();
        this.scheduleId = slot.slot().id().id();
        this.patientId = slot.patient().id();
        this.start = slot.slot().interval().start();
        this.end = slot.slot().interval().end();
        this.status = reservation.status();
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
}
