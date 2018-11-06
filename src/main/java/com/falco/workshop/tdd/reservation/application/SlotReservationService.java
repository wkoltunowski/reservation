package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.reservation.PatientSlot;
import com.falco.workshop.tdd.reservation.domain.schedule.Schedule;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleRepository;
import com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlot;
import com.falco.workshop.tdd.reservation.domain.slots.FreeScheduleSlotRepository;
import com.falco.workshop.tdd.reservation.domain.slots.VisitSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.falco.workshop.tdd.reservation.domain.DateInterval.fromTo;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toList;

@Component
public class SlotReservationService {
    private final FreeScheduleSlotRepository freeScheduleSlotRepository;
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public SlotReservationService(FreeScheduleSlotRepository freeScheduleSlotRepository, ScheduleRepository scheduleRepository) {
        this.freeScheduleSlotRepository = freeScheduleSlotRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public void reserveSlot(VisitSlot scheduleSlot) {
        FreeScheduleSlot oldScheduleSlot = freeScheduleSlotRepository.findByScheduleIdEnclosing(scheduleSlot.id(), scheduleSlot.interval())
                .orElseThrow(() -> new IllegalArgumentException("ScheduleSlot already taken!"));
        checkArgument(oldScheduleSlot.interval().encloses(scheduleSlot.interval()), "ScheduleSlot already taken!");
        List<FreeScheduleSlot> newSlots = oldScheduleSlot.cutInterval(scheduleSlot.interval());
        freeScheduleSlotRepository.delete(oldScheduleSlot);
        freeScheduleSlotRepository.saveAll(newSlots);
    }


    public void regenerateSlots(ScheduleId id) {
        freeScheduleSlotRepository.deleteByScheduleId(id);
        freeScheduleSlotRepository.saveAll(regenerate(scheduleRepository.findById(id)));
    }

    private List<FreeScheduleSlot> regenerate(Schedule schedule) {
        return schedule.generateSlots(fromTo(LocalDate.of(2018, 1, 1).atTime(0, 0), LocalDate.of(2019, 1, 1).atTime(0, 0)));
    }


    public List<PatientSlot> reserveSlots(Collection<PatientSlot> patientSlots) {
        Map<VisitSlot, PatientSlot> map = new HashMap<>();
        for (PatientSlot patientSlot : patientSlots) {
            map.put(patientSlot.slot(), patientSlot);
        }
        return reserveVisitSlots(map.keySet()).stream().map(map::get).collect(toList());
    }

    private List<VisitSlot> reserveVisitSlots(Set<VisitSlot> visitSlots) {
        Map<ScheduleId, List<VisitSlot>> slotBySchedyle = visitSlots.stream().collect(Collectors.groupingBy(VisitSlot::id));
        List<VisitSlot> reservedSlots = new ArrayList<>();

        for (Map.Entry<ScheduleId, List<VisitSlot>> scheduleSlots : slotBySchedyle.entrySet()) {
            reservedSlots.addAll(reserveScheduleSlots(scheduleSlots.getKey(), scheduleSlots.getValue()));
        }
        return reservedSlots;
    }

    private List<VisitSlot> reserveScheduleSlots(ScheduleId id, List<VisitSlot> slots) {
        List<VisitSlot> reservedVisitSlots = new ArrayList<>();
        for (VisitSlot slot : slots) {
            Optional<FreeScheduleSlot> enclosingFreeSlot = freeScheduleSlotRepository.findByScheduleIdEnclosing(id, slot.interval());
            if (enclosingFreeSlot.isPresent()) {
                freeScheduleSlotRepository.delete(enclosingFreeSlot.get());
                freeScheduleSlotRepository.saveAll(enclosingFreeSlot.get().cutInterval(slot.interval()));
                reservedVisitSlots.add(slot);
            }
        }
        return reservedVisitSlots;
    }
}
