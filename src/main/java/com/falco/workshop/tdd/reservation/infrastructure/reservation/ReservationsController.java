package com.falco.workshop.tdd.reservation.infrastructure.reservation;

import com.falco.workshop.tdd.reservation.application.PatientReservationService;
import com.falco.workshop.tdd.reservation.domain.reservation.ReservationRepository;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        Preconditions.checkNotNull(resource);
        return new PatientReservationJS(patientReservationService.reserve(resource.toPatientReservation().details()));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<PatientReservationJS> findById(@PathVariable("id") final Long scheduleId) {
        return reservationRepository.findByScheduleId(new ScheduleId(scheduleId)).stream().map(PatientReservationJS::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Page<PatientReservationJS> findAllPageable(Pageable pageable) {
        return reservationRepository.findAll(pageable).map(PatientReservationJS::new);
    }
}
