package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation;
import com.falco.workshop.tdd.reservation.domain.reservation.ReservationRepository;
import com.falco.workshop.tdd.reservation.domain.schedule.Schedule;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleRepository;
import com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlot;
import com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.falco.workshop.tdd.reservation.domain.DateInterval.fromTo;

@Component
public class DefineScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final FreeScheduleSlotRepository freeScheduleSlotRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public DefineScheduleService(ScheduleRepository scheduleRepository,
                                 FreeScheduleSlotRepository freeScheduleSlotRepository,
                                 ReservationRepository reservationRepository) {
        this.scheduleRepository = scheduleRepository;
        this.freeScheduleSlotRepository = freeScheduleSlotRepository;
        this.reservationRepository = reservationRepository;
    }

    public Schedule defineSchedule(Schedule schedule) {
        Schedule dailySchedule = this.scheduleRepository.save(schedule);
        freeScheduleSlotRepository.saveAll(regenerate(dailySchedule));
        return dailySchedule;
    }

    private List<FreeScheduleSlot> regenerate(Schedule schedule) {
        LocalDateTime start = LocalDate.of(2018, 1, 1).atTime(0, 0);
        LocalDateTime end = LocalDate.of(2019, 1, 1).atTime(0, 0);
        return schedule.generateSlots(fromTo(start, end));
    }

    public void updateSchedule(Schedule schedule) {
        List<FreeScheduleSlot> scheduleSlots = regenerate(schedule);
        for (PatientReservation reservation : reservations(schedule.id())) {
            Optional<FreeScheduleSlot> newSlot = scheduleSlots.stream().filter(s -> s.interval().encloses(reservation.details().slot().interval())).findFirst();
            if (!newSlot.isPresent()) {
                reservation.cancel();
                reservationRepository.save(reservation);
            } else {
                FreeScheduleSlot found = newSlot.get();
                scheduleSlots.remove(found);
                scheduleSlots.addAll(found.cutInterval(reservation.details().slot().interval()));
            }
        }
        freeScheduleSlotRepository.deleteByScheduleId(schedule.id());
        freeScheduleSlotRepository.saveAll(scheduleSlots);
    }

    private List<PatientReservation> reservations(ScheduleId scheduleId) {
        return reservationRepository.findByScheduleId(scheduleId);
    }

    public void deleteSchedule(ScheduleId id) {
        for (PatientReservation reservation : reservations(id)) {
            reservation.cancel();
            reservationRepository.save(reservation);
        }
        freeScheduleSlotRepository.deleteByScheduleId(id);
    }
}
