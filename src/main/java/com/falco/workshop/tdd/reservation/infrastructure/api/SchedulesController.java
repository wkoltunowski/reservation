package com.falco.workshop.tdd.reservation.infrastructure.api;

import com.falco.workshop.tdd.reservation.application.DefineScheduleService;
import com.falco.workshop.tdd.reservation.domain.DailyDoctorSchedule;
import com.falco.workshop.tdd.reservation.domain.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.ScheduleRepository;
import com.falco.workshop.tdd.reservation.infrastructure.PatientReservationJS;
import com.falco.workshop.tdd.reservation.infrastructure.Schedule;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

//@Controller
//@RequestMapping("/reservations")
public class SchedulesController {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private DefineScheduleService defineScheduleService;

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PatientReservationJS create(@RequestBody final Schedule resource) {
        Preconditions.checkNotNull(resource);
        DailyDoctorSchedule dailyDoctorSchedule = resource.toSchedule();
        defineScheduleService.defineSchedule(dailyDoctorSchedule);

//        return findById()new PatientReservationJS(patientReservationService.reserve(resource.toPatientReservation().details()));
        throw new UnsupportedOperationException();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<PatientReservationJS> findById(@PathVariable("id") final Long scheduleId) {
//        return scheduleRepository.findByScheduleId(new ScheduleId(scheduleId)).stream().map(PatientReservationJS::new).collect(toList());
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Page<PatientReservationJS> findAllPageable(Pageable pageable) {
//        return scheduleRepository.findAll(pageable).map(PatientReservationJS::new);
        throw new UnsupportedOperationException();
    }
}
