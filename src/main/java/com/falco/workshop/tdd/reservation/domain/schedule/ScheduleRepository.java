package com.falco.workshop.tdd.reservation.domain.schedule;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScheduleRepository {
    Schedule save(Schedule schedule);

    Schedule findById(ScheduleId id);

    Page<Schedule> findAll(Pageable pageable);
}
