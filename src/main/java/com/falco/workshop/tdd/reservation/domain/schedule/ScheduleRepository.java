package com.falco.workshop.tdd.reservation.domain.schedule;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScheduleRepository {
    DailyDoctorSchedule save(DailyDoctorSchedule schedule);

    DailyDoctorSchedule findById(ScheduleId id);

    Page<DailyDoctorSchedule> findAll(Pageable pageable);
}
