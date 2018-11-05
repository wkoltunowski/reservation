package com.falco.workshop.tdd.reservation.domain.schedule;

import com.falco.workshop.tdd.reservation.domain.schedule.DailyDoctorSchedule;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScheduleRepository {
    DailyDoctorSchedule save(DailyDoctorSchedule schedule);

    DailyDoctorSchedule findById(ScheduleId id);

    Page<DailyDoctorSchedule> findAll(Pageable pageable);
}
