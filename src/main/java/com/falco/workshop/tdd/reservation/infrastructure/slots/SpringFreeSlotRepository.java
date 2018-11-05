package com.falco.workshop.tdd.reservation.infrastructure.slots;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.slots.FreeSlot;
import com.falco.workshop.tdd.reservation.domain.slots.FreeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

@Component
public class SpringFreeSlotRepository implements FreeSlotRepository {
    @Autowired
    private CrudFreeSlotRepository crud;

    @Override
    public List<FreeSlot> find(DateInterval interval) {
        return sequentialStream(crud.findIntersecting(interval.start(), interval.end()))
                .map(FreeSlotEntity::toSlot)
                .collect(toList());
    }

    private Stream<FreeSlotEntity> sequentialStream(Iterable<FreeSlotEntity> iterable) {
        return stream(iterable.spliterator(), false);
    }

    @Override
    public List<FreeSlot> findById(ScheduleId id, DateInterval interval) {
        return sequentialStream(crud.findIntersectingById(id.id(), interval.start(), interval.end())).map(FreeSlotEntity::toSlot).collect(toList());
    }

    @Override
    public void delete(FreeSlot freeSlot) {
        crud.deleteById(crud.findByScheduleIdStart(freeSlot.id().id(), freeSlot.interval().start()).id());
    }

    @Override
    public void saveAll(List<FreeSlot> freeSlots) {
        crud.saveAll(freeSlots.stream().map(FreeSlotEntity::new).collect(toList()));
    }
}

@Component
interface CrudFreeSlotRepository extends CrudRepository<FreeSlotEntity, Long> {
    @Query("SELECT fs FROM FreeSlotEntity fs WHERE fs.start <= :end and fs.end > :start")
    Iterable<FreeSlotEntity> findIntersecting(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT fs FROM FreeSlotEntity fs WHERE fs.start <= :end and fs.end > :start and fs.scheduleId = :scheduleId")
    Iterable<FreeSlotEntity> findIntersectingById(@Param("scheduleId") Long scheduleId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT fs FROM FreeSlotEntity fs WHERE fs.scheduleId = :scheduleId and fs.start = :start")
    FreeSlotEntity findByScheduleIdStart(@Param("scheduleId") Long scheduleId, @Param("start") LocalDateTime start);
}

@Entity
class FreeSlotEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long scheduleId;
    private LocalDateTime start;
    private LocalDateTime end;


    FreeSlotEntity() {
    }

    public FreeSlotEntity(FreeSlot freeSlot) {
        this.scheduleId = freeSlot.id().id();
        this.start = freeSlot.interval().start();
        this.end = freeSlot.interval().end();
    }

    public Long getId() {
        return id;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public FreeSlot toSlot() {
        return FreeSlot.slot(new ScheduleId(scheduleId), DateInterval.fromTo(start, end));
    }

    public Long id() {
        return id;
    }
}
