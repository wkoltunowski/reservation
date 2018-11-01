package com.falco.workshop.tdd.reservation.domain;

import java.util.List;

public interface ScheduleRepository {
    void save(DailyDoctorSchedule schedule);

    List<DailyDoctorSchedule> findAll();

    DailyDoctorSchedule findById(ScheduleId id);

    void update(DailyDoctorSchedule schedule);
}
