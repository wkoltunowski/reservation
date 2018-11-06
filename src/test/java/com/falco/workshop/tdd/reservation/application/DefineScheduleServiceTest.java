package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.schedule.Schedule;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.infrastructure.schedule.InMemoryScheduleRepository;
import org.junit.Before;
import org.junit.Test;

import static com.falco.workshop.tdd.reservation.domain.TimeInterval.fromTo;
import static com.falco.workshop.tdd.reservation.domain.schedule.Schedule.newSchedule;
import static com.falco.workshop.tdd.reservation.domain.schedule.Schedule.schedule;
import static com.falco.workshop.tdd.reservation.domain.schedule.ScheduleStatus.CANCELLED;
import static com.falco.workshop.tdd.reservation.domain.schedule.ScheduleStatus.INITIAL;
import static java.time.Duration.ofMinutes;
import static org.assertj.core.api.Assertions.assertThat;

public class DefineScheduleServiceTest {
    private DefineScheduleService defineScheduleService;
    private InMemoryScheduleRepository scheduleRepository;
    private ScheduleEventsMock scheduleEventsMock;

    @Before
    public void setUp() {
        scheduleRepository = new InMemoryScheduleRepository();
        scheduleEventsMock = new ScheduleEventsMock();
        defineScheduleService = new DefineScheduleService(scheduleRepository, scheduleEventsMock);
    }

    @Test
    public void shouldDefineSchedule() {
        ScheduleId id = given(newSchedule(fromTo("08:00-16:00"), ofMinutes(15)));
        assertThat(scheduleRepository.findById(id)).isEqualTo(schedule(id, fromTo("08:00-16:00"), ofMinutes(15), INITIAL));
        assertThat(scheduleEventsMock.getDefined()).containsExactly(id);
    }

    @Test
    public void shouldUpdateSchedule() {
        ScheduleId id = given(newSchedule(fromTo("08:00-16:00"), ofMinutes(15)));
        defineScheduleService.updateSchedule(id, fromTo("08:00-10:00"), ofMinutes(15));
        assertThat(scheduleRepository.findById(id)).isEqualTo(schedule(id, fromTo("08:00-10:00"), ofMinutes(15), INITIAL));
        assertThat(scheduleEventsMock.getUpdated()).containsExactly(id);
    }

    @Test
    public void shouldCancelSchedule() {
        ScheduleId id = given(newSchedule(fromTo("08:00-16:00"), ofMinutes(15)));
        defineScheduleService.deleteSchedule(id);
        assertThat(scheduleRepository.findById(id)).isEqualTo(schedule(id, fromTo("08:00-16:00"), ofMinutes(15), CANCELLED));
        assertThat(scheduleEventsMock.getCancelled()).containsExactly(id);
    }

    private ScheduleId given(Schedule schedule) {
        return defineScheduleService.defineSchedule(schedule).id();
    }
}