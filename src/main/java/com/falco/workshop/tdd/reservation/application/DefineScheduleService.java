package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.*;
import com.falco.workshop.tdd.reservation.domain.reservation.PatientReservation;
import com.falco.workshop.tdd.reservation.domain.reservation.ReservationRepository;
import com.falco.workshop.tdd.reservation.domain.schedule.DailyDoctorSchedule;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleRepository;
import com.falco.workshop.tdd.reservation.domain.slots.FreeSlotRepository;
import com.falco.workshop.tdd.reservation.domain.slots.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Component
public class DefineScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private FreeSlotRepository freeSlotRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    public DailyDoctorSchedule defineSchedule(DailyDoctorSchedule schedule) {
        DailyDoctorSchedule dailyDoctorSchedule = this.scheduleRepository.save(schedule);
        generateSlots(dailyDoctorSchedule);
        return dailyDoctorSchedule;
    }

    private void generateSlots(DailyDoctorSchedule schedule) {
        LocalDateTime start = LocalDate.of(2018, 1, 1).atTime(0, 0);
        LocalDateTime end = LocalDate.of(2019, 1, 1).atTime(0, 0);
        this.freeSlotRepository.saveAll(schedule.generateSlots(DateInterval.parse(start, end)));
    }

    public void updateSchedule(DailyDoctorSchedule schedule) {
        List<PatientReservation> reservations = reservationRepository.findByScheduleId(schedule.id());

        for (Slot reservedSlot : reservations.stream().map(r -> r.details().slot()).collect(toList())) {
            freeSlotRepository.delete(reservedSlot);
        }

        List<Slot> slots = schedule.generateSlots(DateInterval.parse(LocalDateTime.now(), LocalDateTime.now().plusDays(365)));
        for (PatientReservation reservation : reservations) {
            Optional<Slot> first = slots.stream().filter(s -> s.interval().encloses(reservation.details().slot().interval())).findFirst();
            if (!first.isPresent()) {
                reservation.cancel();
                reservationRepository.save(reservation);
            } else {
                Slot found = first.get();
                slots.remove(found);
                slots.addAll(found.splitBy(reservation.details().slot()));
            }
        }

        freeSlotRepository.saveAll(slots);
    }

    public void deleteSchedule(DailyDoctorSchedule schedule) {
        List<PatientReservation> reservations = reservationRepository.findByScheduleId(schedule.id());
        for (PatientReservation reservation : reservations) {
            reservation.cancel();
            reservationRepository.save(reservation);
        }
    }
}
