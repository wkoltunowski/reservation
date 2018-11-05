package com.falco.workshop.tdd.reservation.infrastructure.schedule;

import com.falco.workshop.tdd.reservation.domain.DailyDoctorSchedule;
import com.falco.workshop.tdd.reservation.domain.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

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

    private ScheduleJS toSchedule(DailyDoctorSchedule schedule) {
        return new ScheduleJS(schedule);
    }

    @Override
    public DailyDoctorSchedule findById(ScheduleId id) {
        return crud.findById(id.id()).get().toSchedule();
    }

    @Override
    public Page<DailyDoctorSchedule> findAll(Pageable pageable) {
        return crud.findAll(pageable).map(ScheduleJS::toSchedule);
    }
}

interface CrudScheduleRepository extends PagingAndSortingRepository<ScheduleJS, Long> {
}



