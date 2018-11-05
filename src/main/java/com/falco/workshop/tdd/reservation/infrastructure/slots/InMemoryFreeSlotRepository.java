package com.falco.workshop.tdd.reservation.infrastructure.slots;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.slots.FreeSlot;
import com.falco.workshop.tdd.reservation.domain.slots.FreeSlotRepository;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class InMemoryFreeSlotRepository implements FreeSlotRepository {
    private List<FreeSlot> freeSlots = Lists.newArrayList();

    @Override
    public List<FreeSlot> find(DateInterval interval) {
        Predicate<FreeSlot> intersecting = intersecting(interval);
        return filterToList(intersecting);
    }

    @Override
    public List<FreeSlot> findById(ScheduleId id, DateInterval interval) {
        return filterToList(idEqTo(id).and(intersecting(interval)));
    }

    private Predicate<FreeSlot> intersecting(DateInterval interval) {
        return s -> interval.intersects(s.interval());
    }

    private Predicate<FreeSlot> idEqTo(ScheduleId id) {
        return s -> s.id().equals(id);
    }

    @Override
    public List<FreeSlot> findById(ScheduleId id) {
        return filterToList(idEqTo(id));
    }

    @Override
    public void delete(FreeSlot freeSlot) {
        freeSlots.removeAll(findById(freeSlot.id(), freeSlot.interval()));
    }

    private List<FreeSlot> filterToList(Predicate<FreeSlot> intersecting) {
        return freeSlots.stream().filter(intersecting).collect(toList());
    }

    @Override
    public void saveAll(List<FreeSlot> freeSlots) {
        this.freeSlots.addAll(freeSlots);
    }

    @Override
    public void deleteByScheduleId(ScheduleId id) {
        freeSlots.removeIf(s -> s.id().equals(id));
    }
}
