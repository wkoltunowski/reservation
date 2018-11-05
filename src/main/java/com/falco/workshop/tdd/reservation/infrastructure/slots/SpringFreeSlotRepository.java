package com.falco.workshop.tdd.reservation.infrastructure.slots;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.FreeSlotRepository;
import com.falco.workshop.tdd.reservation.domain.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

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
    public List<Slot> find(DateInterval interval) {
        return sequentialStream(crud.findIntersecting(interval.start(), interval.end()))
                .map(SlotJS::toSlot)
                .collect(toList());
    }

    private Stream<SlotJS> sequentialStream(Iterable<SlotJS> iterable) {
        return stream(iterable.spliterator(), false);
    }

    @Override
    public Slot findById(ScheduleId id, DateInterval interval) {
        return crud.findIntersectingById(id.id(), interval.start(), interval.end()).toSlot();
    }

    @Override
    public void delete(Slot slot) {
        crud.deleteById(crud.findByScheduleIdStart(slot.id().id(), slot.interval().start()).id());
    }

    @Override
    public void saveAll(List<Slot> slots) {
        crud.saveAll(slots.stream().map(SlotJS::new).collect(toList()));
    }
}

@Component
interface CrudFreeSlotRepository extends CrudRepository<SlotJS, Long> {
    @Query("SELECT fs FROM SlotJS fs WHERE fs.start <= :end and fs.end > :start")
    Iterable<SlotJS> findIntersecting(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT fs FROM SlotJS fs WHERE fs.start <= :end and fs.end > :start and fs.scheduleId = :scheduleId")
    SlotJS findIntersectingById(@Param("scheduleId") Long scheduleId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT fs FROM SlotJS fs WHERE fs.scheduleId = :scheduleId and fs.start = :start")
    SlotJS findByScheduleIdStart(@Param("scheduleId") Long scheduleId, @Param("start") LocalDateTime start);
}
