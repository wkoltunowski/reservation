package com.falco.workshop.tdd.reservation.infrastructure.schedule;

import com.falco.workshop.tdd.reservation.domain.schedule.Schedule;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

//@Component
public class InMemoryScheduleRepository implements ScheduleRepository {

    private List<Schedule> schedules = new ArrayList<>();

    @Override
    public Schedule save(Schedule schedule) {
        schedules.add(schedule);
        return schedule;
    }

    @Override
    public Schedule findById(ScheduleId id) {
        return schedules.stream().filter(s -> s.id().equals(id)).findFirst().get();
    }

    @Override
    public Page<Schedule> findAll(Pageable pageable) {
        Integer from = ObjectUtils.min(schedules.size() - 1, pageable.getPageNumber() * pageable.getPageSize());
        Integer to = ObjectUtils.min(schedules.size() - 1, from + pageable.getPageSize());
        return new PageImpl<>(schedules.subList(from, to + 1), pageable, schedules.size());
    }
}
