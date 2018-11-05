package com.falco.workshop.tdd.reservation.infrastructure;

import com.falco.workshop.tdd.reservation.domain.DailyDoctorSchedule;
import com.falco.workshop.tdd.reservation.domain.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;

@Component
public class SpringScheduleRepository implements ScheduleRepository {
    @Autowired
    private CrudScheduleRepository crudReservationRepository;

    @Override
    public DailyDoctorSchedule save(DailyDoctorSchedule schedule) {
        return crudReservationRepository.save(toSchedule(schedule)).toSchedule();
    }

    private Schedule toSchedule(DailyDoctorSchedule schedule) {
        return new Schedule(schedule);
    }

    @Override
    public DailyDoctorSchedule findById(ScheduleId id) {
        return crudReservationRepository.findById(id.id()).get().toSchedule();
    }
}

@Component
@RepositoryRestResource(collectionResourceRel = "schedules", path = "schedules")
interface CrudScheduleRepository extends PagingAndSortingRepository<Schedule, Long> {
}



