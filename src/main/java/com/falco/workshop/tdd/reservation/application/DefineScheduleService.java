package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation;
import com.falco.workshop.tdd.reservation.domain.reservation.ReservationRepository;
import com.falco.workshop.tdd.reservation.domain.schedule.DailyDoctorSchedule;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleRepository;
import com.falco.workshop.tdd.reservation.domain.slots.FreeSlot;
import com.falco.workshop.tdd.reservation.domain.slots.FreeSlotRepository;
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
    private final FreeSlotRepository freeSlotRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public DefineScheduleService(ScheduleRepository scheduleRepository, FreeSlotRepository freeSlotRepository, ReservationRepository reservationRepository) {
        this.scheduleRepository = scheduleRepository;
        this.freeSlotRepository = freeSlotRepository;
        this.reservationRepository = reservationRepository;
    }

    public DailyDoctorSchedule defineSchedule(DailyDoctorSchedule schedule) {
        DailyDoctorSchedule dailyDoctorSchedule = this.scheduleRepository.save(schedule);
        freeSlotRepository.saveAll(regenerate(dailyDoctorSchedule));
        return dailyDoctorSchedule;
    }

    private List<FreeSlot> regenerate(DailyDoctorSchedule schedule) {
        LocalDateTime start = LocalDate.of(2018, 1, 1).atTime(0, 0);
        LocalDateTime end = LocalDate.of(2019, 1, 1).atTime(0, 0);
        return schedule.generateSlots(fromTo(start, end));
    }

    public void updateSchedule(DailyDoctorSchedule schedule) {
        List<FreeSlot> freeSlots = regenerate(schedule);
        for (PatientReservation reservation : reservations(schedule.id())) {
            Optional<FreeSlot> newSlot = freeSlots.stream().filter(s -> s.interval().encloses(reservation.details().slot().interval())).findFirst();
            if (!newSlot.isPresent()) {
                reservation.cancel();
                reservationRepository.save(reservation);
            } else {
                FreeSlot found = newSlot.get();
                freeSlots.remove(found);
                freeSlots.addAll(found.splitBy(reservation.details().slot()));
            }
        }
        freeSlotRepository.deleteByScheduleId(schedule.id());
        freeSlotRepository.saveAll(freeSlots);
    }

    private List<PatientReservation> reservations(ScheduleId scheduleId) {
        return reservationRepository.findByScheduleId(scheduleId);
    }

    public void deleteSchedule(ScheduleId id) {
        for (PatientReservation reservation : reservations(id)) {
            reservation.cancel();
            reservationRepository.save(reservation);
        }
        freeSlotRepository.deleteByScheduleId(id);
    }
}
