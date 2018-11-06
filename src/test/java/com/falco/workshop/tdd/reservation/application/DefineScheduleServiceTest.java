package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.Schedule;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.infrastructure.reservation.InMemoryReservationRepository;
import com.falco.workshop.tdd.reservation.infrastructure.schedule.InMemoryScheduleRepository;
import com.falco.workshop.tdd.reservation.infrastructure.slots.InMemoryFreeScheduleSlotRepository;
import org.junit.Before;
import org.junit.Test;

import static com.falco.workshop.tdd.reservation.domain.TimeInterval.fromTo;
import static com.falco.workshop.tdd.reservation.domain.schedule.Schedule.schedule;
import static com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId.scheduleId;
import static com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlot.freeScheduleSlot;
import static java.time.Duration.ofMinutes;
import static org.assertj.core.api.Assertions.assertThat;

public class DefineScheduleServiceTest {
    private DefineScheduleService defineScheduleService;
    private InMemoryScheduleRepository scheduleRepository;
    private InMemoryFreeScheduleSlotRepository slotRepository;

    @Before
    public void setUp() {
        scheduleRepository = new InMemoryScheduleRepository();
        slotRepository = new InMemoryFreeScheduleSlotRepository();
        defineScheduleService = new DefineScheduleService(scheduleRepository, slotRepository, new InMemoryReservationRepository());
    }

    @Test
    public void defineScheduleShouldSaveSchedule() {
        ScheduleId id = given(schedule(fromTo("08:00-16:00"), ofMinutes(15)));
        assertThat(scheduleRepository.findById(id)).isEqualTo(schedule(scheduleId(1), fromTo("08:00-16:00"), ofMinutes(15)));
    }

    @Test
    public void defineScheduleShouldGenerateFreeSlots() {
        ScheduleId id = given(schedule(fromTo("08:00-16:00"), ofMinutes(15)));
        assertThat(slotRepository.findByScheduleId(id)).containsExactly(freeScheduleSlot(id, DateInterval.fromTo("2018-09-02 08:00-16:00")));
    }

    @Test
    public void updateSchedule() {
    }

    @Test
    public void deleteScheduleShouldRemoveFreeSlots() {
        ScheduleId id = given(schedule(fromTo("08:00-16:00"), ofMinutes(15)));
        defineScheduleService.deleteSchedule(id);
        assertThat(slotRepository.findByScheduleId(id)).isEmpty();
    }

    private ScheduleId given(Schedule schedule) {
        return defineScheduleService.defineSchedule(schedule).id();
    }
}