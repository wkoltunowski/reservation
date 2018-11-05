package com.falco.workshop.tdd.reservation.infrastructure.schedule;

import com.falco.workshop.tdd.reservation.domain.TimeInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.DailyDoctorSchedule;
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
import java.time.Duration;
import java.util.Optional;

import static com.falco.workshop.tdd.reservation.domain.schedule.DailyDoctorSchedule.dailyDoctorSchedule;

@Component
public class SpringScheduleRepository implements ScheduleRepository {
    private final CrudScheduleRepository crud;

    @Autowired
    public SpringScheduleRepository(CrudScheduleRepository crud) {
        this.crud = crud;
    }

    @Override
    public DailyDoctorSchedule save(DailyDoctorSchedule schedule) {
        return crud.save(toSchedule(schedule)).toSchedule();
    }

    private ScheduleEntity toSchedule(DailyDoctorSchedule schedule) {
        return new ScheduleEntity(schedule);
    }

    @Override
    public DailyDoctorSchedule findById(ScheduleId id) {
        return crud.findById(id.id()).get().toSchedule();
    }

    @Override
    public Page<DailyDoctorSchedule> findAll(Pageable pageable) {
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

    public ScheduleEntity(DailyDoctorSchedule schedule) {
        this.id = Optional.ofNullable(schedule.id()).map(ScheduleId::id).orElse(null);
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

    public DailyDoctorSchedule toSchedule() {
        return dailyDoctorSchedule(Optional.ofNullable(id).map(ScheduleId::new).orElse(null), TimeInterval.fromTo(workingHours), Duration.parse(visitDuration));
    }
}



