package com.falco.workshop.tdd.reservation.infrastructure.schedule;

import com.falco.workshop.tdd.reservation.domain.schedule.Schedule;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import static com.falco.workshop.tdd.reservation.domain.TimeInterval.fromTo;
import static com.falco.workshop.tdd.reservation.domain.schedule.Schedule.schedule;
import static java.time.Duration.parse;
import static java.util.Optional.ofNullable;

@Component
public class SpringScheduleRepository implements ScheduleRepository {
    private final CrudScheduleRepository crud;

    @Autowired
    public SpringScheduleRepository(CrudScheduleRepository crud) {
        this.crud = crud;
    }

    @Override
    public Schedule save(Schedule schedule) {
        return crud.save(toSchedule(schedule)).toSchedule();
    }

    private ScheduleEntity toSchedule(Schedule schedule) {
        return new ScheduleEntity(schedule);
    }

    @Override
    public Schedule findById(ScheduleId id) {
        return crud.findById(id.id()).get().toSchedule();
    }

    @Override
    public Page<Schedule> findAll(Pageable pageable) {
        return crud.findAll(pageable).map(ScheduleEntity::toSchedule);
    }
}

interface CrudScheduleRepository extends PagingAndSortingRepository<ScheduleEntity, Long> {
}

@Entity
class ScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String visitDuration;
    private String workingHours;

    ScheduleEntity() {
    }

    public ScheduleEntity(Schedule schedule) {
        this.id = ofNullable(schedule.id()).map(ScheduleId::id).orElse(null);
        this.visitDuration = schedule.visitDuration().toString();
        this.workingHours = schedule.workingHours().toString();
    }

    public Long getId() {
        return id;
    }

    public String getVisitDuration() {
        return visitDuration;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public Schedule toSchedule() {
        return schedule(ofNullable(id).map(ScheduleId::scheduleId).orElse(null), fromTo(workingHours), parse(visitDuration));
    }
}



