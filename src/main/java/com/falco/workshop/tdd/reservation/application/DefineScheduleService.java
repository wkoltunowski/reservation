package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.DailyDoctorSchedule;
import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.FreeSlotRepository;
import com.falco.workshop.tdd.reservation.domain.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DefineScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private FreeSlotRepository freeSlotRepository;

    public void defineSchedule(DailyDoctorSchedule schedule) {
        this.scheduleRepository.save(schedule);
        generateSlots(schedule);
    }

    private void generateSlots(DailyDoctorSchedule schedule) {
        LocalDateTime start = LocalDate.of(2018, 1, 1).atTime(0, 0);
        LocalDateTime end = LocalDate.of(2019, 1, 1).atTime(0, 0);
        this.freeSlotRepository.saveAll(schedule.generateSlots(DateInterval.parse(start, end)));
    }
}
