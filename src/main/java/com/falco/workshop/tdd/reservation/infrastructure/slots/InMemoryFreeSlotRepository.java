package com.falco.workshop.tdd.reservation.infrastructure.slots;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.slots.FreeSlotRepository;
import com.falco.workshop.tdd.reservation.domain.slots.FreeSlot;
import com.google.common.collect.Lists;

import java.util.List;

import static java.util.stream.Collectors.toList;

//@Component
public class InMemoryFreeSlotRepository implements FreeSlotRepository {
    private List<FreeSlot> freeSlots = Lists.newArrayList();

    @Override
    public List<FreeSlot> find(DateInterval interval) {
        return freeSlots.stream().filter(s -> interval.intersects(s.interval())).collect(toList());
    }

    @Override
    public List<FreeSlot> findById(ScheduleId id, DateInterval interval) {
        return freeSlots.stream().filter(s -> s.id().equals(id) && s.interval().encloses(interval)).collect(toList());
    }

    @Override
    public void delete(FreeSlot freeSlot) {
        freeSlots.removeAll(findById(freeSlot.id(), freeSlot.interval()));
    }

    @Override
    public void saveAll(List<FreeSlot> freeSlots) {
        this.freeSlots.addAll(freeSlots);
    }
}
