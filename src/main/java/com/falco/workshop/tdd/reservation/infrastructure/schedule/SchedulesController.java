package com.falco.workshop.tdd.reservation.infrastructure.schedule;

import com.falco.workshop.tdd.reservation.application.DefineScheduleService;
import com.falco.workshop.tdd.reservation.domain.schedule.Schedule;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleRepository;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

import static com.falco.workshop.tdd.reservation.domain.TimeInterval.fromTo;
import static com.falco.workshop.tdd.reservation.domain.schedule.Schedule.schedule;
import static com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId.scheduleId;

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
        Schedule schedule = defineScheduleService.defineSchedule(
                schedule(fromTo(resource.getWorkingHours()), Duration.parse(resource.getVisitDuration())));
        return new ScheduleJS(schedule);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ScheduleJS findById(@PathVariable("id") final Long scheduleId) {
        return new ScheduleJS(scheduleRepository.findById(scheduleId(scheduleId)));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Page<ScheduleJS> findAllPageable(Pageable pageable) {
        return scheduleRepository.findAll(pageable).map(ScheduleJS::new);
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable("id") Long scheduleId) {
        defineScheduleService.deleteSchedule(scheduleId(scheduleId));
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void update(@RequestBody final ScheduleJS resource) {
        defineScheduleService.updateSchedule(
                Schedule.schedule(scheduleId(resource.getId()), fromTo(resource.getWorkingHours()), Duration.parse(resource.getVisitDuration()))
        );
    }
}

class ScheduleJS {
    private Long id;
    private String visitDuration;
    private String workingHours;

    ScheduleJS() {
    }

    public ScheduleJS(Schedule schedule) {
        this.id = schedule.id().id();
        this.visitDuration = schedule.visitDuration().toString();
        this.workingHours = schedule.workingHours().toString();
    }

    public Long getId() {
        return id;
    }

    public String getVisitDuration() {
        return visitDuration;
    }

    public String getWorkingHours() {
        return workingHours;
    }
}