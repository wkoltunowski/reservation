package com.falco.workshop.tdd.reservation.infrastructure.schedule;

import com.falco.workshop.tdd.reservation.application.DefineScheduleService;
import com.falco.workshop.tdd.reservation.domain.schedule.DailyDoctorSchedule;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleRepository;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/schedules")
public class SchedulesController {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private DefineScheduleService defineScheduleService;

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ScheduleJS create(@RequestBody final ScheduleJS resource) {
        Preconditions.checkNotNull(resource);
        DailyDoctorSchedule dailyDoctorSchedule = resource.toSchedule();
        DailyDoctorSchedule schedule = defineScheduleService.defineSchedule(dailyDoctorSchedule);
        return new ScheduleJS(schedule);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ScheduleJS findById(@PathVariable("id") final Long scheduleId) {
        return new ScheduleJS(scheduleRepository.findById(new ScheduleId(scheduleId)));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Page<ScheduleJS> findAllPageable(Pageable pageable) {
        return scheduleRepository.findAll(pageable).map(ScheduleJS::new);
    }
}