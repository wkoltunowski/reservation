package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.DailyDoctorSchedule;
import com.falco.workshop.tdd.reservation.domain.ScheduleRepository;
import com.falco.workshop.tdd.reservation.domain.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SlotReservationService {
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public SlotReservationService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public void reserveSlot(Slot slot) {
        DailyDoctorSchedule schedule = scheduleRepository.findById(slot.id());
        schedule.reserveSlot(slot);
        scheduleRepository.update(schedule);
    }
}
