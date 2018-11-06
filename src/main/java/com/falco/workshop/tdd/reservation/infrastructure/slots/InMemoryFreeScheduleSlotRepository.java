package com.falco.workshop.tdd.reservation.infrastructure.slots;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlot;
import com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlotRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Predicate;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

public class InMemoryFreeScheduleSlotRepository implements FreeScheduleSlotRepository {
    private List<FreeScheduleSlot> scheduleSlots = newArrayList();

    @Override
    public Page<FreeScheduleSlot> findIntersecting(DateInterval interval, Pageable pageable) {
        Predicate<FreeScheduleSlot> intersecting = intersecting(interval);
        List<FreeScheduleSlot> results = filterToList(intersecting);
        int from = ObjectUtils.min(results.size(), pageable.getPageNumber() * pageable.getPageSize());
        int to = ObjectUtils.min(results.size(), (pageable.getPageNumber() + 1) * pageable.getPageSize());
        return new PageImpl<>(results.subList(from, to), pageable, results.size());

    }

    @Override
    public List<FreeScheduleSlot> findByScheduleIdIntersecting(ScheduleId id, DateInterval interval) {
        return filterToList(eqTo(id).and(intersecting(interval)));
    }

    private Predicate<FreeScheduleSlot> intersecting(DateInterval interval) {
        return s -> interval.intersects(s.interval());
    }

    private Predicate<FreeScheduleSlot> eqTo(DateInterval interval) {
        return s -> interval.equals(s.interval());
    }

    private Predicate<FreeScheduleSlot> eqTo(ScheduleId id) {
        return s -> s.id().equals(id);
    }

    @Override
    public List<FreeScheduleSlot> findByScheduleId(ScheduleId id) {
        return filterToList(eqTo(id));
    }

    @Override
    public void delete(FreeScheduleSlot scheduleSlot) {
        scheduleSlots.removeAll(findByScheduleIdIntersecting(scheduleSlot.id(), scheduleSlot.interval()));
    }

    private List<FreeScheduleSlot> filterToList(Predicate<FreeScheduleSlot> intersecting) {
        return scheduleSlots.stream().filter(intersecting).collect(toList());
    }

    @Override
    public void saveAll(List<FreeScheduleSlot> scheduleSlots) {
        this.scheduleSlots.addAll(scheduleSlots);
    }

    @Override
    public void deleteByScheduleId(ScheduleId id) {
        scheduleSlots.removeIf(s -> s.id().equals(id));
    }
}
