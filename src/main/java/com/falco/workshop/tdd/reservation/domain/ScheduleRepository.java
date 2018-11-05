package com.falco.workshop.tdd.reservation.domain;

public interface ScheduleRepository {
    DailyDoctorSchedule save(DailyDoctorSchedule schedule);

    DailyDoctorSchedule findById(ScheduleId id);
}
