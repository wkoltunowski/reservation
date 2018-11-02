package com.falco.workshop.tdd.reservation.infrastructure;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.FreeSlotRepository;
import com.falco.workshop.tdd.reservation.domain.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.Slot;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

//@Component
public class InMemoryFreeSlotRepository implements FreeSlotRepository {
    private List<Slot> slots = Lists.newArrayList();

    @Override
    public List<Slot> find(DateInterval interval) {
        return slots.stream().filter(s -> interval.intersects(s.interval())).collect(toList());
    }

    @Override
    public Slot findById(ScheduleId id, LocalDateTime start) {
        return slots.stream().filter(s -> s.id().equals(id) && s.interval().start().equals(start)).findFirst().get();
    }

    @Override
    public void delete(Slot slot) {
        slots.remove(findById(slot.id(), slot.interval().start()));
    }

    @Override
    public void saveAll(List<Slot> slots) {
        this.slots.addAll(slots);
    }
}
