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

import static com.falco.workshop.tdd.reservation.domain.schedule.Schedule.schedule;
import static com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId.scheduleId;

//@Component
public class InMemoryScheduleRepository implements ScheduleRepository {

    private List<Schedule> schedules = new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public Schedule save(Schedule schedule) {
        Schedule toSave;
        if (schedule.id() == null)
            toSave = schedule(scheduleId(nextId++), schedule.workingHours(), schedule.visitDuration());
        else
            toSave = schedule;
        schedules.removeIf(s->s.id().equals(toSave.id()));
        schedules.add(toSave);
        return toSave;
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
