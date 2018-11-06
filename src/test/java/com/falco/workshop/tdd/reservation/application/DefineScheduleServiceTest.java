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
import static com.falco.workshop.tdd.reservation.domain.schedule.Schedule.newSchedule;
import static com.falco.workshop.tdd.reservation.domain.schedule.Schedule.schedule;
import static com.falco.workshop.tdd.reservation.domain.schedule.ScheduleStatus.INITIAL;
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
        final InMemoryReservationRepository reservationRepository = new InMemoryReservationRepository();
        SlotReservationService slotReservationService = new SlotReservationService(slotRepository, scheduleRepository);
        defineScheduleService = new DefineScheduleService(
                scheduleRepository,
                new ScheduleEvents(
                        slotRepository,
                        new PatientReservationService(
                                reservationRepository,
                                slotReservationService
                        ),
                        slotReservationService
                )
        );
    }

    @Test
    public void defineScheduleShouldSaveSchedule() {
        ScheduleId id = given(newSchedule(fromTo("08:00-16:00"), ofMinutes(15)));
        assertThat(scheduleRepository.findById(id)).isEqualTo(schedule(id, fromTo("08:00-16:00"), ofMinutes(15), INITIAL));
    }

    @Test
    public void defineScheduleShouldGenerateFreeSlots() {
        ScheduleId id = given(newSchedule(fromTo("08:00-16:00"), ofMinutes(15)));
        assertThat(slotRepository.findByScheduleId(id)).containsExactly(freeScheduleSlot(id, DateInterval.fromTo("2018-09-02 08:00-16:00")));
    }

    @Test
    public void shouldUpdateSchedule() {
        ScheduleId id = given(newSchedule(fromTo("08:00-16:00"), ofMinutes(15)));
        defineScheduleService.updateSchedule(id, fromTo("08:00-10:00"), ofMinutes(15));
        assertThat(scheduleRepository.findById(id)).isEqualTo(schedule(id, fromTo("08:00-10:00"), ofMinutes(15), INITIAL));
    }

    @Test
    public void deleteScheduleShouldRemoveFreeSlots() {
        ScheduleId id = given(newSchedule(fromTo("08:00-16:00"), ofMinutes(15)));
        defineScheduleService.deleteSchedule(id);
        assertThat(slotRepository.findByScheduleId(id)).isEmpty();
    }

    private ScheduleId given(Schedule schedule) {
        return defineScheduleService.defineSchedule(schedule).id();
    }
}