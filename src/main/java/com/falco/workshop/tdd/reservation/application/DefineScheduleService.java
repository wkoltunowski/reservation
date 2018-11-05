package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
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
        this.freeSlotRepository.saveAll(schedule.generateSlots(DateInterval.fromTo(start, end)));
    }

    public void updateSchedule(DailyDoctorSchedule schedule) {
        List<PatientReservation> reservations = reservationRepository.findByScheduleId(schedule.id());

        for (FreeSlot reservedFreeSlot : reservations.stream().map(r -> r.details().slot()).collect(toList())) {
            freeSlotRepository.delete(reservedFreeSlot);
        }

        List<FreeSlot> freeSlots = schedule.generateSlots(DateInterval.fromTo(LocalDateTime.now(), LocalDateTime.now().plusDays(365)));
        for (PatientReservation reservation : reservations) {
            Optional<FreeSlot> first = freeSlots.stream().filter(s -> s.interval().encloses(reservation.details().slot().interval())).findFirst();
            if (!first.isPresent()) {
                reservation.cancel();
                reservationRepository.save(reservation);
            } else {
                FreeSlot found = first.get();
                freeSlots.remove(found);
                freeSlots.addAll(found.splitBy(reservation.details().slot()));
            }
        }

        freeSlotRepository.saveAll(freeSlots);
    }

    public void deleteSchedule(ScheduleId id) {
        for (PatientReservation reservation : reservationRepository.findByScheduleId(id)) {
            reservation.cancel();
            reservationRepository.save(reservation);
        }
        freeSlotRepository
                .findById(id, DateInterval.fromTo(LocalDateTime.parse("2000-01-01T00:00"), LocalDateTime.parse("2999-12-31T23:59")))
                .forEach(freeSlotRepository::delete);
    }
}
