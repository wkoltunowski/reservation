package com.falco.workshop.tdd.reservation.domain;

public interface ScheduleRepository {
    void save(DailyDoctorSchedule schedule);

    DailyDoctorSchedule findById(ScheduleId id);
}
