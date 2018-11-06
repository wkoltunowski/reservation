package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.TimeInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.Schedule;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class DefineScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ScheduleEvents scheduleEvents;

    @Autowired
    public DefineScheduleService(ScheduleRepository scheduleRepository, ScheduleEvents scheduleEvents) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleEvents = scheduleEvents;
    }

    public Schedule defineSchedule(Schedule schedule) {
        Schedule dailySchedule = scheduleRepository.save(schedule);
        scheduleEvents.scheduleDefined(dailySchedule.id());
        return dailySchedule;
    }

    public void updateSchedule(ScheduleId id, TimeInterval workingHours, Duration visitDuration) {
        Schedule schedule = scheduleRepository.findById(id);
        schedule.updateWorkingHours(workingHours);
        schedule.updateVisitDuration(visitDuration);
        scheduleRepository.save(schedule);
        scheduleEvents.scheduleUpdated(id);
    }

    public void deleteSchedule(ScheduleId id) {
        Schedule schedule = scheduleRepository.findById(id);
        schedule.cancel();
        scheduleRepository.save(schedule);

        scheduleEvents.scheduleCancelled(id);
    }
}
