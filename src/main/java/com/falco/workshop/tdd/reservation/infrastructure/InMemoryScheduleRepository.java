package com.falco.workshop.tdd.reservation.infrastructure;

import com.falco.workshop.tdd.reservation.domain.DailyDoctorSchedule;
import com.falco.workshop.tdd.reservation.domain.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.ScheduleRepository;

import java.util.ArrayList;
import java.util.List;

//@Component
public class InMemoryScheduleRepository implements ScheduleRepository {

    private List<DailyDoctorSchedule> schedules = new ArrayList<>();

    @Override
    public DailyDoctorSchedule save(DailyDoctorSchedule schedule) {
        schedules.add(schedule);
        return schedule;
    }

    @Override
    public DailyDoctorSchedule findById(ScheduleId id) {
        return schedules.stream().filter(s -> s.id().equals(id)).findFirst().get();
    }
}
