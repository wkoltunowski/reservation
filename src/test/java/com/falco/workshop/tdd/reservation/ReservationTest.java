package com.falco.workshop.tdd.reservation;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.falco.workshop.tdd.reservation.DailyDoctorSchedule.dailyDoctorSchedule;
import static java.time.Duration.ofMinutes;
import static org.assertj.core.api.Assertions.assertThat;

public class ReservationTest {
    private static final ScheduleId DOC_SMITH = new ScheduleId("dr. Smith, John");
    private DailyDoctorSchedule schedule;

    @Test
    public void shouldFindSlots() {
        given(dailyDoctorSchedule(DOC_SMITH, TimeInterval.parse("08:00-09:00"), ofMinutes(15)));
        assertThat(findFreeSlots("2018-09-02 09:00")).containsExactly(
                slot(DOC_SMITH, "2018-09-02 08:00-08:15"),
                slot(DOC_SMITH, "2018-09-02 08:15-08:30"),
                slot(DOC_SMITH, "2018-09-02 08:30-08:45"),
                slot(DOC_SMITH, "2018-09-02 08:45-09:00"));
    }

    @Test
    public void shouldReserveSlot() {
        given(dailyDoctorSchedule(DOC_SMITH, TimeInterval.parse("08:00-09:15"), ofMinutes(15)));
        reserveSlot(slot(DOC_SMITH, "2018-09-02 08:00-08:15"));
        assertThat(findFreeSlots("2018-09-02 09:00")).isEmpty();
    }

    private void reserveSlot(Slot slot) {
        schedule.reserveSlot(slot);
    }

    private Slot slot(ScheduleId scheduleId, String dayFromTo) {
        return Slot.slot(scheduleId, DateInterval.parse(dayFromTo));
    }

    private List<Slot> findFreeSlots(String startingFrom) {
        return schedule.findFreeSlots(LocalDateTime.parse(startingFrom.split(" ")[0] + "T" + startingFrom.split(" ")[1]));
    }

    private void given(DailyDoctorSchedule schedule) {
        this.schedule = schedule;
    }
}

