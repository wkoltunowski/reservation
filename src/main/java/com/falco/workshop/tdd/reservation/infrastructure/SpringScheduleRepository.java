package com.falco.workshop.tdd.reservation.infrastructure;

import com.falco.workshop.tdd.reservation.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.falco.workshop.tdd.reservation.domain.DailyDoctorSchedule.dailyDoctorSchedule;
import static java.util.stream.Collectors.toList;

@Component
public class SpringScheduleRepository implements ScheduleRepository {
    @Autowired
    private CrudScheduleRepository crudReservationRepository;

    @Override
    public void save(DailyDoctorSchedule schedule) {
        crudReservationRepository.save(toSchedule(schedule));
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

@Component
class ScheduleControllerEventListener extends AbstractRepositoryEventListener<Schedule> {
    @Autowired
    private FreeSlotRepository freeSlotRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    protected void onAfterCreate(Schedule entity) {
        freeSlotRepository.saveAll(entity.toSchedule().generateSlots(DateInterval.parse(LocalDateTime.now(), LocalDateTime.now().plusDays(90))));
    }

    @Override
    protected void onAfterSave(Schedule entity) {
        List<PatientReservation> reservations = reservationRepository.findByScheduleId(entity.toSchedule().id());

        for (Slot reservedSlot : reservations.stream().map(r -> r.details().slot()).collect(toList())) {
            freeSlotRepository.delete(reservedSlot);
        }

        List<Slot> slots = entity.toSchedule().generateSlots(DateInterval.parse(LocalDateTime.now(), LocalDateTime.now().plusDays(365)));
        for (PatientReservation reservation : reservations) {
            Optional<Slot> first = slots.stream().filter(s -> s.interval().encloses(reservation.details().slot().interval())).findFirst();
            if (!first.isPresent()) {
                reservation.cancel();
                reservationRepository.save(reservation);
            } else {
                Slot found = first.get();
                slots.remove(found);
                slots.addAll(found.splitBy(reservation.details().slot()));
            }
        }

        freeSlotRepository.saveAll(slots);


    }

    @Override
    protected void onAfterDelete(Schedule entity) {
        List<PatientReservation> reservations = reservationRepository.findByScheduleId(entity.toSchedule().id());
        for (PatientReservation reservation : reservations) {
            reservation.cancel();
            reservationRepository.save(reservation);
        }

    }
}

@Entity
class Schedule {
    @Id
    @GeneratedValue(generator = "auto")
    private Long id;
    private String visitDuration;
    private String workingHours;

    Schedule() {
    }

    public Schedule(DailyDoctorSchedule schedule) {
        this.id = schedule.id().id();
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
        return dailyDoctorSchedule(new ScheduleId(id), TimeInterval.parse(workingHours), Duration.parse(visitDuration));
    }
}
