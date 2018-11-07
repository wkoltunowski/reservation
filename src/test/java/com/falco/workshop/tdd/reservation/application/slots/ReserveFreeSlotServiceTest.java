package com.falco.workshop.tdd.reservation.application.slots;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlot;
import com.falco.workshop.tdd.reservation.infrastructure.slots.InMemoryFreeScheduleSlotRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.falco.workshop.tdd.reservation.domain.DateInterval.fromTo;
import static com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlot.freeScheduleSlot;
import static com.falco.workshop.tdd.reservation.domain.slots.VisitSlot.visitSlot;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ReserveFreeSlotServiceTest {

    public static final ScheduleId SCHEDULE_1 = ScheduleId.scheduleId(1);
    private ReserveFreeSlotService reserveService;
    private InMemoryFreeScheduleSlotRepository slotRepository;

    @Before
    public void setUp() {
        slotRepository = new InMemoryFreeScheduleSlotRepository();
        reserveService = new ReserveFreeSlotService(slotRepository);
    }

    @Test
    public void shouldReserveSlot() {
        given(freeScheduleSlot(SCHEDULE_1, fromTo("2018-01-01 08:00-16:00")));
        reserve(fromTo("2018-01-01 10:00-10:20"));
        assertThat(findScheduleSlots(SCHEDULE_1)).containsOnly(
                freeScheduleSlot(SCHEDULE_1, fromTo("2018-01-01 08:00-10:00")),
                freeScheduleSlot(SCHEDULE_1, fromTo("2018-01-01 10:20-16:00"))
        );
    }

    @Test
    public void shouldReserveAllViaOneSlot() {
        given(freeScheduleSlot(SCHEDULE_1, fromTo("2018-01-01 08:00-16:00")));
        reserve(fromTo("2018-01-01 08:00-16:00"));
        assertThat(findScheduleSlots(SCHEDULE_1)).isEmpty();
    }

    @Test
    public void shouldReserveAllViaManySlots() {
        given(freeScheduleSlot(SCHEDULE_1, fromTo("2018-01-01 08:00-16:00")));
        reserve(fromTo("2018-01-01 08:00-12:30"));
        reserve(fromTo("2018-01-01 12:30-14:00"));
        reserve(fromTo("2018-01-01 14:00-16:00"));
        assertThat(findScheduleSlots(SCHEDULE_1)).isEmpty();
    }

    @Test(expected = SlotTakenException.class)
    public void shouldFailReserveTwice() {
        given(freeScheduleSlot(SCHEDULE_1, fromTo("2018-01-01 08:00-16:00")));
        reserve(fromTo("2018-01-01 08:00-08:15"));
        reserve(fromTo("2018-01-01 08:00-08:15"));
    }

    @Test(expected = SlotTakenException.class)
    public void shouldFailReserveWhenNoSlot() {
        reserve(fromTo("2018-01-01 08:00-12:30"));
    }

    @Test(expected = SlotTakenException.class)
    public void shouldFailReserveWhenSlotBeyondAvailable() {
        given(freeScheduleSlot(SCHEDULE_1, fromTo("2018-01-01 08:00-16:00")));
        reserve(fromTo("2018-01-01 15:50-16:05"));
    }

    @Test(expected = SlotTakenException.class)
    public void shouldFailReserveWhenSlotBeforeAvailable() {
        given(freeScheduleSlot(SCHEDULE_1, fromTo("2018-01-01 08:00-16:00")));
        reserve(fromTo("2018-01-01 07:50-08:05"));
    }

    private List<FreeScheduleSlot> findScheduleSlots(ScheduleId id) {
        return slotRepository.findByScheduleId(id);
    }

    private void given(FreeScheduleSlot... freeSlots) {
        slotRepository.saveAll(asList(freeSlots));
    }

    private void reserve(DateInterval interval) {
        reserveService.reserveSlot(visitSlot(SCHEDULE_1, interval));
    }
}