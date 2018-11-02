package com.falco.workshop.tdd.reservation.infrastructure;

import com.falco.workshop.tdd.reservation.domain.DailyDoctorSchedule;
import com.falco.workshop.tdd.reservation.domain.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public class SpringScheduleRepository implements ScheduleRepository {
    @Autowired
    private CrudScheduleRepository crudReservationRepository;

    @Override
    public void save(DailyDoctorSchedule schedule) {
        crudReservationRepository.save(schedule);
    }

    @Override
    public DailyDoctorSchedule findById(ScheduleId id) {
        return crudReservationRepository.findOne(id);
    }
}

@Component
interface CrudScheduleRepository extends CrudRepository<DailyDoctorSchedule, ScheduleId> {
}
