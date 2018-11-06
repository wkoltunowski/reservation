package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;

import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Lists.newArrayList;

public class ScheduleEventsMock implements ScheduleEvents {
    private final List<ScheduleId> cancelled = newArrayList();
    private final List<ScheduleId> defined = newArrayList();
    private final List<ScheduleId> updated = newArrayList();

    @Override
    public void scheduleDefined(ScheduleId id) {
        defined.add(id);
    }

    @Override
    public void scheduleUpdated(ScheduleId id) {
        updated.add(id);
    }

    @Override
    public void scheduleCancelled(ScheduleId id) {
        cancelled.add(id);
    }

    public List<ScheduleId> getCancelled() {
        return copyOf(cancelled);
    }

    public List<ScheduleId> getDefined() {
        return copyOf(defined);
    }

    public List<ScheduleId> getUpdated() {
        return copyOf(updated);
    }
}
