package com.falco.workshop.tdd.reservation.infrastructure;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.FreeSlotRepository;
import com.falco.workshop.tdd.reservation.domain.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

@Component
public class SpringFreeSlotRepository implements FreeSlotRepository {
    @Autowired
    private CrudFreeSlotRepository crudFreeSlotRepository;

    @Override
    public List<Slot> find(DateInterval interval) {
        return sequentialStream(crudFreeSlotRepository.findIntersecting(interval.start(), interval.end()))
                .map(FreeSlot::toSlot)
                .collect(toList());
    }

    private Stream<FreeSlot> sequentialStream(Iterable<FreeSlot> iterable) {
        return stream(iterable.spliterator(), false);
    }

    @Override
    public Slot findById(ScheduleId id, DateInterval interval) {
        return crudFreeSlotRepository.findIntersectingById(id.id(), interval.start(), interval.end()).toSlot();
    }

    @Override
    public void delete(Slot slot) {
        crudFreeSlotRepository.deleteById(new FreeSlot.Id(slot.id(), slot.interval().start()));
    }

    @Override
    public void saveAll(List<Slot> slots) {
        crudFreeSlotRepository.saveAll(slots.stream().map(FreeSlot::new).collect(toList()));
    }
}

@Entity
class FreeSlot {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Id freeSlotId;
    private LocalDateTime end;

    FreeSlot() {
    }

    FreeSlot(Slot slot) {
        this.freeSlotId = new Id(slot.id(), slot.interval().start());
        this.end = slot.interval().end();
    }

    public Slot toSlot() {
        return Slot.slot(new ScheduleId(freeSlotId.scheduleId), DateInterval.parse(freeSlotId.start, end));
    }

    @Embeddable
    static class Id implements Serializable {
        private Long scheduleId;
        private LocalDateTime start;

        Id() {
        }

        Id(ScheduleId scheduleId, LocalDateTime start) {
            this.scheduleId = scheduleId.id();
            this.start = start;
        }
    }
}

@Component
interface CrudFreeSlotRepository extends CrudRepository<FreeSlot, FreeSlot.Id> {
    @Query("SELECT fs FROM FreeSlot fs WHERE fs.freeSlotId.start <= :end and fs.end > :start")
    Iterable<FreeSlot> findIntersecting(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT fs FROM FreeSlot fs WHERE fs.freeSlotId.start <= :end and fs.end > :start and fs.freeSlotId.scheduleId = :scheduleId")
    FreeSlot findIntersectingById(@Param("scheduleId") Long scheduleId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
